package org.cwq.flink.window.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.cwq.flink.bean.WaterSensor;

import java.time.Duration;

/**
 * @Description 滚动时间窗口
 * @Author 承文全
 * @Date 2021/4/5 0:02
 * @Version 1.0
 */
public class TumblingTimeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //flink1.12之后默认就是event Time不需要显示的设置
        //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<String> ds = env.socketTextStream("hadoop200", 9999);

        SingleOutputStreamOperator<WaterSensor> map = ds.map(e -> {
            String[] split = e.split(",");
            return new WaterSensor(split[0], Long.parseLong(split[1]), Double.valueOf(split[2]));
        });

        //乱序
        map.assignTimestampsAndWatermarks(
                WatermarkStrategy.<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(5)).withTimestampAssigner(
                        new SerializableTimestampAssigner<WaterSensor>(){
                            @Override
                            public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                                return element.getTimestamp()*1000;
                            }
                        })
        ).keyBy(e->e.getMid())
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .reduce(new ReduceFunction<WaterSensor>() {
                    @Override
                    public WaterSensor reduce(WaterSensor value1, WaterSensor value2) throws Exception {
                        if(value2.getTemperature()>value1.getTemperature()){
                            value1.setTemperature(value2.getTemperature());
                            value1.setTimestamp(value2.getTimestamp());
                        }
                        return value1;
                    }
                })
                .print();


        env.execute();
    }

}
