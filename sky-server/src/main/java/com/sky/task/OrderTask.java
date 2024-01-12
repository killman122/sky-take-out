package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类, 定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的方法
     */
//    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void procesTimeOutOrder() {
        log.info("定时处理超时订单:{}", LocalDateTime.now());
        // select * from orders where status = ? and order_time(下单时间) < (当前时间 - 15)

        //LocalDateTime.now().plusMinutes(15);//当前时间 + 15分钟
        //LocalDateTime.now().minusMinutes(15);//当前时间 - 15分钟

        /*
        将订单状态为待付款的订单, 并且下单时间小于当前时间减去15分钟的订单查询出来,后通过循环遍历的方式修改订单状态为已取消
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15));
        if (orderList != null && orderList.size()>0){
            for (Orders orders : orderList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }*/

        orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15))
                .forEach(order -> {
                    //修改订单状态为已取消
                    order.setStatus(Orders.CANCELLED);
                    order.setCancelReason("订单超时自动取消");
                    order.setCancelTime(LocalDateTime.now());
                    orderMapper.update(order);
                });
    }

//    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点触发一次
    public void processDeliveryOrder(){
        log.info("定时处理派送中的订单:{}", LocalDateTime.now());
        //查询出所有派送中的订单, 并且订单的派送时间小于当前时间减去1小时的订单, 然后将订单状态修改为已完成
        orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusHours(1))
                .forEach(order -> {
                    order.setStatus(Orders.COMPLETED);
                    orderMapper.update(order);
                });
    }
}
