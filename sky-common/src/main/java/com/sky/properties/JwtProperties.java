package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * 指定配置的前缀, 这里的前缀就是sky.jwt, 这里的配置文件就是sky-server/src/main/resources/application.yml
 * 通过配置属性类的方式将SpringBoot配置文件中的配置项sky.jwt前缀的相关配置, 封装到这里的Java对象中, 这里的Java对象就是JwtProperties类的实例对象, 在将Java对象封装到EmployeeController类中
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
