package org.cwq.flink.cdc.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import java.util.Properties;

/**
 * @Description 自定义反序列化器
 * @Author 承文全
 * @Date 2021/3/24 17:54
 * @Version 1.0
 */
public class FlinkCDCWithCustomerSchema {

    public static void main(String[] args) throws Exception {
        //1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //2.创建Flink-MySQL-CDC的Source
        Properties properties = new Properties();

        //initial (default): Performs an initial snapshot on the monitored database tables upon first startup, and continue to read the latest binlog.
        //latest-offset: Never to perform snapshot on the monitored database tables upon first startup, just read from the end of the binlog which means only have the changes since the connector was started.
        //timestamp: Never to perform snapshot on the monitored database tables upon first startup, and directly read binlog from the specified timestamp. The consumer will traverse the binlog from the beginning and ignore change events whose timestamp is smaller than the specified timestamp.
        //specific-offset: Never to perform snapshot on the monitored database tables upon first startup, and directly read binlog from the specified offset.
        properties.setProperty("debezium.snapshot.mode", "initial");
        DebeziumSourceFunction<String> mysqlSource = MySQLSource
                .<String>builder()
                .hostname("hadoop200")
                .port(3306)
                .username("cwq")
                .password("123456")
                .databaseList("gmall")
                .tableList("gmall.test_stu") //可选配置项,如果不指定该参数,则会读取上一个配置下的所有表的数据
                .debeziumProperties(properties)
                .deserializer(new DebeziumDeserializationSchema<String>() {  //自定义数据解析器
                    /**
                     * 对传入的数据进行反序列化
                     * @param sourceRecord 输入数据
                     * @param collector 收集输出数据
                     * @throws Exception
                     */
                    @Override
                    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
                        /**
                         * 数据格式：
                         * SourceRecord {
                         * 	sourcePartition = {
                         * 		server = mysql_binlog_source
                         *        }, sourceOffset = {
                         * 		file = mysql - bin .000143,
                         * 		pos = 120,
                         * 		row = 1,
                         * 		snapshot = true
                         *    }
                         * }
                         * ConnectRecord {
                         * 	topic = 'mysql_binlog_source.gmall.test_stu', kafkaPartition = null, key = Struct {
                         * 		id = 1
                         *    }, keySchema = Schema {
                         * 		mysql_binlog_source.gmall.test_stu.Key: STRUCT
                         *    }, value = Struct {
                         * 		after = Struct {
                         * 			id = 1, name = zs, age = 18, classId = 1
                         *        }, source = Struct {
                         * 			version = 1.2 .1.Final, connector = mysql, name = mysql_binlog_source, ts_ms = 0, snapshot = true, db = gmall, table = test_stu, server_id = 0, file = mysql - bin .000143, pos = 120, row = 0
                         *        }, op = c, ts_ms = 1616580493863
                         *    }, valueSchema = Schema {
                         * 		mysql_binlog_source.gmall.test_stu.Envelope: STRUCT
                         *    }, timestamp = null, headers = ConnectHeaders(headers = )
                         * }
                         */
                        //SourceRecord{sourcePartition={server=mysql_binlog_source}, sourceOffset={file=mysql-bin.000143, pos=120, row=1, snapshot=true}} ConnectRecord{topic='mysql_binlog_source.gmall.test_stu', kafkaPartition=null, key=Struct{id=1}, keySchema=Schema{mysql_binlog_source.gmall.test_stu.Key:STRUCT}, value=Struct{after=Struct{id=1,name=zs,age=18,classId=1},source=Struct{version=1.2.1.Final,connector=mysql,name=mysql_binlog_source,ts_ms=0,snapshot=true,db=gmall,table=test_stu,server_id=0,file=mysql-bin.000143,pos=120,row=0},op=c,ts_ms=1616580493863}, valueSchema=Schema{mysql_binlog_source.gmall.test_stu.Envelope:STRUCT}, timestamp=null, headers=ConnectHeaders(headers=)}
                        //获取主题信息,包含着数据库和表名  gmall.test_stu
                        String topic = sourceRecord.topic();
                        String[] arr = topic.split("\\.");
                        String db = arr[1];
                        String tableName = arr[2];

                        //获取操作类型 READ DELETE UPDATE CREATE
                        Envelope.Operation operation = Envelope.operationFor(sourceRecord);

                        //获取值信息并转换为Struct类型
                        Struct value = (Struct) sourceRecord.value();

                        //获取变化后的数据
                        Struct after = value.getStruct("after");

                        //创建JSON对象用于存储数据信息
                        JSONObject data = new JSONObject();
                        for (Field field : after.schema().fields()) {
                            Object o = after.get(field);
                            data.put(field.name(), o);
                        }

                        //创建JSON对象用于封装最终返回值数据信息
                        JSONObject result = new JSONObject();
                        result.put("operation", operation.toString().toLowerCase());
                        result.put("data", data);
                        result.put("database", db);
                        result.put("table", tableName);

                        //发送数据至下游
                        collector.collect(result.toJSONString());
                    }

                    /**
                     * 返回值类型
                     * @return
                     */
                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                })
                .build();
        //3.使用CDC Source从MySQL读取数据
        DataStreamSource<String> mysqlDS = env.addSource(mysqlSource);
        //4.打印数据
        mysqlDS.print();
        //5.执行任务
        env.execute();
    }
}
