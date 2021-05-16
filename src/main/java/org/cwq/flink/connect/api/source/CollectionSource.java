package org.cwq.flink.connect.api.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.cwq.flink.bean.Student;

import java.util.Arrays;

/**
 * @Description 从集合读取数据
 * @Author 承文全
 * @Date 2021/4/4 22:22
 * @Version 1.0
 */
public class CollectionSource {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tenv = StreamTableEnvironment.create(env,settings);

        DataStreamSource<Student> studentDataStreamSource = env.fromCollection(
                Arrays.asList(
                        new Student(1001l, "zs", 12),
                        new Student(1002l, "ls", 14),
                        new Student(1003l, "ww", 18)
                )
        );

        Table table = tenv.fromDataStream(studentDataStreamSource);
        tenv.createTemporaryView("student",table);

        Table table1 = tenv.sqlQuery("select * from student");
        tenv.toRetractStream(table1, Row.class).print();

        env.execute("");
    }
}
