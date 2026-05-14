package com.sky.admin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sky.base.BaseTest;
import com.sky.test.auto.util.DBUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddEmployeeTest extends BaseTest {

    //前置清理
    @BeforeClass
    public void initData() {
        Connection con=null;
        PreparedStatement ps = null;
        try { // 清理可能存在的测试数据
           con = DBUtils.getCon();
            String sql = "DELETE FROM employee WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "yangzx");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {

            // 关闭资源
            DBUtils.close(con, ps);
            log.info("已清理测试数据，确保用例可重复执行");
        }
    }

    @Test(description = "正常添加后台员工用户", priority = 1)
    public void addEmployeeTest() throws SQLException {
        log.info("---------- TC01: 正常添加后台员工用户 ----------");
        String requestJson = "{\"name\":\"杨簪星\",\"phone\":\"17756772102\",\"sex\":\"0\",\"idNumber\":\"340121100507171723\",\"username\":\"yangzx\"}";
        HttpResponse response = HttpRequest.post("http://localhost/api/employee").header("Content-Type", "application/json")
                .header("token", token)
                .body(requestJson)
                .execute();

        log.info("响应报文是" + response.body());
        JSONObject respJson = JSONUtil.parseObj(response.body());
        int code = (int) respJson.get("code");
        Assert.assertEquals(code, 1, "响应code应该是1");

        //连接数据库做dbcheck
        System.out.println("*****连接数据库做dbcheck");
        Connection con = DBUtils.getCon();
        System.out.println(con);
        log.info("连接到数据库，对应信息是:--" + con.toString());
        String sql = "select * from employee where username=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "yangzx");
        ResultSet rs = ps.executeQuery();
        //比对各个字段的值
        while (rs.next()) {
           log.info("查询到一条记录:");
            Assert.assertEquals(rs.getRow(), 1, "查询到一条记录");
            Assert.assertEquals(rs.getString(2), "杨簪星");
            Assert.assertEquals(rs.getString(5), "17756772102");
            Assert.assertEquals(rs.getString(6), "0");
            Assert.assertEquals(rs.getString(7), "340121100507171723");
            Assert.assertEquals(rs.getInt(8), 1, "状态应该默认为1（启用）");
        }


        //关闭资源
        rs.close();
        DBUtils.close(con, ps);
    }

    @Test(description = "添加后台员工用户时，用户名已存在", priority = 2)
    public void addEmployeeExistTest() throws SQLException {
        log.info("---------- TC02: 添加后台员工用户时，用户名已存在 ----------");
        String requestJson = "{\"name\":\"杨簪星\",\"phone\":\"17756772102\",\"sex\":\"0\",\"idNumber\":\"340121100507171723\",\"username\":\"yangzx\"}";
        HttpResponse response = HttpRequest.post("http://localhost/api/employee").header("Content-Type",
                        "application/json")
                .header("token", token)
                .body(requestJson)
                .execute();

        log.info("响应报文是" + response.body());
        JSONObject respJson = JSONUtil.parseObj(response.body());
        int code = (int) respJson.get("code");
        Assert.assertEquals(code, 0, "响应code应该是0，用户名已存在");
        String msg = (String) respJson.get("msg");
        Assert.assertEquals(msg, "yangzx已存在", "响应msg应该是yangzx已存在");
    }

}
