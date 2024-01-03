package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    //自动注入mapper
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     * 由于在重写的方法中可能涉及到两张数据库中表的操作, 所以需要使用@Transactional注解进行事务管理
     * 可能需要向菜品表和口味表中都写入数据, 如果其中一张表写入失败, 则需要回滚事务
     * 如果需要事务相关的注解生效, 需要在项目的启动类中使用@EnableTransactionManagement 开启注解方式的事务管理
     *
     * @param dishDTO
     * @return
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();

        //属性拷贝将DTO对象中的属性拷贝到实体类对象中, 注意只有两个类中的属性名称一致的情况下才可以进行拷贝, 否则无法拷贝
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表中插入一条数据, 由于已经使用了自动填充公共属性, 所以不需要在service层实现类中进行设置set方法设置属性值
        dishMapper.insert(dish);

        //获取到Insert语句生成的主键值dishId菜品id
        Long dishId = dish.getId();

        //向口味表中插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();//获取DTO中的口味集合中的数据, 使用lombok中的@Data注解可以自动生成get和set方法, 从而可以直接使用get方法获取属性值
        if (flavors != null && flavors.size() > 0) {//说明口味数据不为空,单例集合中的size()方法获取集合中的数据元素个数也就是长度
            //方式1:通过遍历集合的方式向口味表中插入数据
            for (DishFlavor flavor : flavors) {//这里也可以使用foreach循环, 但是需要注意的是, 如果使用foreach循环, 则需要在循环体中进行设置菜品id, 否则会出现空指针异常
                flavor.setDishId(dishId);//设置菜品id
//                dishFlavorMapper.insertBatch(flavor);//向口味表中插入数据, 可以选择一次加入, 但是需要在mapper中写出单个插入的方法
            }

            //方式2:通过直接将集合对象传入的方式向口味表中批量插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * 基于mybatis的分页插件PageHelper
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());//参数1是total, 参数2是records
    }

    /**
     * 菜品批量删除
     * 使用事务注解@Transactional进行事务管理, 如果有对于多个数据表的操作, 需要保证事务的一致性
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除--是否存在起售中的菜品?
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品正在起售中不能删除, 直接使用业务异常的方式进行抛出
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否被套餐关联了, 如果被套餐关联了, 则不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //说明当前菜品被套餐关联了, 不能删除, 直接使用业务异常的方式进行抛出
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //如果是可以删除, 删除菜品表中的菜品数据, 删除菜品对应的口味数据, 根据菜品表中的id主键
        //遍历集合中的id, 逐个进行删除
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //无论是否存在口味数据, 都可以调用删除, 如果不存在口味数据, 则不会删除任何数据, 根据菜品id作为条件
            dishFlavorMapper.deleteByDishId(id);
        }

    }

    /**
     * 根据id查询菜品和对应的口味数据
     * 先查询菜品表获取所有的菜品属性并封装到VO对象中, 再查询口味表获取所有的口味属性并封装到VO对象中, 最后将两个VO对象进行合并
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装到VO对象中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);//将菜品表中的数据拷贝到VO对象中
        dishVO.setFlavors(dishFlavors);//将口味表中的数据封装到VO对象中

        return dishVO;
    }

    /**
     * 根据id修改菜品信息和对应的口味
     * 在修改的时候需要对菜品表和口味表两张表中的数据进行修改
     * 对于口味的修改比较麻烦, 但是可以将原先的口味进行删除, 再将前端传输过来新的口味进行添加, 统一进行口味的处理
     *
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品表中的基本信息(不包括口味), 需要传入基本的菜品对象dish, 如果是将dishDTO进行传入则是将所有的属性都传入, 但是这里只需要传入部分属性, 所以需要将dishDTO中的部分属性拷贝到dish对象中
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新批量插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {//说明口味数据不为空,单例集合中的size()方法获取集合中的数据元素个数也就是长度
            //通过foreach遍历集合的方式向口味表中插入数据
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());//设置菜品id
            });
            //通过直接将集合对象传入的方式向口味表中批量插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
