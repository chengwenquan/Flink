package org.cwq.flink.cdc.sql;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
/**
 * @Description 使用 flink SQL对CDC进行测试
 * @Author 承文全
 * @Date 2021/3/15 17:55
 * @Version 1.0
 */
public class FlinkCDC {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2.创建Flink-MySQL-CDC的Source
        tableEnv.executeSql("CREATE TABLE student (\n" +
                "  id INT NOT NULL,\n" +
                "  name STRING,\n" +
                "  age INT,\n" +
                "  classId INT\n" +
                ") WITH (\n" +
                "  'connector' = 'mysql-cdc',\n" +
                "  'hostname' = 'hadoop200',\n" +
                "  'port' = '3306',\n" +
                "  'username' = 'cwq',\n" +
                "  'password' = '123456',\n" +
                "  'database-name' = 'gmall',\n" +
                "  'table-name' = 'test_stu'\n" +
                ")");

        Table tableResult = tableEnv.sqlQuery("select * from student");
        tableEnv.toRetractStream(tableResult, Row.class).print();
        env.execute();
    }
}
