package org.cwq.flink.connect.api.sink;

import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description 自定义Sink
 * @Author 承文全
 * @Date 2021/4/7 9:24
 * @Version 1.0
 */
public class CustomSink implements SinkFunction<String> { //输入数据为String

    public static void main(String[] args) {

    }
}
