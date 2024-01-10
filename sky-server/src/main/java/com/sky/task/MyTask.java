package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component//将当前类进行实例化后交给Spring进行管理
public class MyTask {
    /**
     * 定时任务 任务每隔5s触发一次
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void execute() {
        System.out.println("MyTask.execute()");
        log.info("定时任务开始执行:{}", new Date());
    }
}
