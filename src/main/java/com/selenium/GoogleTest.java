package com.selenium;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class GoogleTest {

    private static final Logger logger = LoggerFactory.getLogger(GoogleTest.class);
    private static final String REPORT_PATH = System.getProperty("user.dir") + "/test-output/ExtentReport.html";

    public static void main(String[] args) {
        ExtentReports extent = setupExtentReport();
        ExtentTest test = extent.createTest("Open Google Test", "Opening Google website and verifying title");

        WebDriver driver = null;

        try {
            // Setup WebDriver using WebDriverManager
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            logger.info("ChromeDriver initialized successfully.");

            // Test steps
            driver.get("https://www.google.com");
            String title = driver.getTitle();
            logger.info("Page Title: {}", title);

            if (title.contains("Google")) {
                test.pass("Google website opened successfully with title: " + title);
            } else {
                test.fail("Google website did not open properly. Title was: " + title);
            }

        } catch (Exception e) {
            test.fail("Test failed with exception: " + e.getMessage());
            logger.error("Exception occurred during test execution", e);
        } finally {
            if (driver != null) {
                driver.quit();
                logger.info("Browser closed successfully.");
            }
            extent.flush();
            logger.info("Extent Report generated.");

            // Send Email
            sendEmailWithReport();
        }
    }

    private static ExtentReports setupExtentReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        logger.info("ExtentReports initialized at: {}", REPORT_PATH);
        return extent;
    }

    private static void sendEmailWithReport() {
        String to = "hrishiofficial01@gmail.com"; // Receiver
        String from = "hrishiofficial01@gmail.com"; // Sender
        String host = "smtp.gmail.com";

        String username = "hrishiofficial01@gmail.com"; // Gmail ID
        String password = System.getenv("mail"); // App password from Environment Variable

        if (password == null || password.isEmpty()) {
            logger.error("GMAIL_APP_PASSWORD environment variable is not set!");
            return;
        }

        // Set properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Automation Test Report - Google Test");

            // Body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hi,\n\nPlease find the attached Automation Test Report.\n\nThanks,\nAutomation Team");

            // Attachment
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(REPORT_PATH);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(new File(REPORT_PATH).getName());
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            logger.info("✅ Email Sent Successfully with Test Report!");

        } catch (MessagingException e) {
            logger.error("Failed to send email.", e);
        }
    }
}
