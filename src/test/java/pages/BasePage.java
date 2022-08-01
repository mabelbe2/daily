package pages;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BasePage {
    public MobileDriver<MobileElement> driver;
    public WebDriverWait wait;
    public WebDriverWait longWait;

    public BasePage(MobileDriver<MobileElement> driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 10);
        longWait = new WebDriverWait(driver, 60);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(60)), this);
    }

    public boolean isElementVisible(By androidBy) {
        List<MobileElement> elementList = driver.findElements(androidBy);
        if (elementList.size() > 0) {
            MobileElement element = elementList.get(0);
            return element.isDisplayed();
        }
        return false;
    }

    public void assertElementVisible(By androidBy) {
        Assert.assertTrue(isElementVisible(androidBy));
    }

    public void assertElementVisible(MobileElement element) {
        Assert.assertTrue(element.isDisplayed());
    }

    public void assertElementNotVisible(By androidBy) {
        Assert.assertFalse(isElementVisible(androidBy));
    }

    public void assertElementNotVisible(MobileElement element) {
        Assert.assertFalse(element.isDisplayed());
    }

}
