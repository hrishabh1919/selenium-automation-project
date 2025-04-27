package com.selenium;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;
import java.util.Properties;

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

            // Send Email with Report
            sendEmailWithReport();
        }
    }

    public static void sendEmailWithReport() {
        // Email configuration
        String to = "hrishiofficial01@gmail.com"; // Receiver email
        String from = "hrishiofficial01@gmail.com"; // Your email
        String host = "smtp.gmail.com"; // Gmail SMTP server
        String username = "hrishiofficial01@gmail.com"; // Your Gmail ID
        String password = "pull juyi qeyh rgcw"; // Gmail app password (NOT your login password)

        // Set properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject
            message.setSubject("Automation Test Report - Google Test");

            // Create message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hi,\n\nPlease find the attached Automation Test Report.\n\nThanks,\nAutomation Team");

            // Create Multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Attach the ExtentReport.html
            messageBodyPart = new MimeBodyPart();
            String filename = "test-output/ExtentReport.html";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(new File(filename).getName());
            multipart.addBodyPart(messageBodyPart);

            // Complete the message
            message.setContent(multipart);

            // Send the message
            Transport.send(message);
            System.out.println("✅ Email Sent Successfully with Test Report!");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
