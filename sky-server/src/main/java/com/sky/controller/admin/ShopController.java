package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController 用于标注控制层组件，说明这个类是控制层组件（注入服务）
 * @RequestMapping("/admin/shop") 用于标注控制层组件的请求地址
 * @Slf4j 用于标注日志对象
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    //自动装配redisTemple对象
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺的营业状态
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺的营业状态", notes = "0-关闭，1-开启")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺的营业状态为:{}", status == 1 ? "营业中" : "打烊中");

        //使用redisTemple对象的opsForValue()方法获取redis中的value对象, 然后使用set方法设置redis中的value对象的值
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

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
