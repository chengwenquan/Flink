package org.cwq.flink.bean;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author 承文全
 * @Date 2021/3/26 17:36
 * @Version 1.0
 */
public class Student implements Serializable {
    public Student() {
    }

    public Student(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    private Long id;
    private String name;
    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id  +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
