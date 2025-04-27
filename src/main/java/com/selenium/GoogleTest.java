package com.selenium;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class GoogleTest {
    public static void main(String[] args) {
        // Setup ExtentReports
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        ExtentTest test = extent.createTest("Open Google Test", "Opening Google website and verifying title");

        // Setup WebDriver
        System.setProperty("webdriver.chrome.driver", "C:/drivers/chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        try {
            // Test Steps
            driver.get("https://www.google.com");
            String title = driver.getTitle();
            System.out.println("Page Title: " + title);

            // Logging to report
            if (title.contains("Google")) {
                test.pass("Google website opened successfully with title: " + title);
            } else {
                test.fail("Google website did not open properly. Title was: " + title);
            }

        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close Browser
            driver.quit();
            System.out.println("Test Completed. Browser closed.");

            // Finalize ExtentReport
            extent.flush();
        }
    }
}
