package com.sky.exception;

/**
 * 账号不存在异常类, 该异常类继承于BaseExc
 * eption, 该异常类用于在登录的时候, 如果用户输入的用户名不存在, 则抛出该异常
 */
public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
