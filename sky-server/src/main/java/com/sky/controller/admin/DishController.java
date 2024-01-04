package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 清理指定key的缓存, 主要是抽取代码实现抽取的相关代码封装
     *
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {//使用DTO对象接收参数, 主要接收的是菜品的基本信息, 以及菜品的口味信息等
        log.info("新增菜品: {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存数据, 调用redisTemplate的delete方法, 删除redis中的数据
//        redisTemplate.delete("dish_" + dishDTO.getCategoryId());
        //清理缓存数据
        cleanCache("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }


    /**
     * 分页查询菜品
     * 如果请求的方式是Post类型请求, 请求的请求体Body使用json类型数据进行传输, 那么在调用的时候就需要在参数中加上Spring框架中的注解@RequstBody注解进行标注
     * 但是如果是使用Get的方式提交请求, 那么就不需要在参数中添加@RequestBody注解, 但是可能需要添加@RequestParam注解
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * 删除菜品中传入的参数, 按照接口文档是一个字符串, 但是在实际开发中, 一般都是一个数组, 数组中的元素是菜品的id
     * 如果按照文档使用string类型数据存储, 需要编写方法将每个字符之间做出间隔, 但是如果需要使用SpringMVC框架中的注解@RequestParam注解, 那么就需要将参数类型设置为数组类型或者是集合类型, 自动的将每个字符之间做出间隔
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除: {}", ids);
        dishService.deleteBatch(ids);

        //删除对应的缓存数据, 由于这里是通过单例集合的方式删除多个缓存数据, 可以遍历集合, 逐个删除, 也可以调用redis中的keys方法进行匹配后在将所有的缓存数据全部删除
        //将所有的菜品缓存数据全部清除, 也就是所有的以dish_开头的key, delete方法中支持的参数可以是一个集合, 所以在这里可以将集合当作参数传入到delete方法中
        //Set keys = redisTemplate.keys("dish_");
        //redisTemplate.delete(keys);

        //清理缓存数据
        cleanCache("dish_");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {//使用@PathVariable注解获取参数id并传入到对应的地址路径参数中
        log.info("根据id查询菜品: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 根据id修改菜品信息和对应的口味信息
     * 由于在修改菜品的时候可能还需要修改口味, 所以在这里调用service层的方法时需要调用修改菜品和口味的方法
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品: {}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将所有的菜品缓存数据全部清除, 也就是所有的以dish_开头的key, delete方法中支持的参数可以是一个集合, 所以在这里可以将集合当作参数传入到delete方法中
        /*Set keys = redisTemplate.keys("dish_");
        redisTemplate.delete(keys);*/

        //清理缓存数据
        cleanCache("dish_");

        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品起售停售: status={}, id={}", status, id);
        dishService.startOrStop(status, id);

        //清理缓存数据
        cleanCache("dish_");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }
}
