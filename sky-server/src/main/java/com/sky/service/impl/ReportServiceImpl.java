package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区域间内的营业额数据
     * LocalDate类中有方法可以对时间类型的数据进行操作, 进行增加或者减少数据, 对应着增加天数或者是减少天数
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTrunoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin日期到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        //如果begin不等于end !begin.equals(end), 则一直遍历完所有数据, 将所有的中间时间加入到日期的集合中
        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //在List集合中存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        //营业额和对应的时间日期是匹配的, 但是每个日期可能对应着不同的营业额
        for (LocalDate date : dateList) {
            //查询date营业额对应的数据, 这里的营业额指的是状态为"已完成"的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); //获得当天的开始时间, 也就是将年月日和时分秒组合
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX); //获得当前的结束时间

            //select sum(amount) from orders where order_time > begin_time and order_time < endTime and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map); //营业额

            //如果营业额为null, 则将营业额设置为0.0
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        //通过 org.apache.commons.lang3 包中StringUtils工具类的join方法将集合中的元素取出后封装为字符串,将每个元素使用指定的separator分隔符进行分隔
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区域间内的用户数据
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间每天对应的日期时间
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //统计每天的用户数量使用数据库设计时的字段也是在映射到实体中的字段creatTime属性进行设置
        //存放每天的新增用户总量, 先将每天的用户数量查询出来, 放入到一个list集合中进行处理
        List<Integer> newUserList = new ArrayList<>();
        //select count(id) from user where creat_time < ?(当天时间中的最小时间点) and creat_time >(当前时间天数中的最大时间点) ?

        //存放每天总共的用户数量
        List<Integer> totalUserList = new ArrayList<>();
        //select count(id) from user where creat_time < ?

        //通过遍历的方式得到每一天的数据,只有年月日, 时分秒之后使用LocalDateTime类中的of方法实现年月日和时分秒的拼接组合
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);

            //总用户数量
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", beginTime);

            //新增用户数量
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        //封装结果数据到VO对象中
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ",")).build();
    }

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end之间每天对应的日期时间
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每日订单数
        List<Integer> orderCountList = new ArrayList<>();

        //存放每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        //遍历dateList集合, 查询每天的订单数量, 有效订单数量
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//当天时间中的0:0:0
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//当天时间中的23:59:59

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            //查询每天的订单数量  select count(id) from orders where order_time > begin_time and order_time < endTime
            Integer orderCount = orderMapper.countByMap(map);

            //查询每天的有效订单数量  select count(id) from orders where order_time > begin_time and order_time < endTime and status = 5
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间区间内的总订单数量, 通过for循环的方式进行计算
        Integer totalOrderCount = 0;
        for (int i = 0; i < orderCountList.size(); i++) {
            totalOrderCount += orderCountList.get(i);
        }
//        for (Integer i : orderCountList) {
//            //totalOrderCount = totalOrderCount+i;
//            //totalOrderCount+=i;
//            (totalOrderCount, i)-> totalOrderCount + i;
//        }


        //计算时间区间内的有效订单数量
        Stream<Integer> stream = validOrderCountList.stream();//获取一个有效订单数量的stream流对象
        Integer validOrderCount = stream.reduce(Integer::sum).get();//通过reduce方法进行计算, 通过sum方法进行求和

        //对于Integer类型数据调用.doubleValue()方法能够将Integer类型数据转换为double类型数据

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        //封装结果数据到VO对象中
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(orderCountList.stream().mapToInt(Integer::intValue).sum())
                .validOrderCount(validOrderCountList.stream().mapToInt(Integer::intValue).sum())
                .orderCompletionRate(orderCompletionRate).build();

    }

    /**
     * 根据条件统计订单数量, 如果是按照第二种的方式调用, 直接将不需要的参数传递一个null的空参数代替, 因为在mapper中使用了if标签进行判断
     *
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    /**
     * 统计指定时间区间内的销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);//将LocalDate 相关的日期数据加上组合变为LocalDateTime的日期时间类型数据
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}
