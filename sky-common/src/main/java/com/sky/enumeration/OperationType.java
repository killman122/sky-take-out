package com.sky.enumeration;

/**
 * 数据库操作类型, 这里通过枚举的方式指定数据操作的类型, 在自动填充的时候根据不同的操作类型进行不同的处理
 */
public enum OperationType {

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 插入操作
     */
    INSERT

}
