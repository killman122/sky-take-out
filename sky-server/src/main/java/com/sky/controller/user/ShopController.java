package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController 用于标注控制层组件，说明这个类是控制层组件（注入服务）
 * @RequestMapping("/user/shop") 用于标注控制层组件的请求地址
 * @Slf4j 用于标注日志对象
 * 由于在项目中存在两个同名的类, 所以在Spring自动创建bean对象的时候, 会出现冲突, 所以需要在其中一个类中使用@Primary注解, 该注解用于标注该类是首选的bean对象
 * 或者是将其中一个类的bean对象的名称修改为不同的名称, 然后在使用的时候使用@Qualifier注解标注使用的是哪一个bean对象
 * 或者是在@RestController注解中使用value属性指定bean对象的名称
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    //使用常量标注redis中的key名称
    public static final String KEY = "SHOP_STATUS";

    //自动装配redisTemple对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺的营业状态
     * 在这里使用泛型限定获取的状态是Integer整型
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺的营业状态", notes = "0-关闭，1-开启")
    public Result<Integer> getStatus() {
        //使用redisTemple对象的opsForValue()方法获取redis中的value对象, 然后使用get方法获取redis中的value对象的值
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态为:{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
