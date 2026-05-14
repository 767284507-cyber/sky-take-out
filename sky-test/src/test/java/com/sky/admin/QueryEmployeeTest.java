package com.sky.admin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sky.base.BaseTest;
import org.springframework.lang.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class QueryEmployeeTest  extends BaseTest {

    /**
     * 测试用例1：正常查询第一页，每页/10条，不输入员工姓名
     */
    @Test(description = "正常分页查询员工列表")
    public void testQueryByPageSuccess() {
        log.info("---------- TC01: 正常分页查询 ----------");
        JSONObject result = JSONUtil.parseObj(queryByPage(1000, 10, null));
        Integer code = result.getInt("code");
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        Assert.assertEquals(code, 1, "查询成功code应该是1");
        Assert.assertNotNull(data, "data数据不为空");


    }

    /**
     * 测试用例2:根据员工姓名查询：精准查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Test(description = "根据员工姓名精确查询")
    public void testQueryByName() {
        log.info("---------- TC02: 正常按员工姓名精确查询 ----------");
        JSONObject result = JSONUtil.parseObj(queryByPage(1, 10, "管理员"));
        log.info("查询结果是" + result.toString());
        //断言：查询结果不报错
        Assert.assertEquals(result.getInt("code"), 1);
        //提取data对象
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        //获取records list
        java.util.List<Map<String,Object>>  records = (java.util.List<Map<String, Object>>) data.get("records");
        //断言records不为空且有数据
        Assert.assertFalse(records.isEmpty(), "records应包含至少一条记录");

        //断言第一条记录的 name 字段
        String name  = (String) records.get(0).get("name");
        Assert.assertEquals(name,"管理员");


    }

    /**
     * 测试用例3:根据员工姓名查询：模糊查询
     *
     */

    @Test(description = "根据员工姓名模糊匹配查询")
    public void testVagueQueryByName() {
        log.info("---------- TC03: 正常按员工姓名模糊查询 ----------");
        JSONObject result = JSONUtil.parseObj(queryByPage(1, 10, "管"));
        log.info("查询结果是" + result.toString());
        //断言：查询结果不报错
        Assert.assertEquals(result.getInt("code"), 1);
        //提取data对象
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        //获取records list
        java.util.List<Map<String,Object>>  records = (java.util.List<Map<String, Object>>) data.get("records");
        //断言records不为空且有数据
        Assert.assertFalse(records.isEmpty(), "records应包含至少一条记录");

        //断言第一条记录的 name 字段
        String name  = (String) records.get(0).get("name");
        Assert.assertEquals(name,"管理员");


    }

    /**
     * 测试用例3:根据员工姓名查询：查询结果为空但查询不报错
     *
     */

    @Test(description = "根据员工姓名查不到记录")
    public void testVoidQueryByName(){

        log.info("---------- TC04: 根据员工姓名查不到记录 ----------");
        JSONObject result = JSONUtil.parseObj(queryByPage(1, 10, "管鸭子"));
        log.info("查询结果是" + result.toString());
        //断言：查询结果不报错
        Assert.assertEquals(result.getInt("code"), 1);
        //提取data对象
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        //获取records list
        java.util.List<Map<String,Object>>  records = (java.util.List<Map<String, Object>>) data.get("records");
        //断言records为空
        Assert.assertTrue(records.isEmpty(), "records应包含至少一条记录");


    }

    public String queryByPage(int page, int pageSize, @Nullable String name) {
        HttpRequest httpRequest = HttpRequest.get(BASE_URL + "/admin/employee/page");
        httpRequest.header("token", token);
        httpRequest.form("page", page);
        httpRequest.form("pageSize", pageSize);
        if (name != null) {
            httpRequest.form("name", name);
        }
        httpRequest.charset("utf-8");
        HttpResponse httpResponse = httpRequest.execute();
        System.out.println(httpRequest.getUrl());
        return httpResponse.body();
    }
}
