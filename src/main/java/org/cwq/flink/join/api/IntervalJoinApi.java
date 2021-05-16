package org.cwq.flink.join.api;

import com.alibaba.fastjson.JSON;
import org.apache.avro.Schema;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Properties;
import org.cwq.flink.bean.*;
/**
 * @Description 订单和订单明细表进行关联
 * @Author 承文全
 * @Date 2021/4/5 11:33
 * @Version 1.0
 */
public class IntervalJoinApi {

    public static void main(String[] args) throws Exception{
        //TODO 环境相关配置
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);//kafka有四个分区，并行度设置和分区数据一样可以增大吞吐量

        //TODO kafka相关配置
        String kafka_server = "hadoop200:9092,hadoop201:9092,hadoop202:9092";
        String orderInfoSourceTopic = "dwd_order_info";//订单表
        String orderDetailSourceTopic = "dwd_order_detail";//订单明细表
        String groupId = "order_wide_group";//消费者组

        //TODO 获取数据
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_server);
        //获取订单信息
        FlinkKafkaConsumer<String> order_consumer = new FlinkKafkaConsumer<>(orderInfoSourceTopic, new SimpleStringSchema(), props);
        DataStreamSource<String> order = env.addSource(order_consumer);
        //获取订单明细
        FlinkKafkaConsumer<String> order_detail_consumer = new FlinkKafkaConsumer<>(orderDetailSourceTopic, new SimpleStringSchema(), props);
        DataStreamSource<String> detail = env.addSource(order_detail_consumer);

        //TODO 转换结构
        //转换订单数据结构
        SingleOutputStreamOperator<OrderInfo> orderInfoMapDS = order.map(
                new RichMapFunction<String, OrderInfo>() {
                    SimpleDateFormat sdf = null;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }

                    @Override
                    public OrderInfo map(String jsonStr) throws Exception {
                        OrderInfo orderInfo = JSON.parseObject(jsonStr, OrderInfo.class);
                        orderInfo.setCreate_ts(sdf.parse(orderInfo.getCreate_time()).getTime());
                        return orderInfo;
                    }
                }
        );

        //转换订单明细数据结构
        SingleOutputStreamOperator<OrderDetail> orderDetailMapDS = detail.map(
                new RichMapFunction<String, OrderDetail>() {
                    SimpleDateFormat sdf = null;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }

                    @Override
                    public OrderDetail map(String jsonStr) throws Exception {
                        OrderDetail orderDetail = JSON.parseObject(jsonStr, OrderDetail.class);
                        orderDetail.setCreate_ts(sdf.parse(orderDetail.getCreate_time()).getTime());
                        return orderDetail;
                    }
                }
        );

        //TODO 指定事件时间
        SingleOutputStreamOperator<OrderInfo> orderInfoDS = orderInfoMapDS.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<OrderInfo>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                        .withTimestampAssigner(new SerializableTimestampAssigner<OrderInfo>() {
                            @Override
                            public long extractTimestamp(OrderInfo element, long recordTimestamp) {
                                return element.getCreate_ts() * 1000;
                            }
                        })
        );


        SingleOutputStreamOperator<OrderDetail> orderDetailDS = orderDetailMapDS.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<OrderDetail>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                        .withTimestampAssigner(new SerializableTimestampAssigner<OrderDetail>() {
                            @Override
                            public long extractTimestamp(OrderDetail element, long recordTimestamp) {
                                return element.getCreate_ts() * 1000;
                            }
                        }));


        //TODO 订单和订单明细做关联 根据key进行关联
        KeyedStream<OrderInfo, Long> orderInfoKeyedDS = orderInfoDS.keyBy(OrderInfo::getId);
        KeyedStream<OrderDetail, Long> orderDetailKeyedDS = orderDetailDS.keyBy(OrderDetail::getOrder_id);

        SingleOutputStreamOperator<OrderWide> orderWideDS = orderInfoKeyedDS
                .intervalJoin(orderDetailKeyedDS)
                .between(Time.milliseconds(-5), Time.milliseconds(5))
                .process(new ProcessJoinFunction<OrderInfo, OrderDetail, OrderWide>() {
                    @Override
                    public void processElement(OrderInfo left, OrderDetail right, Context ctx, Collector<OrderWide> out) throws Exception {
                        out.collect(new OrderWide(left, right));
                    }
                });


        orderWideDS.print();
        env.execute();
    }
}
