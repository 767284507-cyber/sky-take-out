package com.sky.admin.category;

import com.sky.base.BaseTest;
import com.sky.test.auto.util.DBUtils;
import com.sky.test.auto.util.provider.DataTable;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class AddCategoryTest extends BaseTest {
    private static String filePath = System.getProperty("user.dir") + "/src/test/resources/testdata/employee.xlsx";

    @BeforeClass
    public void initData() {
        Connection con = null;
        PreparedStatement ps = null;
        try { // 清理可能存在的测试数据
            con = DBUtils.getCon();
            String sql = "DELETE FROM category WHERE name in(?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, "凉拌海蜇");
            ps.setString(2, "凉拌海蜇");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {

            // 关闭资源
            DBUtils.close(con, ps);
            log.info("已清理测试数据，确保用例可重复执行");
        }
    }


    @DataProvider(name = "addCategoryData", parallel = false)
    Iterator<Object[]> getData(Method method) {
        DataTable dataTable = new DataTable();
        dataTable.setDataTable(filePath, "addCategory", "");
        Iterator<Object[]> it = dataTable.getDataTable();
        List<Object[]> dataList = new ArrayList<>();

        while (it.hasNext()) {
            Object[] objects = it.next();
            dataList.add(objects);

            for (Object o : objects) {
                log.info("加载的数据为：" + o);
            }
        }
        log.info("从数据文件加载了 {} 条测试数据", dataList.size());
        return dataList.iterator();
    }

    @Test(dataProvider = "addCategoryData", description = "新增分类测试")
    public void addCategoryTest(HashMap<String, String> provider) {
        logTestStart("新增分类测试 - " + provider.get("TestCaseName"));
        //使用RestAssured发起post请求
        given().baseUri(BASE_URL)
                .header("token", token)
                .contentType(ContentType.JSON)
                //json序列化，把hashmap转换成json字符串
                .body(provider)
                .when()
                .post("/admin/category")
                .then()
                .log().all() //.log().body()  // 打印响应信息
                .assertThat().statusCode(200)
                .assertThat().body("code", equalTo(Integer.parseInt(provider.get("resultcode"))))
                .assertThat().body("msg", provider.get("msg") == null || provider.get("msg").isEmpty() ? nullValue() : equalTo(provider.get("msg")));


        if (Integer.parseInt(provider.get("resultcode")) == 1) {
            //数据库断言验证
            //获取数据库连接
            Connection con = DBUtils.getCon();
            //执行sql语句
            String sql = "select type,name,sort,status from category where name=? and type=1";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = con.prepareStatement(sql);
                ps.setString(1, provider.get("name"));
                rs = ps.executeQuery();
                while (rs.next()) {
                    log.info("数据库查询到数据");
                    Assert.assertEquals(rs.getInt(1), Integer.parseInt(provider.get("type")), "分类类型匹配");
                    Assert.assertEquals(rs.getString(2), provider.get("name"), "分类名称匹配");
                    Assert.assertEquals(rs.getInt(3), Integer.parseInt(provider.get("sort")), "排序匹配");
                    Assert.assertEquals(rs.getInt(4), 0, "初始添加状态都是0-禁用");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                DBUtils.close(con, ps);
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        logTestEnd("新增分类测试 - " + provider.get("TestCaseName"));
    }

}
