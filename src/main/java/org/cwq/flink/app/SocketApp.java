package org.cwq.flink.app;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/3/24 10:40
 * @Version 1.0
 */
public class SocketApp {
    public static void main(String[] args) throws Exception{
        //接收参数
//        ParameterTool parameterTool = ParameterTool.fromArgs(args);
//        String host = parameterTool.get("host");
//        int port = parameterTool.getInt("port");
        String host = "hadoop200";
        int port = 9999;
        //创建flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度
        env.setParallelism(1);

        //接收来自socket的数据
        DataStreamSource<String> ds = env.socketTextStream(host, port);

        ds.map(new MapFunction<String,Tuple2<String,Integer> >() {
            @Override
            public Tuple2<String,Integer> map(String value) throws Exception {
                return new Tuple2<String,Integer>(value,1);
            }
        })
                .keyBy(0)
                .sum(1)
                .print();

        env.execute("cwq202103241047");
    }
}
