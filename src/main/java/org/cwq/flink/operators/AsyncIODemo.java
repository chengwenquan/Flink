package org.cwq.flink.operators;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.cwq.flink.bean.OrderWide;
import org.cwq.flink.util.ThreadPoolUtil;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description 使用异步io
 * @Author 承文全
 * @Date 2021/4/5 14:45
 * @Version 1.0
 */
public class AsyncIODemo {
    public static void main(String[] args) throws Exception{
        //TODO 环境相关配置
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> ds = env.socketTextStream("hadoop200", 9999);

        SingleOutputStreamOperator<OrderWide> map = ds.map(new MapFunction<String, OrderWide>() {
            @Override
            public OrderWide map(String value) throws Exception {
                //数据格式：1000001,1001,20.2|订单,用户id,消费总金额
                String[] split = value.split(",");
                OrderWide order = new OrderWide();
                order.setOrder_id(Long.parseLong(split[0]));
                order.setUser_id(Long.parseLong(split[1]));
                order.setTotal_amount(Double.valueOf(split[2]));
                return order;
            }
        });


        SingleOutputStreamOperator<OrderWide> result = AsyncDataStream.unorderedWait(map, new AsyncFindDim(), 60, TimeUnit.SECONDS);

        result.print();
        env.execute();
    }
}

class AsyncFindDim extends RichAsyncFunction<OrderWide,OrderWide> {

    //线程池对象的父接口
   private ExecutorService executorService;

    @Override
    public void open(Configuration parameters) throws Exception {
        //创建线程池对象
        executorService = ThreadPoolUtil.getInstance();
    }

    /**
     * 发送异步请求的方法
     * @param input 输入数据
     * @param resultFuture 异步处理结束之后，返回结果
     */
    @Override
    public void asyncInvoke(OrderWide input, ResultFuture<OrderWide> resultFuture) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Long user_id = input.getUser_id();//获取用户的id
                    //根据用户的id去redis中查询
                    //没有再去数据库中查询
                    //然后将查询得到的数据添加到redis中
                    String name = "";
                    if(user_id==1001){
                        name = "zhangsan";
                    }
                    input.setUser_name(name);
                    //将关联后的数据数据继续向下传递
                    resultFuture.complete(Arrays.asList(input));
                }catch(Exception e){
                    System.out.println("失败");
                }

            }
        });

    }
}
