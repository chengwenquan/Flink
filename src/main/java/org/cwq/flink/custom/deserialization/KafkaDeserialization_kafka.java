package org.cwq.flink.custom.deserialization;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.Charset;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/3/26 18:42
 * @Version 1.0
 */
public class KafkaDeserialization_kafka implements KafkaDeserializationSchema<String> {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    @Override
    public boolean isEndOfStream(String nextElement) {
        return false;
    }

    @Override
    public String deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
        long offset = record.offset();//获取offset
        int partition = record.partition();
        String value = new String(record.value(),UTF_8);
        String topic = record.topic();

        String str = topic + " | " + partition + " | " + offset + " | " + value;
        return str;
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }
}
