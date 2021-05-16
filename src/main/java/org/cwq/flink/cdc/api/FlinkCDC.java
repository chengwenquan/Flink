package org.cwq.flink.cdc.api;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Properties;

/**
 * @Description 使用Flink api的方式测试CDC
 * @Author 承文全
 * @Date 2021/3/15 16:32
 * @Version 1.0
 */
public class FlinkCDC {
    public static void main(String[] args) throws Exception{
        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

//        //2.设置checkpoint相关(本地为local模式，local不支持从checkpoint恢复数据，集群才可以)
//        //2.Flink-CDC将读取binlog的位置信息以状态的方式保存在CK,如果想要做到断点续传,需要从Checkpoint或者Savepoint启动程序
//        //2.1 开启Checkpoint,每隔5秒钟做一次CK
//        env.enableCheckpointing(5000L);
//        //2.2 指定CK的一致性语义
//        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        //2.3 设置任务关闭的时候保留最后一次CK数据
//        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        //2.4 指定从CK自动重启策略（默认限重启）
//        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000L));
//        //2.5 设置状态后端
//        env.setStateBackend(new FsStateBackend("hdfs://hadoop200:8020/flinkCDC"));
//        //2.6 设置访问HDFS的用户名
//        System.setProperty("HADOOP_USER_NAME", "cwq");

        //3.创建Flink MySQL CDC的Source
        Properties properties = new Properties();
        //initial (default): 先数据库快照，再binlog实时采集,
        //latest-offset: 直接从binlog最新的位置同步
        //timestamp: 从binlog中根据指定的时间戳处恢复
        //specific-offset: 从binlog中根据指定的offset处恢复
        properties.setProperty("scan.startup.mode", "initial");

        DebeziumSourceFunction<String> mysqlSource = MySQLSource
                .<String>builder()
                .hostname("hadoop200")
                .port(3306)
                .username("cwq")
                .password("123456")
                .databaseList("gmall")
                .tableList("gmall.test_stu") //可选配置项,如果不指定该参数,则会读取上一个配置下的所有表的数据
                .debeziumProperties(properties)
                .deserializer(new StringDebeziumDeserializationSchema())
                .build();

        //4.使用cdc从mysql读取数据
        DataStreamSource<String> ds = env.addSource(mysqlSource);
        //5.打印
        ds.print();
        //6.执行任务
        env.execute();

    }
}
