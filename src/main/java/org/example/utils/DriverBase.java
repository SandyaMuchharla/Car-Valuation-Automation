package org.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverBase {

    public WebDriver driver;

    public static ThreadLocal<WebDriver> tdriver = new ThreadLocal<>();

    public WebDriver initialize(String browserName) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        if (browserName.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-in-process-stack-traces", "--disable-crash-reporter", "--disable-logging", "--log-level=3", "--ignore-certificate-errors", "--unsafely-treat-insecure-origin-as-secure");
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setHeadless(BooleanUtils.toBoolean(System.getProperty("headless")));
            chromeOptions.addArguments("--window-size=1920x1080"); //should be enabled for Jenkins
            chromeOptions.merge(desiredCapabilities);
            driver = new ChromeDriver(chromeOptions);
        } else if (browserName.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.addCommandLineOptions("--accept_untrusted_certs");
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.setBinary(firefoxBinary);
            firefoxOptions.setHeadless(BooleanUtils.toBoolean(System.getProperty("headless")));
            firefoxOptions.setAcceptInsecureCerts(true);
            driver = new FirefoxDriver(firefoxOptions);
        }
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        tdriver.set(driver);
        System.out.println("Before Test Thread ID: " + Thread.currentThread().getId());
        return getDriver();
    }
        public synchronized WebDriver getDriver() {
            return tdriver.get();
    }

}

