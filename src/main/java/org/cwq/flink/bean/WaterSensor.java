package org.cwq.flink.bean;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/3/12 13:37
 * @Version 1.0
 */
public class WaterSensor {
    private String mid;
    private Long timestamp;
    private Double temperature;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public WaterSensor(String mid, Long timestamp, Double temperature) {
        this.mid = mid;
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "WaterSensor{" +
                "mid='" + mid + '\'' +
                ", timestamp=" + timestamp +
                ", temperature=" + temperature +
                '}';
    }
}
