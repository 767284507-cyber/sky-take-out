package com.sky.exception;

/**
 * 参数校验异常
 */
public class ParameterValidationException extends BaseException {

    public ParameterValidationException() {
    }

    public ParameterValidationException(String msg) {
        super(msg);
    }

}
