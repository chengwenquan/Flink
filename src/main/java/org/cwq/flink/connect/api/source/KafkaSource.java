package org.cwq.flink.connect.api.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.cwq.flink.custom.deserialization.KafkaDeserialization_kafka;

import java.util.Properties;

/**
 * @Description 连接 KAFKA
 * @Author 承文全
 * @Date 2021/3/26 16:59
 * @Version 1.0
 */
public class KafkaSource {
    public static void main(String args[]) throws Exception{
        //创建Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String topic = "ods_test";
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop200:9092");
        properties.setProperty("group.id", "consumer-group11");
        properties.setProperty("auto.offset.reset", "earliest");
        //TODO 自定义序列化器
        //DataStreamSource<Student> ds = env.addSource(new FlinkKafkaConsumer<>(topic, new KafkaDeserialization(), properties));
        DataStreamSource<String> ds = env.addSource(new FlinkKafkaConsumer<>(topic, new KafkaDeserialization_kafka(), properties));

        ds.print();
        env.execute("kafkaSource");


    }
}
