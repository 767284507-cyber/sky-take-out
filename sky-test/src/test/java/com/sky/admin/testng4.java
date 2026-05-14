package com.sky.admin;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;

public class testng4 {
    @DataProvider(name = "testdata4", parallel = false)
    public Iterator<String> testdata() {
        String[] strs = {"张小了", "李四", "王五"};
        List<String> list = Arrays.asList(strs);
        return list.iterator();
    }

    @Test(dataProvider = "testdata4")
    public void test4(String name) {
        System.out.println("name:" + name);
        Object obj1 = "123";
        System.out.println(obj1);

        Object[] strr = {"1", "2", 3};
        for (Object obj : strr) {
            System.out.println(obj);
        }

    }

    @Test
    public void test5() {
        User u1 = new User("张小三", 20);
        User u2 = new User("张兰", 21);
        Object[] obj = {u1, u2};
//        for(Object o:obj){
//            System.out.println(o);
//        }
        System.out.println(obj);

//        Map map=new HashMap();
//        map.put("name","张三");
//        map.put("age",25);
//        map.put("city","北京");
//
//        Map map1=new HashMap();
//        map1.put("name","李四");
//        map1.put("age",30);
//        map1.put("city","上海");
//
//        List list=new ArrayList();
//        list.add(map);
//        list.add(map1);
//        System.out.println(list);
//
//        System.out.println(map);
//        System.out.println(map1);
//
//
        List<String> list1 = Arrays.asList(new String[]{"123", "abbsbs", "999"});
        System.out.println(list1);
//

        File file = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/employee.xlsx");
        boolean exists = file.exists();
        System.out.println("文件是否存在：" + exists);
        if (!exists) {
            throw new RuntimeException("文件不存在");
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String str = "1";
        int i = Integer.parseInt(str);
        Assert.assertEquals(i,1);


        System.out.println("*****************");
        System.out.println(nullValue());
    }
}
