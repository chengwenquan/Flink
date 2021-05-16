package org.cwq.flink.bean;

/**
 * @Description 订单宽表
 * @Author 承文全
 * @Date 2021/4/5 14:14
 * @Version 1.0
 */
public class OrderWide {
    private Long order_id;      //订单id
    private Long detail_id;     //明细id
    private Long province_id;   //产品id
    private String order_status;//订单状态
    private Long user_id;       //用户id
    private String user_name;   //用户名
    private String user_gender; //用户性别
    private String vip_level;   //级别
    private Double total_amount;//消费金额
    private Double sku_price;   //商品单价
    private Long sku_num ;      //商品数量
    private String sku_name;    //商品名
    private String create_time; //创建时间 yyyy-MM-dd HH:ss:mm
    private String create_date; //创建日期
    private Long create_ts;     //创建时间时间戳

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(Long detail_id) {
        this.detail_id = detail_id;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getVip_level() {
        return vip_level;
    }

    public void setVip_level(String vip_level) {
        this.vip_level = vip_level;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public Double getSku_price() {
        return sku_price;
    }

    public void setSku_price(Double sku_price) {
        this.sku_price = sku_price;
    }

    public Long getSku_num() {
        return sku_num;
    }

    public void setSku_num(Long sku_num) {
        this.sku_num = sku_num;
    }

    public String getSku_name() {
        return sku_name;
    }

    public void setSku_name(String sku_name) {
        this.sku_name = sku_name;
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

    public OrderWide() {
    }

    public OrderWide(OrderInfo order, OrderDetail detail) {
        this.order_id = order.getId();
        this.detail_id = detail.getId();
        this.province_id = order.getProvince_id();
        this.order_status = order.getOrder_status();
        this.user_id = order.getUser_id();
        this.total_amount = order.getTotal_amount();
        this.sku_price = detail.getSku_price();
        this.sku_num = detail.getSku_num();
        this.sku_name = detail.getSku_name();
        this.create_time = order.getCreate_time();
        this.create_date = order.getCreate_date();
        this.create_ts = order.getCreate_ts();
    }

    @Override
    public String toString() {
        return "OrderWide{" +
                "order_id=" + order_id +
                ", detail_id=" + detail_id +
                ", province_id=" + province_id +
                ", order_status='" + order_status + '\'' +
                ", user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", user_gender='" + user_gender + '\'' +
                ", vip_level='" + vip_level + '\'' +
                ", total_amount=" + total_amount +
                ", sku_price=" + sku_price +
                ", sku_num=" + sku_num +
                ", sku_name='" + sku_name + '\'' +
                ", create_time='" + create_time + '\'' +
                ", create_date='" + create_date + '\'' +
                ", create_ts=" + create_ts +
                '}';
    }
}
