package com.sky.base;

import com.sky.test.auto.httpapi.ApiClient;
import com.sky.test.auto.util.EmployeeLoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;

public class BaseTest {
    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static final String BASE_URL = "http://localhost:8080";
    protected String token;
    protected ApiClient apiClient;


    @BeforeClass
    public void setUp() {
        try {
            // 初始化 API 客户端
            apiClient = new ApiClient();
            apiClient.setBaseUrl(BASE_URL);

            // 获取登录 token
            token = EmployeeLoginUtil.login("admin", "123456");
            log.info("登录成功，token已获取，token:" + token);

            // 设置请求头
            apiClient.setHeader("token", token);

        } catch (IOException e) {
            throw new RuntimeException("登录失败", e);
        }
    }

    @AfterClass
    public void tearDown() {
        // 清理资源
        if (apiClient != null) {
            apiClient = null;
        }
    }

    // 通用的测试方法
    protected void logTestStart(String testName) {
        log.info("开始测试：{}", testName);
    }

    protected void logTestEnd(String testName) {
        log.info("测试结束：{}", testName);
    }
}
