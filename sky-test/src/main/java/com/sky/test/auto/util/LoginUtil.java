package com.sky.test.auto.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 登录工具类，登录成功提取token,登录失败打印日志
 */
public class LoginUtil {

    private static final Logger log = LoggerFactory.getLogger(LoginUtil.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String employeeLogin(String username, String password) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);

        String json = objectMapper.writeValueAsString(loginData);

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url("http://localhost:8080/admin/employee/login")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
            Integer code = (Integer) result.get("code");
            if (code == null || code != 1) {
                log.error("登录失败，响应体: {}", responseBody);
                throw new IOException("登录失败: " + responseBody);
            }
            // 登录成功，提取token
            log.info("登录成功，响应体: {}", responseBody);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            return (String) data.get("token");
        }
    }
}

