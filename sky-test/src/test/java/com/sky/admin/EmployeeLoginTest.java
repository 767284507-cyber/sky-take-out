package com.sky.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工登录接口自动化测试
 * 接口：POST /admin/employee/login
 */
public class EmployeeLoginTest {

    private static final Logger log = LoggerFactory.getLogger(EmployeeLoginTest.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LOGIN_URL = "http://localhost:8080/admin/employee/login";

    private OkHttpClient client;

    @BeforeClass
    public void setUp() {
        client = new OkHttpClient();
        log.info("========== 员工登录接口测试开始 ==========");
    }

    /**
     * 测试用例1：正常登录 - 用户名密码正确
     */
    @Test(description = "正常登录，用户名密码正确")
    public void testLoginSuccess() throws IOException {
        log.info("---------- TC01: 正常登录 ----------");
//        String responseBody = doLogin("admin", "123456");
        String responseBody = login("admin", "123456");

        log.info("响应体: {}", responseBody);

        Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
        Integer code = (Integer) result.get("code");
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        String token = (String) data.get("token");

        Assert.assertEquals(code, 1, "业务code应为1（成功）");
        Assert.assertNotNull(token, "token不应为空");
        Assert.assertTrue(token.startsWith("ey"), "token应为JWT格式");
        Assert.assertEquals(code, 1);
        log.info("TC01 通过，token: {}", token);
    }

    /**
     * 测试用例2：密码错误
     */
    @Test(description = "密码错误，登录失败")
    public void testLoginWrongPassword() throws IOException {
        log.info("---------- TC02: 密码错误 ----------");
        String responseBody = doLogin("admin", "wrongpassword");
        log.info("响应体: {}", responseBody);

        Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
        Integer code = (Integer) result.get("code");
        String msg = (String) result.get("msg");

        Assert.assertEquals(code, 0, "密码错误时业务code应为0（失败）");
        Assert.assertEquals(msg,"密码错误");

        log.info("TC02 通过，失败原因: {}", msg);
    }

    /**
     * 测试用例3：用户名不存在
     */
    @Test(description = "用户名不存在，登录失败")
    public void testLoginUserNotFound() throws IOException {
        log.info("---------- TC03: 用户名不存在 ----------");
        String responseBody = doLogin("notexist_user", "123456");
        log.info("响应体: {}", responseBody);

        Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
        Integer code = (Integer) result.get("code");
        String msg = (String) result.get("msg");

        Assert.assertEquals(code, 0, "用户不存在时业务code应为0（失败）");
//        Assert.assertNotNull(msg, "失败时msg不应为空");
        Assert.assertEquals(msg,"账号不存在");
        log.info("TC03 通过，失败原因: {}", msg);
    }

    /**
     * 测试用例4：用户名为空
     */
    @Test(description = "用户名为空，登录失败")
    public void testLoginEmptyUsername() throws IOException {
        log.info("---------- TC04: 用户名为空 ----------");
        String responseBody = doLogin("", "123456");
        log.info("响应体: {}", responseBody);

        Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
        Integer code = (Integer) result.get("code");

        Assert.assertEquals(code, 0, "用户名为空时业务code应为0（失败）");
        log.info("TC04 通过");
    }

    @Test
    public void testLoginUserlock() throws IOException {
        log.info("---------- TC06: 用户被锁定 ----------");
         String responseBody = doLogin("jiangyc", "123456");
        log.info("响应体: {}", responseBody);
        Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
        Integer code = (Integer) result.get("code");
        String msg= (String) result.get("msg");
        Object data = result.get("data");
        Assert.assertEquals(code, 0, "用户被锁定code应为0（失败）");
        Assert.assertEquals(msg,"账号被锁定");
        Assert.assertTrue(data==null||data=="","data内容为空");
        log.info("TC05 通过");


    }

    /**
     * 发送登录请求，返回响应体字符串方式一：
     *
     */
    private String doLogin(String username, String password) throws IOException {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);

        String json = objectMapper.writeValueAsString(loginData);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("HTTP状态码: {}", response.code());
            return responseBody;
        }
    }

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 发送登录请求，返回响应体字符串方式二：
     *
     * @param username
     * @param password
     * @return
     */
    private String login(String username, String password) {

        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/admin/employee/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

//        HttpResponse<String> response = httpClient.send(request,
//                HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> resp = null;
        try {
            resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resp.body();
    }


    private String login2(String username, String password) {


        // 1. 使用 Map 构建请求数据
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);

        // 2. 转换为 JSON 字符串
        String json = null;
        try {
            json = objectMapper.writeValueAsString(loginData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 4. 创建 HttpClient 和 HttpPost
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

            // 5. 设置请求头和请求体
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            // 6. 执行请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                log.info("HTTP状态码: {}", response.getStatusLine().getStatusCode());
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}