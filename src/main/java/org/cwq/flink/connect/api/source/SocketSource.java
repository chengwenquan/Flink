package org.cwq.flink.connect.api.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description SocketSource
 * @Author 承文全
 * @Date 2021/4/4 22:56
 * @Version 1.0
 */
public class SocketSource {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> localhost = env.socketTextStream("hadoop200", 9999);

        localhost.print();
        env.execute();
    }
}
