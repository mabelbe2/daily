package pages;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.BasePage;


public class JoinPage extends BasePage {
    @AndroidFindBy(id = "call_button")
    MobileElement joinBtn;

    @AndroidFindBy(id = "aurl")
    MobileElement roomUrlField;

    @AndroidFindBy(id = "local_video_view_container")
    MobileElement guestVideoContainer;

    By placeholderTextBeforeConnectionLocator = By.id("remote_camera_mask_view");

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

    public void assertHostCameraOn() {
        // enter link placeholder is not there

        // host video is
    }

    public void assertGuestCameraOn() {
        // guest container is there
        assertElementVisible(guestVideoContainer);
        // guest video is not placeholder
        assertElementNotVisible(placeholderTextBeforeConnectionLocator);
        // guest image matches expected
    }

}
