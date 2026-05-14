package com.sky.admin;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sky.base.BaseTest;
//import io.restassured.response.Response;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

//import static io.restassured.RestAssured.given;

public class EmployeeLogoutTest extends BaseTest {
    @Test(description = "员工登出接口测试开始...")
    public void employeeLogoutTest() {
        log.info("员工登出接口测试");
        /**
         *  //使用Hutool的HttpRequest发送POST请求
         HttpRequest request = HttpRequest.post(BASE_URL + "/admin/employee/logout").header("token", token);
         HttpResponse response = request.execute();
         String body = response.body();
         log.info("接口返回结果：{}", body);
         JSONObject jsonObject = JSONUtil.parseObj(body);
         Assert.assertEquals(jsonObject.getInt("code"), 1,"登出成功");
         **/
        /**
         //使用Apache的HttpClient发送POST请求
         CloseableHttpClient httpClient = HttpClients.createDefault();
         HttpPost request = new HttpPost(BASE_URL + "/admin/employee/logout");
         request.setHeader("token", token);
         try(CloseableHttpResponse response = httpClient.execute(request)){
         log.info("接口发送结果状态码：" + response.getStatusLine().getStatusCode());
         HttpEntity entity = response.getEntity();
         String s = EntityUtils.toString(entity, "utf-8");
         log.info("接口返回结果：{}", s);
         JSONObject jsonObject = JSONUtil.parseObj(s);
         Assert.assertEquals(jsonObject.getInt("code"), 1,"登出成功");

         } catch (IOException e) {
         throw new RuntimeException(e);
         }
         **/

        /**

        //使用OkHttp发起请求
        OkHttpClient client = new OkHttpClient();
        
        // 创建空的请求体（登出接口通常不需要请求体）
        RequestBody emptyBody = RequestBody.create(null, new byte[0]);
        
        Request postRequest = new Request.Builder()
                .url(BASE_URL + "/admin/employee/logout")
                .header("token", token)
                .post(emptyBody)
                .build();
        
        Call call = client.newCall(postRequest);
        try (Response response = call.execute()) {
            // 获取响应码
            int statusCode = response.code();
            log.info("接口返回状态码：{}", statusCode);
            // 获取响应体
            String responseBody = response.body().string();
            log.info("接口返回结果：{}", responseBody);
            
            // 解析JSON并断言
            JSONObject jsonObject = JSONUtil.parseObj(responseBody);
            Assert.assertEquals(jsonObject.getInt("code"), 1, "登出成功");

        } catch (IOException e) {
            throw new RuntimeException("请求执行失败", e);
        }
         **/

        //使用RestAssured发起post请求
        Response response = given()
                .header("token", token)
                .contentType("application/json")
                .when()
                .post(BASE_URL + "/admin/employee/logout")
                .then()
                .statusCode(200)
                .body("code",equalTo(1))
                .header("Content-Type", containsString("json"))  // 验证响应头
                .extract()
                .response();

        log.info("接口返回结果是:"+response.body().asString());

//        int statusCode = response.getStatusCode();
//        String responseBody = response.getBody().asString();
//        log.info("接口返回状态码：{}", statusCode);
//        log.info("接口返回结果：{}", responseBody);

//        JSONObject jsonObject = JSONUtil.parseObj(responseBody);
//        Assert.assertEquals(jsonObject.getInt("code"), 1, "登出成功");

    }

}
