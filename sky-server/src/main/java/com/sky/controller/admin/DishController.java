package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜品管理
 * 对应Controller层代码中需要使用@RestController注解进行标注, 并使用@RequestMapping注解进行路径映射
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    //注入菜品Service
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){//使用DTO对象接收参数, 主要接收的是菜品的基本信息, 以及菜品的口味信息等
        log.info("新增菜品: {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
}
