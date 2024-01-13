package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定时间区域间内的营业额数据
     * LocalDate类中有方法可以对时间类型的数据进行操作, 进行增加或者减少数据, 对应着增加天数或者是减少天数
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
        while (!begin.isEqual(end)){
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
                .dateList( StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }
}
