package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 分页查询菜品
     * 如果请求的方式是Post类型请求, 请求的请求体Body使用json类型数据进行传输, 那么在调用的时候就需要在参数中加上Spring框架中的注解@RequstBody注解进行标注
     * 但是如果是使用Get的方式提交请求, 那么就不需要在参数中添加@RequestBody注解, 但是可能需要添加@RequestParam注解
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * 删除菜品中传入的参数, 按照接口文档是一个字符串, 但是在实际开发中, 一般都是一个数组, 数组中的元素是菜品的id
     * 如果按照文档使用string类型数据存储, 需要编写方法将每个字符之间做出间隔, 但是如果需要使用SpringMVC框架中的注解@RequestParam注解, 那么就需要将参数类型设置为数组类型或者是集合类型, 自动的将每个字符之间做出间隔
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除: {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }
}
