package org.cwq.flink.bean;


/**
 * @Description 订单表
 * @Author 承文全
 * @Date 2021/4/5 11:52
 * @Version 1.0
 */
public class OrderInfo {
    private Long id;            //订单id
    private Long province_id;   //产品id
    private String order_status;//订单状态
    private Long user_id;       //用户id
    private Double total_amount;//消费金额
    private String create_time; //创建时间 yyyy-MM-dd HH:ss:mm
    private String create_date; //创建日期
    private Long create_ts;     //时间戳

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProvince_id() {
        return province_id;
    }

    public void setProvince_id(Long province_id) {
        this.province_id = province_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public Long getCreate_ts() {
        return create_ts;
    }

    public void setCreate_ts(Long create_ts) {
        this.create_ts = create_ts;
    }
}
