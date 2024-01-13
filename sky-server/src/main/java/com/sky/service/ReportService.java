package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 统计指定时间区域间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTrunoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区域间内的用户数据
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
}