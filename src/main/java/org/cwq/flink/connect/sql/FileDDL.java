package org.cwq.flink.connect.sql;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/4/5 18:06
 * @Version 1.0
 */
public class FileDDL {

    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        //创建表执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env,settings);

        //定义处理时间
        String sql = "CREATE TABLE orders( \n"+
                "  user_id INT,\n" +
                "  product STRING,\n" +
                "  amount INT,\n" +
                "  ts TIMESTAMP(3),\n" +
                "  pt AS PROCTIME()\n"+  //定义处理时间，pt为随意指定的
                ") WITH (\n"+
                "'connector.type' = 'filesystem',\n"+
                "'connector.path' = 'D:\\MyPro\\Flink\\Flink12\\src\\main\\resources\\a.txt',\n"+
                "'format.type'='csv'\n" +
                ")";

        //定义事件时间
        String sql1 = "CREATE TABLE orders( \n"+
                "  user_id INT,\n" +
                "  product STRING,\n" +
                "  amount INT,\n" +
                "  ts TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts - INTERVAL '3' SECOND\n" +  //将ts作为事件时间，延迟3s
                ") WITH (\n"+
                "'connector.type' = 'filesystem',\n"+
                "'connector.path' = 'D:\\MyPro\\Flink\\Flink12\\src\\main\\resources\\a.txt',\n"+
                "'format.type'='csv'\n" +
                ")";

        tableEnv.executeSql(sql);
        Table table = tableEnv.sqlQuery("select * from orders");
        DataStream<Row> rowDataStream = tableEnv.toAppendStream(table, Row.class);
        rowDataStream.print();




        env.execute();
    }
}
