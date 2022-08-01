package stepDefinitions;

import common.CommonMediator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class test {
    @Given("host opens the app")
    public void hostOpensTheApp() throws IOException {
        CommonMediator.setCurrentDriver("host");
        CommonMediator.joinPage().waitUntilPageLoaded();

//        CommonMediator.compareIdenticalScreenshot();
    }

    @And("{word} clicks {string}")
    public void clicks(String role, String btnName) {
        CommonMediator.setCurrentDriver(role);
//        CommonMediator.currentDriver = CommonMediator.hostDriver;
//        if (btnName.equals("create demo room")) {
//            CommonMediator.roomUrl = CommonMediator.startPage().createDemoRoomUrl();
//            System.out.println(CommonMediator.roomUrl);
//        } else {
            CommonMediator.joinPage().clickJoinCall();
//        }

    }

    @When("guest opens the app")
    public void guestOpensTheApp() {
        CommonMediator.setCurrentDriver("guest");
        CommonMediator.joinPage().waitUntilPageLoaded();
    }

    @And("{word} paste in the demo room link")
    public void pasteInTheDemoRoomLink(String role) {
        CommonMediator.setCurrentDriver(role);
        CommonMediator.joinPage().pasteInRoomUrl(CommonMediator.roomUrl);
    }

//    @And("guest clicks {string}")
//    public void guestClicks(String btnName) {
//        if(btnName.equals("join")) {
//            CommonMediator.joinPage().clickJoinCall();
//        }
//    }

    @And("guest wait until host camera on")
    public void guestWaitUntilHostCameraOn() throws InterruptedException, IOException {
        CommonMediator.setCurrentDriver("guest");
        CommonMediator.joinPage().waitUntilHostVideoLoaded();

//        Thread.sleep(5000);
//        File scrFile = ((TakesScreenshot)CommonMediator.currentDriver).getScreenshotAs(OutputType.FILE);
//        FileUtils.copyFile(scrFile, new File("screenshot_" + CommonMediator.currentRole +".jpg"));
    }


    @And("verify {word} side video call started when reached minimum similarity threshold {string} within {string} ms")
    public void verifyVideoCallStartedBySimilarityThresholdWithinSeconds(String role, String minThresholdStr, String timeoutStr) throws IOException, InterruptedException {
        CommonMediator.setCurrentDriver(role);
        Double minThreshold = Double.parseDouble(minThresholdStr);
        long timeout = Long.parseLong(timeoutStr);
        CommonMediator.waitUntilVideoConnected(minThreshold, timeout);
    }

}
