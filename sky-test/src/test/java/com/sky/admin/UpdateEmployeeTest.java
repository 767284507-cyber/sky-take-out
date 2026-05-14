package com.sky.admin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.sky.base.BaseTest;
import com.sky.dto.EmployeeDTO;
import com.sky.test.auto.util.provider.DataTable;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class UpdateEmployeeTest extends BaseTest {
    private static String filePath = System.getProperty("user.dir") + "/src/test/resources/testdata/employee.xlsx";


    @DataProvider(name = "getData", parallel = false)
    //parallel = false 代表不并行执行测试用例 true 代表并行执行测试用例
    public Iterator<Object[]> getData(Method method) {
        DataTable dataTable = new DataTable();
        dataTable.setDataTable(filePath, "update", "");
        List<Object[]> dataList = new ArrayList<>();
        Iterator<Object[]> it = dataTable.getDataTable();
        while (it.hasNext()) {
            Object[] objects = it.next();
            dataList.add(objects);

            for (Object o : objects) {
                System.out.println(o);
            }
        }
        log.info("从数据文件加载了 {} 条测试数据", dataList.size());
        return dataList.iterator();
    }


    @Test(dataProvider = "getData", description = "更新员工信息接口测试")
    public void updateEmployeeTest(HashMap<String, String> provider) {
        logTestStart("更新员工信息 - " + provider.get("TestCaseName"));

        String employeeId = provider.get("id");
        String expectedCode = provider.get("resultcode");
        String expectedMsg = provider.get("msg");

        // 构建请求对象
        EmployeeDTO employeeDTO = new EmployeeDTO();
        if (employeeId != null && !employeeId.isEmpty()) {
            employeeDTO.setId(Long.parseLong(employeeId));
        }
        employeeDTO.setUsername(provider.get("username").trim());
        employeeDTO.setName(provider.get("name").trim());
        employeeDTO.setPhone( provider.get("phone").trim());
        employeeDTO.setSex(provider.get("sex"));
        employeeDTO.setIdNumber(provider.get("idNumber"));

        log.info("发送PUT请求到: /admin/employee");
        log.info("请求体: {}", JSONUtil.toJsonStr(employeeDTO));

        HttpResponse httpResponse = HttpRequest.put(BASE_URL + "/admin/employee").header("Content-Type",
                        "application/json")
                .header("token", token)
                .body(JSONUtil.toJsonStr(employeeDTO))
                .execute();
        log.info("响应信息：" + httpResponse.body());

        Assert.assertEquals(httpResponse.getStatus(), 200);
        Integer code = (Integer) JSONUtil.parseObj(httpResponse.body()).get("code");
        //断言业务返回的code码是否和预期一致
        log.info("业务返回的code码: {}", code);
        log.info("预期的code码: {}", Integer.parseInt(expectedCode));
        Assert.assertEquals(code, Integer.parseInt(expectedCode), "code码一致性检查");
        //断言业务返回的错误信息是否和预期一致
        if (code != 1) {
            log.info("业务返回的错误信息: {}", JSONUtil.parseObj(httpResponse.body()).getStr("msg"), "错误信息一致性检查");
            Assert.assertEquals(JSONUtil.parseObj(httpResponse.body()).getStr("msg"), expectedMsg);
        }


    }


}
