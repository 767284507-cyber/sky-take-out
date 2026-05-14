package com.sky.admin;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

//方式一：使用@Parameters和xml文件配合使用
public class testng1 {

    //@Optional = "如果没给我参数，就用这个默认值，想快速测试？→ 直接右键运行testng1.java，使用默认值
    //想批量测试？→ 配置 testng.xml，统一管理参数

    @Test
    @Parameters({"username","password"})
    public void testl(@Optional("admin") String username, @Optional("123456") String password){
        System.out.println("username:"+username+",password:"+password);
    }
}
