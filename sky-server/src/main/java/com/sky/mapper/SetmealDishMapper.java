package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 套餐菜品映射
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询出套餐id, 可能查询出多个, 所以是多对多的数据关系, 这里使用List集合的方式进行存储
     * 动态sql最好是写在XML映射文件中而不是注解中, 因为注解中的sql语句比较简单, 如果使用注解的方式, 那么sql语句会比较长, 不利于阅读
     * 使用动态sql的时候在xml文件中是foreach标签
     *
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1, 2, 3) 因为可能有多个菜品id, 所以使用in关键字, 如果使用精确匹配=会出错
    List<Long> getSetmealIdsByDishIds(@Param("dishIds") List<Long> dishIds);

    /**
     * 根据菜品id查询套餐菜品引用个数
     *
     * @param ids
     * @return
     */
    Integer getCountByDishIds(List<Long> ids);

    /**
     * 批量保存套餐和菜品的关联关系
     *
     * @param setmealDishes
     */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     *
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据菜品id查询菜品对应套餐关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where dish_id = #{id}")
    SetmealDish getSetmealDishByDishId(Long id);
}
