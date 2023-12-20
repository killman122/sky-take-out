package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 通过直接将集合对象传入的方式向口味表中批量插入数据
     * 由于是批量插入数据, 不仅是大量数据, 而且是动态SQL, 所以需要使用xml映射的方式向数据库中插入数据, 对mapper进行映射处理
     * 在将动态SQL使用Mapper进行映射时, 由于是动态SQL所以需要将对应的动态集合或者数组传入到xml中, 这里使用的是List集合, 所以需要将List集合传入到xml中, 通过参数名进行调用, 在xml中通过foreach标签实现遍历
     * 需要将foreach标签中的collection属性设置为传入的参数名, 通过item属性设置遍历的元素, 通过index属性设置遍历的索引, 通过open属性设置遍历的开始标签, 通过close属性设置遍历的结束标签, 通过separator属性设置遍历的分隔符
     * @param flavors
     */
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);
}
