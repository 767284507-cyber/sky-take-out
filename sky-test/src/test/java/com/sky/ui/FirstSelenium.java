package com.sky.ui;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

@Slf4j
public class FirstSelenium {
    WebDriver driver;

    @Test
    public void test() throws Exception {
        // 自动下载并设置 ChromeDriver,经常启动半天，太难了
//        WebDriverManager.chromedriver().setup();
//        String path=System.getProperty("user.dir")+"/src/test/resources/driver/chromedriver";
//        System.setProperty("webdriver.chrome.driver", path);

//        driver = new ChromeDriver();

        driver = new SafariDriver();   //safari浏览器驱动
        //设置全屏打开

        driver.manage().window().maximize();

        driver.get("http://localhost/#/login");

        //输入用户名密码，点击登录按钮，完成登录流程
//        driver.findElement(By.id("username")).sendKeys("peisong");
        driver.findElement(By.xpath("/html/body/div/div/div/div/form/div[2]/div/div[1]/input")).clear();
        driver.findElement(By.xpath("/html/body/div/div/div/div/form/div[2]/div/div[1]/input")).sendKeys("yangzx");
        driver.findElement(By.xpath("/html/body/div/div/div/div/form/div[3]/div/div[1]/input")).clear();
        driver.findElement(By.xpath("/html/body/div/div/div/div/form/div[3]/div/div[1]/input")).sendKeys("123456");
//        driver.findElement(By.xpath("//*[@id=\"app\"]/div/div/div/form/div[4]/div/button")).click();
        driver.findElement(By.cssSelector("button[type='button']")).click();

        Thread.sleep(1000);

        Assert.assertTrue(driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[1]/div[1]/div/img")).isDisplayed());

    }

    @AfterTest
    public void tearDown() {
        // 关闭浏览器
        if (driver != null)
            driver.quit();
    }
}
