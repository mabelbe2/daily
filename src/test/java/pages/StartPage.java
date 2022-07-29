package pages;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;


public class StartPage extends BasePage{
    @AndroidFindBy(accessibility = "robots-room-url")
    MobileElement roomUrlField;

    @AndroidFindBy(accessibility = "robots-create-room")
    MobileElement createDemoRoomBtn;

    @AndroidFindBy(accessibility = "robots-start-call")
    MobileElement joinRoomBtn;

    public StartPage (MobileDriver<MobileElement> driver) {
        super(driver);
    }

    public void waitUntilPageLoaded() {
        longWait.until(ExpectedConditions.visibilityOfElementLocated(MobileBy.AccessibilityId("robots-start-call")));
    }

    public String createDemoRoomUrl() {
        createDemoRoomBtn.click();
        wait.until(ExpectedConditions.attributeContains(roomUrlField, "text", "https"));
        return roomUrlField.getText();
    }

    public void clickJoinCall() {
        joinRoomBtn.click();
    }

    public void pasteInRoomUrl(String url) {
        roomUrlField.sendKeys(url);
    }
}
