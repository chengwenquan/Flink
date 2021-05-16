package org.cwq.flink.bean;

import java.math.BigDecimal;

/**
 * @Description 订单明细表
 * @Author 承文全
 * @Date 2021/4/5 11:53
 * @Version 1.0
 */
public class OrderDetail {
    private Long id;//订单明细id
    private Long order_id ;//订单id
    private Double sku_price ;//商品单价
    private Long sku_num ;//商品数量
    private String sku_name;//商品名
    private String create_time;//创建时间
    private Long create_ts;//创建时间时间戳

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
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

    public Long getCreate_ts() {
        return create_ts;
    }

    public void setCreate_ts(Long create_ts) {
        this.create_ts = create_ts;
    }
}
