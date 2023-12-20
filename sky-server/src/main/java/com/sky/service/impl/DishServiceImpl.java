package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    //自动注入mapper
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应的口味
     * 由于在重写的方法中可能涉及到两张数据库中表的操作, 所以需要使用@Transactional注解进行事务管理
     * 可能需要向菜品表和口味表中都写入数据, 如果其中一张表写入失败, 则需要回滚事务
     * 如果需要事务相关的注解生效, 需要在项目的启动类中使用@EnableTransactionManagement 开启注解方式的事务管理
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
        if (flavors!=null && flavors.size()>0) {//说明口味数据不为空,单例集合中的size()方法获取集合中的数据元素个数也就是长度
            //方式1:通过遍历集合的方式向口味表中插入数据
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);//设置菜品id
//                dishFlavorMapper.insertBatch(flavor);//向口味表中插入数据, 可以选择一次加入, 但是需要在mapper中写出单个插入的方法
            }

            //方式2:通过直接将集合对象传入的方式向口味表中批量插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
