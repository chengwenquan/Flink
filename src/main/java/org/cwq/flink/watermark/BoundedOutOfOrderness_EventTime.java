package org.cwq.flink.watermark;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.cwq.flink.bean.WaterSensor;

import java.time.Duration;

/**
 * @Description 乱序时间指定时间戳
 * @Author 承文全
 * @Date 2021/4/5 11:06
 * @Version 1.0
 */
public class BoundedOutOfOrderness_EventTime {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //flink1.12之后默认就是event Time不需要显示的设置
        //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<String> ds = env.socketTextStream("hadoop200", 9999);

        SingleOutputStreamOperator<WaterSensor> map = ds.map(e -> {
            String[] split = e.split(",");
            return new WaterSensor(split[0], Long.getLong(split[1]), Double.valueOf(split[2]));
        });

        //乱序
        map.assignTimestampsAndWatermarks(
                WatermarkStrategy.<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(5)).withTimestampAssigner(
                        new SerializableTimestampAssigner<WaterSensor>(){
                            @Override
                            public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                                return element.getTimestamp();
                            }
                        })
        );

        env.execute();
    }
}
