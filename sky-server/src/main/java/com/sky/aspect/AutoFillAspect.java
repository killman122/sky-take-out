package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import com.sky.constant.AutoFillConstant;

/**
 * 自定义切面, 实现公共字段自动填充的处理逻辑
 *
 * @Aspect 用于声明切面类, 但是如果spring的配置没有配好可能会出现问题
 * @Component 用于声明组件, 表名这是一个Spring的Bean对象
 * @Slf4j 用于声明日志, 使得日志的记录更方便
 * <p>
 * 切面: 通知+切入点
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点: 用于声明切入点表达式, 用于确定哪些方法需要被切入, 对哪些类中的哪些方法执行拦截操作
     * <p>
     * execution: 用于声明切入点表达式; 第一个* 表示全部拦截(返回值是所有), 拦截com包下sky包中mapper包下, 第二个* 表示拦截所有类, 第三个* 表示拦截所有方法, .. 表示拦截所有参数
     * <p>
     * 不仅需要拦截上述条件成立的情况, 还需要加上@AutoFill注解, 表示只有加上了@AutoFill注解的方法才会被拦截, 也就是两个条件的交关系
     * <p>
     * 前面的表达式可以理解为: 拦截com包下sky包中mapper包下的所有类中的所有方法, 但是只有加上了@AutoFill注解的方法才会被拦截
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {

    }

    /**
     * 前置通知, 在通知中进行公共字段的赋值
     * 通过前置通知的方式, 在执行某些update和insert操作之前, 将公共字段进行填充
     * 并且在通知的参数中, 添加切入点的表达式的方法名, 表示只有满足切入点表达式的方法才会被拦截
     * 在前置通知中还需要添加连接点对象JoinPoint, 用于获取连接点的信息, 例如拦截到方法名, 参数值和类型等
     */
    @Before("autoFillPointCut()")
    public static void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始执行公共字段的自动填充操作...");

        /**
         * 完善前置通知的相关方法
         * 根据当前不同的操作类型, 通过反射的方式为实体类对象中的公共属性赋值
         */
        //获取当前被拦截的方法上的数据库操作类型是update还是insert, 这里通过连接点对象中的方法进行获取
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取连接点对象中的(签名)信息, 这里强转为接口的子接口, 获取方法签名对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//通过方法签名对象调用getMethod方法获取方法对象, 然后调用getAnnotation方法获取方法上的注解对象
        OperationType operationType = annotation.value();//通过注解对象调用value方法获取注解中的值, 也就是数据库操作类型, 之前的枚举类型中的值

        //获取方法中的实体参数, 通过连接点JoinPointer获取实体类对象, 然后为实体类对象中的公共属性赋值
        Object[] args = joinPoint.getArgs();//通过连接点对象获取方法中的参数, 这里的参数是一个数组, 数组中的元素就是方法中的所有参数, update的相关参数也有insert的相关参数, 所以做一个约定将所有参数中的实体对象放在参数中的第一位参数, 并且返回的参数不能确定类型, 所以使用最终父类Object
        if (args == null || args.length == 0) {//如果参数为空或者参数长度为0, 说明方法无参数 ,那么直接返回
            return;
        }
        Object entity = args[0];//获取参数中的实体对象, 也就是第一位参数

        //准备赋值的数据, 也就是当前的时间以及当前登录用户的id (这里的时间可以使用LocalDateTime.now()获取当前时间, 也可以使用new Date()获取当前时间)
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型, 为对应的属性赋值
        if (operationType == OperationType.INSERT) {
            //如果是insert操作, 那么需要为createTime, updateTime, createUser和updateUser字段赋值, 赋值的时候使用反射的方式为实体类对象中的属性赋值
            //直接连续操作
            entity.getClass()//通过实体类对象获取字节码对象, 真正的类型
                    .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class)//通过字节码对象获取方法对象, 这里的方法名是setCreateTime, 参数类型是LocalDateTime.class
                    .invoke(entity, now);//通过反射的方式为实体类对象中的属性赋值

            //分步骤获取到方法后在通过反射的方式调用
            //Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);//这里可以通过先获取方法在调用的方式获取方法, 也可以直接通过方法名获取方法, 这里使用的是通过方法名获取方法的方式
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);//由于用户id在实体类中就是long类型的变量, 所以这里的参数类型是Long
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            //反射的方式调用方法进行操作, 方法中的参数是实体类对象和需要赋值的值
            setCreateUser.invoke(entity, currentId);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);

        } else if (operationType == OperationType.UPDATE) {
            //如果是update操作, 那么需要为updateTime和updateUser字段赋值, 赋值的时候使用反射的方式为实体类对象中的属性赋值
            entity.getClass()
                    .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class)
                    .invoke(entity, now);

            entity.getClass()
                    .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class)
                    .invoke(entity, currentId);
        }

        log.info("执行完毕公共字段的自动填充操作");
    }
}
