package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 统计指定时间区域间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTrunoverStatistics(LocalDate begin, LocalDate end);
}
