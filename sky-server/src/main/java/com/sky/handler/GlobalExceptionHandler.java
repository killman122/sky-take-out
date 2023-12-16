package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Array;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     * 全局异常处理器, 统一捕获异常, 并且这里传入的异常都是父类异常, 所有的自定义异常类都是继承于BaseException, 所以这里可以捕获所有的自定义异常, 并向前端返回对应的异常信息
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQLIntegrityConstraintViolationException异常, 处理SQL异常
     * @param ex
     * @return
     * <p>
     * ExceptionHandler注解是spring框架中的注解, 用于捕获异常, 这里的异常是SQLIntegrityConstraintViolationException,
     * 这个异常是在数据库中的异常, 一般是在数据库中的字段设置了唯一约束, 但是在插入数据的时候, 有重复的数据, 所以会抛出这个异常
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("异常信息：{}", ex.getMessage());//Duplicate entry 'wd' for key 'employee.idx_username'
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")){//如果异常信息中包含Duplicate entry, 则表示是重复的数据
            String[] strings = message.split(" ");
            //System.out.println(Arrays.toString(strings));//打印出数组中的所有元素, 确定分隔后的元素索引位置
            String username = strings[2];
            String msg = username + MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
