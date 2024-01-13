package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     *
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Orders getByNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单id查询订单对象
     *
     * @param id
     * @return
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 添加订单
     *
     * @param orders
     */
    @Insert(" insert into orders\n" +
            "        (number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark,\n" +
            "         phone, address, consignee, estimated_delivery_time, delivery_status, pack_amount, tableware_number,\n" +
            "         tableware_status)\n" +
            "        values (#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, #{payMethod},\n" +
            "                #{payStatus}, #{amount}, #{remark}, #{phone}, #{address}, #{consignee},\n" +
            "                #{estimatedDeliveryTime}, #{deliveryStatus}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Orders orders);

    /**
     * 根据状态统计订单数量
     *
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(@Param("status") Integer status);

    /**
     * 根据订单状态和下单时间查询订单
     *
     * @param status
     * @param orderTime
     * @return 注意：在springboot中的mybatis参数名不用加@Param, 只要保证占位符中的名字与参数名字一样就可以, 但是如果在idea中使用了插件后必须要将所有的参数使用@Param注解
     */
    @Select("select * from orders where  status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(@Param("ordersPageQueryDTO") OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);
}