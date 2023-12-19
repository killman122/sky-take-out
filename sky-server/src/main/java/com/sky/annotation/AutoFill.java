package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解, 用于标识某个方法需要进行功能字段自动填充处理
 * 使用@Target(ElementType.METHOD) 注解在注解类型的前面表示这个注解只能添加在方法上
 * 但是可以在@Target中指定其余的ElementType类型,标识特定的类型字段等, 例如: ElementType.TYPE, ElementType.FIELD
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)//表示该注解一直保留到运行阶段
public @interface AutoFill {
    //通过枚举的方式指定数据操作的类型, UPDATE INSERT, 在确定枚举的类型后, 后面的元素名value中参数就可以作为传入的参数直接传入到注解中, 可以查看注解相关的笔记
    OperationType value();
}
