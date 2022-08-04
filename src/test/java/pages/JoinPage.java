package pages;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class JoinPage extends BasePage {
    @AndroidFindBy(id = "call_button")
    MobileElement joinBtn;

    @AndroidFindBy(id = "aurl")
    MobileElement roomUrlField;

    @AndroidFindBy(id = "input_mic_button")
    MobileElement micToggleBtn;

    By leaveBtnLocator = By.id("hangup_button");

    public JoinPage (MobileDriver<MobileElement> driver) {
        super(driver);
    }

    public void waitUntilPageLoaded() {
        longWait.until(ExpectedConditions.visibilityOfElementLocated(MobileBy.id("call_button")));
    }

    public void clickJoinCall() {
        wait.until(ExpectedConditions.elementToBeClickable(joinBtn));
        joinBtn.click();
    }

    public void pasteInRoomUrl(String url) {
        roomUrlField.sendKeys(url);
    }

    public void waitUntilHostVideoLoaded() {
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("remote_camera_mask_view")));
    }

    public void muteSelf() {
        micToggleBtn.click();
    }

    public void leaveCallRoom() {
        driver.findElement(leaveBtnLocator).click();
        joinBtn.isDisplayed();
    }

    public void leaveCallRoomIfStillInside() {
        if (driver.findElements(leaveBtnLocator).size() > 0) {
            leaveCallRoom();
        }
    }
}
