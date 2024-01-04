package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * 由于insert语句中的字段较多, 所以使用xml文件的方式进行配置, 不直接使用注解的方式
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     *
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
//添加自动填充的注解
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * 由于使用了分页插件, 所以需要动态将几个属性进行填写, 所以这是一个动态SQL需要使用使用xml配置文件的方式进行配置
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键id查询菜品
     * 由于查询语句较简单这里直接使用注解的方式进行查询
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据主键删除菜品数据
     *
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 修改菜品表中的基本信息
     * 通过传入的dish对象中的id属性进行修改
     * 动态的进行修改, 只有当dish对象中的属性有值, 并且属性的值不为null, 才能进行修改
     * 使用@AutoFill自定义注解填充公共属性, 但是之前的都是写在Controller层中的, 这里是写在Mapper层中的, 所以需要在自定义注解中添加一个属性, 用于标识是哪一层的注解
     *
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 动态条件查询菜品
     *
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long id);
}
