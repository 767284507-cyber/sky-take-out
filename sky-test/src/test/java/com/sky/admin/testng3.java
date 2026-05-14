package com.sky.admin;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
//方式一：使用@DataProvider注解，直接在方法内写数据,并使用二维数组返回数据
public class testng3 {
    @DataProvider(name="testdata3",parallel = false)
    public Object[][] testdata(){
        Object[][] data={{"admin","123456",1},{"yangzx","123456",0},{"zhangsan","123456",0}};
        return data;
    }

    @Test(dataProvider = "testdata3")
    public void test3(String user,String password,int sex){
        System.out.println("user:"+user+",password:"+password+",sex:"+sex);
    }
}
