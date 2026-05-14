package com.sky.admin;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

//方式一：使用@DataProvider注解，直接在方法内写数据,并使用一维数组返回数据
public class testng2 {


    @DataProvider(name="testdata2",parallel = false)
    public Object[] testData(){
        return new Object[]{"admin","yangzx"};
    }


    @Test(dataProvider = "testdata2")
    public void test2(String user){
        System.out.println("user:"+user);
    }
}
