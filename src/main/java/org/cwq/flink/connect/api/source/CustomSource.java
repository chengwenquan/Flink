package org.cwq.flink.connect.api.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.cwq.flink.bean.Student;

import java.util.Random;

/**
 * @Description 测试自定义Source
 * @Author 承文全
 * @Date 2021/4/4 22:44
 * @Version 1.0
 */
public class CustomSource {
    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Student> ds = env.addSource(new MyCustomSource());

        ds.print();
        env.execute();
    }
}

/**
 * 自定义Source
 */
class MyCustomSource implements SourceFunction<Student>{

    Boolean bool=true;

    @Override
    public void run(SourceContext<Student> ctx) throws Exception {
        Random round = new Random();
        while (bool){
            Long id = round.nextLong();
            long num = id % 100;
            int age = round.nextInt(100);
            ctx.collect(new Student(id,"admin"+num,age));
        }

    }

    @Override
    public void cancel() {
        bool=false;
    }
}