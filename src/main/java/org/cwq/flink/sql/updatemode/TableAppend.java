package org.cwq.flink.sql.updatemode;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.cwq.flink.bean.Student;

import java.util.Arrays;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Description 更新模式：追加模式
 * @Author 承文全
 * @Date 2021/4/6 22:27
 * @Version 1.0
 */
public class TableAppend {

    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings setting = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env,setting);

        DataStreamSource<String> ds = env.socketTextStream("hadoop200", 9999);

        SingleOutputStreamOperator<Student> map = ds.map(e -> {
            String[] split = e.split(",");
            return new Student(Long.parseLong(split[0]), split[1], Integer.valueOf(split[2]));
        });

        tableEnv.createTemporaryView("student",map,$("id"),$("name"),$("age"));

        Table table = tableEnv.sqlQuery("select id,name,sum(age) age from student group by id,name");

        DataStream<Tuple2<Boolean, Student>> tuple2DataStream = tableEnv.toRetractStream(table, Student.class);
        tuple2DataStream.print();

        env.execute();

    }
}
