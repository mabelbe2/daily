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

    }

    @And("{word} clicks {string}")
    public void clicks(String role, String btnName) {
        CommonMediator.setCurrentDriver(role);
        CommonMediator.joinPage().clickJoinCall();

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

    @And("guest wait until host camera on")
    public void guestWaitUntilHostCameraOn() throws InterruptedException, IOException {
        CommonMediator.setCurrentDriver("guest");
        CommonMediator.joinPage().waitUntilHostVideoLoaded();
    }


    @And("verify {word} side video call started when reached minimum similarity threshold {string} within {string} seconds")
    public void verifyVideoCallStartedBySimilarityThresholdWithinSeconds(String role, String minThresholdStr, String timeoutStr) throws IOException, InterruptedException {
        CommonMediator.setCurrentDriver(role);
        Double minThreshold = Double.parseDouble(minThresholdStr);
        long timeout = Long.parseLong(timeoutStr)*1000;
        CommonMediator.waitUntilVideoConnected(minThreshold, timeout);
    }

    @And("verify {word} should be able to hear {word} audio input within {string} seconds")
    public void verifyHostSideAudioSimilarityWithSampleHasMinimumScoreWithinMs(String role, String unusedRole, String audioTimeoutStr) throws Exception {
        CommonMediator.setCurrentDriver(role);
        CommonMediator.assertAudioOutputSilence(false, Integer.parseInt(audioTimeoutStr)*1000);

    }

    @And("screenshot {word} after {int} second wait")
    public void screenshotHostAfterSecondWait(String role, int seconds) throws IOException, InterruptedException {
        Thread.sleep(seconds*1000);
        CommonMediator.setCurrentDriver(role);
        CommonMediator.takeSampleScreenshot();
    }

    @And("record {word} sample for {int} seconds")
    public void recordHostForSeconds(String role, int sec) throws Exception {
        CommonMediator.setCurrentDriver(role);
        CommonMediator.captureAudio(sec*1000, true);
    }

    @And("{word} mutes own mic")
    public void hostMutesOwnMicAndGuestShouldNotBeAbleToHearAnyAudio(String firstRole) throws Exception {
        CommonMediator.setCurrentDriver(firstRole);
        CommonMediator.joinPage().muteSelf();
        Thread.sleep(2000); // 2 sec wait for host to stop hearing guest audio
    }

    @And("{word} leaves video call room")
    public void hostLeavesVideoCallRoom(String firstRole) {
        CommonMediator.setCurrentDriver(firstRole);
        CommonMediator.joinPage().leaveCallRoom();
    }
}
