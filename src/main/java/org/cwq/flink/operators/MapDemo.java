package org.cwq.flink.operators;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


/**
 * @Description 测试filter、flatmap、map、KeySelector、reduce
 * @Author 承文全
 * @Date 2021/4/4 23:01
 * @Version 1.0
 */
public class MapDemo {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> ds = env.socketTextStream("hadoop200", 9999);

        //测试filter算子
        ds.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return !"".equals(value.trim());
            }
        }).
        //测试FlatMap算子
        flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] split = value.split(",");
                for (String s : split) {
                    out.collect(s);
                }
            }
        })
        //测试map算子
        .map(new MapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                return new Tuple2<>(value,1);
            }
        })
        //测试keyBy算子，自定一key选择器
        .keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
           @Override
           public String getKey(Tuple2<String, Integer> value) throws Exception {
               return value.f0;
           }
        })
        //测试reduce算子
        .reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                return new Tuple2(value1.f0,value1.f1+value2.f1);
            }
        }).print();

        env.execute();
    }

}
