package com.sky.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Slf4j
public class FirstPlaywright {
    Playwright playwright;
    Browser browser;

    @BeforeTest
    public void beforeTest() {
        //创建playwright对象
        playwright = Playwright.create();
    }
    @Test
    public void test() {

        //启动浏览器
         browser = playwright.chromium().launch();  //默认无头模式headless模式

//        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        //打开一个新页面
        Page page = browser.newPage();
        //访问页面
        page.navigate("http://localhost/#/login");
        //截图
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("login.png")));

        page.getByPlaceholder("账号").fill("peisong");
        page.getByPlaceholder("密码").fill("123456");
        //css选择器
        page.locator("text=登录").click();
        // 等待登录完成
        page.waitForLoadState();

        //断言
//        assertThat(page.url()).contains("http://localhost/#/dashboard");
         assertThat(page.locator("img[src='img/logo.38b01728.png']")).isVisible();
    }
}

