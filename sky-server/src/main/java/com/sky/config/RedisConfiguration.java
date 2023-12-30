package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
/**
 * @Configuration 用于标注配置类组件，说明这个类是配置类组件（注入服务）
 * @Slf4j 用于标注日志对象
 * RedisConfiguration类用于配置redisTemplate对象, 该对象是用于操作redis数据库的对象, 该对象由springboot自动创建
 * 可以在RedisConfiguration类中配置redisTemplate对象的序列化器, 该序列化器是用于设置redisTemplate对象的key的序列化器, 因为redisTemplate对象默认使用的是jdk序列化器, 所以在使用redisTemplate对象时, key和value都会出现乱码
 */
public class RedisConfiguration {

    //目标: 修改RedisTemplate对象默认使用的jdk序列化器为字符串序列化器

    /**
     * 使用bean注解注入RedisConnectionFactory redis连接工厂, 该对象由springboot自动创建
     * 实际上@Bean注解是用来声明redisTemplate方法返回的对象是一个Spring管理的bean
     * 注意序列化器的类型设置是在配置类中而不是在测试类或者是要使用的一般类中, 在一般类中编写的是传入的数据类型而不是序列化器的类型设置
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redisTemplate模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器, 否则key和value会出现乱码默认设置的是jdk序列化, 需要设置的是字符串类型的redis序列化器, 但是value不需要更改, 因为value中的读取是直接使用Java中的反序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
