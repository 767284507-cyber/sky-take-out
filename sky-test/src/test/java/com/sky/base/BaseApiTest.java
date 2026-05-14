package com.sky.base;

import com.sky.test.auto.httpapi.ApiClient;
import com.sky.test.auto.httpapi.ApiResponse;
import com.sky.test.auto.util.LoginUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;

public class BaseApiTest {
    protected ApiClient apiClient;
    protected String token;

    @BeforeMethod
    public void setUp() {
        // 初始化 API 客户端
        apiClient = new ApiClient();
        // 获取登录 token
        try {
            token = LoginUtil.employeeLogin("admin", "123456");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 设置请求头
        apiClient.setHeader("Authorization", "Bearer " + token);
    }

    @AfterMethod
    public void tearDown() {
        if (apiClient != null) {
            // ApiClient 可能没有 close 方法，根据实际情况调整
            // apiClient.close();
            apiClient = null;
        }
    }

    // 通用的 API 测试方法
    protected <T> ApiResponse<T> sendRequest(String method, String url, Object requestBody, Class<T> responseType) {
        return apiClient.execute(method, url, requestBody, responseType);
    }

}
