package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详情数据
     *
     * @param orderDetailList
     */
    void insertBatch(@Param("orderDetailList") List<OrderDetail> orderDetailList);
}
