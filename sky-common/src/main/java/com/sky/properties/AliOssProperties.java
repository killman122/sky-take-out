package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置文件中的阿里云OSS相关的属性类
 * 作用: 读取配置文件yml中的配置项并封装成一个Java对象
 */
@Component
@ConfigurationProperties(prefix = "sky.alioss")//这里的前缀名和在yml配置文件中的一致
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
