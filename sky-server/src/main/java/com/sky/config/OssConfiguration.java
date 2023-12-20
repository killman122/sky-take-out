package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类, 用于配置阿里云OSS相关的属性, 创建AliOssUtil对象
 * 配置类交由Spring容器进行管理, 将配置类中的Bean对象交由Spring容器进行管理
 */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean//使用Bean注解, 表示这是一个Bean对象, 在项目启动时调用该方法创建对象,会被Spring容器所管理, 否则这个方法不会被调用
    @ConditionalOnMissingBean//条件Bean对象, 只有在没有这个Bean对象的时候才会创建Bean, 如果有则不会在创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("初始化AliOssUtil对象");
        log.info("开始创建阿里云文件上传工具类对象{}", aliOssProperties);
        AliOssUtil aliOssUtil = new AliOssUtil();
        //new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());
        BeanUtils.copyProperties(aliOssProperties, aliOssUtil);//这里我使用的是Spring框架中BeanUtils类中的copyProperties方法, 用于将一个对象中的属性值拷贝到另一个对象中, 所以需要将原来先的工具类中用注解创建无参构造器, 或者直接创建无参构造器; 也可以直接调用AliOssUtil类中的get方法获取属性后传入对象的构造器中
        return aliOssUtil;
    }
}
