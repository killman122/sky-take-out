package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 在设计DTO类的时候类中的请求参数也就是类中的成员变量和前端传递的参数一致, 依据前端传递的参数来定义类中的成员变量
 */
@Data
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
