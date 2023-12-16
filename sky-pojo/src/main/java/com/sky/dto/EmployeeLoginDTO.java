package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工登录时传递的数据模型")//使用swagger的注解@ApiModel，表示这个类是一个swagger的配置类, 传入的参数description属性表示当前类的作用
public class EmployeeLoginDTO implements Serializable {

    @ApiModelProperty("用户名")//使用swagger的注解@ApiModelProperty，表示这个属性是一个swagger的配置属性, 传入的参数value属性表示当前属性的作用
    private String username;

    @ApiModelProperty("密码")
    private String password;

}
