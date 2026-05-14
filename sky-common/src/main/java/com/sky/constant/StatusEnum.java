package com.sky.constant;

/**
 * 状态枚举类(有时候好像使用常量类更简单）
 * @author chaoyan
 */

public enum StatusEnum {
    ENABLE(1),
    DISABLE(0);

    private final Integer status;

   private StatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
