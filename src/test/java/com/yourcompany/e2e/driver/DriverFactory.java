package com.yourcompany.e2e.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver getDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Configurações para rodar liso no CI/CD e Localmente
        options.addArguments("--remote-allow-origins=*");

        // Verifica se passamos a propriedade -Dheadless=true no Maven
        String headless = System.getProperty("headless");
        if ("true".equals(headless)) {
            options.addArguments("--headless=new"); // Modo sem janela
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        return new ChromeDriver(options);
    }
}