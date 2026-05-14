package com.sky.test.auto.httpapi;

/**
 * API响应封装类
 * 响应结构：{"code":1,"msg":null,"data":null}
 */
public class ApiResponse<T> {

    /**
     * 业务状态码（1表示成功，其他表示失败）
     */
    private Integer code;

    /**
     * 业务消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, null, data);
    }

    /**
     * 创建成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(1, msg, data);
    }

    /**
     * 创建失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    /**
     * 判断业务是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 1;
    }

    // Getters and Setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
