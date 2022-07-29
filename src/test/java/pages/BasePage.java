package pages;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    MobileDriver<MobileElement> driver;
    WebDriverWait wait;
    WebDriverWait longWait;

    public BasePage(MobileDriver<MobileElement> driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 3000);
        longWait = new WebDriverWait(driver, 5000);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(60)), this);

    }
}
