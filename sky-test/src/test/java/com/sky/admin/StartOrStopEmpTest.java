package com.sky.admin;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sky.base.BaseTest;
import com.sky.test.auto.util.provider.DataTable;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class StartOrStopEmpTest extends BaseTest {
    private static String filePath = System.getProperty("user.dir") + "/src/test/resources/testdata/employee.xlsx";

    @DataProvider(name = "getData", parallel = false)
    //parallel = false 代表不并行执行测试用例 true 代表并行执行测试用例
    public Iterator<Object[]> getData(Method method) {
        DataTable dataTable = new DataTable();
        dataTable.setDataTable(filePath, "startOrStop", "");
        Iterator<Object[]> it = dataTable.getDataTable();
        List<Object[]> dataList = new ArrayList<>();

        while (it.hasNext()) {
            Object[] objects = it.next();
            dataList.add(objects);

            for (Object o : objects) {
                log.info("加载的数据为："+o);
            }
        }
        log.info("从数据文件加载了 {} 条测试数据", dataList.size());
        return dataList.iterator();
    }

    @Test(dataProvider = "getData", description = "启用/停用员工接口测试")
    public void startOrStopEmpTest(HashMap<String, String> provider) {
        logTestStart("启/停用员工 - " + provider.get("TestCaseName"));

        //准备参数
        Integer status = Integer.parseInt(provider.get("status"));
        Long id = Long.parseLong(provider.get("id"));
        Integer expectCode = Integer.valueOf(provider.get("resultcode"));
         String expectMsg = provider.get("msg");

        //发起请求
        //换一种方式，发起http请求
        HttpPost httpPost = new HttpPost(BASE_URL + "/admin/employee/status/" + status + "?id=" + id);
        httpPost.setHeader("token", token);
        httpPost.setHeader("Content-Type", "application/json");
        try {

            //准备客户端，提交请求
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);

            //获取响应并解析结果为json对象
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            log.info("接口返回结果：{}", s);
            JSONObject jsonObject = JSONUtil.parseObj(s);
            Assert.assertEquals(jsonObject.getInt("code"), expectCode);
            if(expectCode != 1){
                log.info("校验错误信息是否匹配");
                Assert.assertEquals(jsonObject.getStr("msg"), expectMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}