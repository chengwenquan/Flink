package org.cwq.flink.custom.deserialization;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.cwq.flink.bean.Student;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/3/26 17:27
 * @Version 1.0
 */
public class KafkaDeserialization implements DeserializationSchema<Student> {

    @Override
    public Student deserialize(byte[] message) throws IOException {
        String str = new String(message);
        Student stu = JSONObject.parseObject(str,Student.class);
        return stu;
    }

    @Override
    public boolean isEndOfStream(Student nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Student> getProducedType() {
        return TypeInformation.of(Student.class);
    }
}
