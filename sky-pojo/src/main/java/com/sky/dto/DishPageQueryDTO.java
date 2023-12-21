package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜品分页查询DTO
 * 这个类主要是用于接收前端页面传递过来的分页查询参数, 这里的参数主要有以下的五种, 但是只有page和pageSize是必须的, 其他的参数都是可选的
 */
@Data
public class DishPageQueryDTO implements Serializable {
    //页码
    private int page;

    //每页记录数
    private int pageSize;

    //菜品名称
    private String name;

    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
