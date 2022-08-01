package common;

import environment.AppiumSession;
import io.appium.java_client.ComparesImages;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.imagecomparison.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pages.JoinPage;
import pages.host.StartPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CommonMediator {
    public static AppiumSession hostSession;
    public static AppiumSession guestSession;

    public static MobileDriver<MobileElement>hostDriver;
    public static MobileDriver<MobileElement>guestDriver;

    public static MobileDriver<MobileElement>currentDriver;
    public static String currentRole;
    public static String roomUrl = "https://mabelbe.daily.co/mabeltest1032";

    public static byte[] actualScreenshot = null;
    public static File actualScreenshotFile;
    static {
        try {
            hostSession = new AppiumSession("host");
            hostSession.startAppiumServer();
            hostDriver = hostSession.startDriver();

            guestSession = new AppiumSession("guest");
            guestSession.startAppiumServer();
            guestDriver = guestSession.startDriver();

//            guestSession = null;
//            guestDriver = null;

            setCurrentDriver("host");
        } catch (Exception e) {
            System.out.print("Timeout exception" + e);
            if (hostSession != null && hostSession.service.isRunning()) {
                hostSession.stopAppiumServer();
            }
            if (guestSession != null && guestSession.service.isRunning()) {
                guestSession.stopAppiumServer();
            }
            throw new RuntimeException();
        }
    }

    public static void setCurrentDriver(String role) {
        currentRole = role;
        if (role.equals("host")) {
            currentDriver = hostDriver;
        } else {
            currentDriver = guestDriver;
        }
    }
    @Before()
    public void beforeScenarioLaunchApp() {
        hostDriver.launchApp();
        guestDriver.launchApp();
    }

    @After()
    public void afterScenarioCloseApp(Scenario scenario) throws Exception {
        if (scenario.isFailed()) {
            final byte[] hostScreenshot = ((TakesScreenshot) hostDriver)
                    .getScreenshotAs(OutputType.BYTES);

            final byte[] guestScreenshot = ((TakesScreenshot) guestDriver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach("host_screenshot", "text/plain", "host_screenshot_title");
            scenario.attach(hostScreenshot, "image/png", "host_screenshot");
            scenario.attach("guest_screenshot", "text/plain", "guest_screenshot_title");
            scenario.attach(guestScreenshot, "image/png", "guest_screenshot");

            if (checkFileExists("viz.jpg")) {
                scenario.attach("viz_screenshot(expected left, actual right)", "text/plain", "viz_screenshot_title");
                scenario.attach(readFileToByteArray("viz.jpg"), "image/png", "viz_screenshot");
            }
//            FileUtils.writeByteArrayToFile(actualScreenshotFile, actualScreenshot);
//            scenario.attach(actualScreenshot, "image/png", "screenshot");

//            byte[] expectedScreenshot = CommonMediator.readExpectedScreenShotFileToByteArray();
//            scenario.attach(expectedScreenshot, "image/png", "screenshot");

            String pageSource = currentDriver.getPageSource();

            scenario.attach(pageSource, "text/plain", "pageSource");


        } else {
//            File scrFile = ((TakesScreenshot)hostDriver).getScreenshotAs(OutputType.FILE);
//            FileUtils.copyFile(scrFile, new File("screenshot_host.jpg"));
//
//            scrFile = ((TakesScreenshot)guestDriver).getScreenshotAs(OutputType.FILE);
//            FileUtils.copyFile(scrFile, new File("screenshot_guest.jpg"));

//            File hostExpectedScreenshotFile = new File("screenshot_host.jpg");
//            byte[] hostExpectedScreenshot = Files.readAllBytes(hostExpectedScreenshotFile.toPath());
//            compareScreenshot(hostDriver, hostExpectedScreenshot);
//
//            File guestExpectedScreenshotFile = new File("screenshot_guest.jpg");
//            byte[] guestExpectedScreenshot = Files.readAllBytes(guestExpectedScreenshotFile.toPath());
//            compareScreenshot(guestDriver, guestExpectedScreenshot);
        }
        hostDriver.closeApp();
        guestDriver.closeApp();
    }

    public static byte[] readFileToByteArray(String filePath) throws IOException {
        File fileToRead = new File(filePath);
        return Files.readAllBytes(fileToRead.toPath());
    }

    public static SimilarityMatchingResult compareImageBySimilarity() throws IOException {
        File ExpectedHostImageFile = new File("screenshot_"+currentRole+".jpg");

        actualScreenshotFile = ((TakesScreenshot)currentDriver).getScreenshotAs(OutputType.FILE);
//        byte[] screenshot1 = Base64.encodeBase64(actualScreenshot);
        byte[] ExpectedHostImage = Files.readAllBytes(ExpectedHostImageFile.toPath());
        SimilarityMatchingResult result = ((ComparesImages) currentDriver)
                .getImagesSimilarity(actualScreenshotFile, ExpectedHostImageFile, new SimilarityMatchingOptions()
                        .withEnabledVisualization());
        Assert.assertTrue(result.getVisualization().length>0);
        Assert.assertTrue(result.getScore()>0);
        System.out.println(result.getScore());
        return result;
    }

    public static void waitUntilVideoConnected(Double minThreshold, long timeout) throws InterruptedException, IOException {
        SimilarityMatchingResult result;
        long startTime = System.currentTimeMillis();
        do {
            result = compareImageBySimilarity();
        } while(result.getScore() < minThreshold && (System.currentTimeMillis()-startTime<timeout));

        File viz = new File("viz.jpg");
        result.storeVisualization(viz);

//        File scrFile = ((TakesScreenshot)currentDriver).getScreenshotAs(OutputType.FILE);
//        FileUtils.copyFile(scrFile, new File("screenshot_" + currentRole +".jpg"));

        Assert.assertTrue("actual score is " + result.getScore() + ", but expected minimum score is " + minThreshold, result.getScore() >= minThreshold);
    }

    public static void compareTwoImage(byte[] image, byte[] imageToCompare) {
//        byte[] screenshot = Base64.encodeBase64(((TakesScreenshot)currentDriver).getScreenshotAs(OutputType.BYTES));
        FeaturesMatchingResult result = ((ComparesImages) currentDriver)
                .matchImagesFeatures(image, imageToCompare, new FeaturesMatchingOptions()
                        .withDetectorName(FeatureDetector.ORB)
                        .withGoodMatchesFactor(40)
                        .withMatchFunc(MatchingFunction.BRUTE_FORCE_HAMMING)
                        .withEnabledVisualization());
        Assert.assertTrue(result.getVisualization().length > 0);
        Assert.assertTrue(result.getCount() > 0);
        Assert.assertTrue(result.getTotalCount() > 0);
        Assert.assertFalse(result.getPoints1().isEmpty());
        Assert.assertNotNull(result.getRect1());
        Assert.assertFalse(result.getPoints2().isEmpty());
        Assert.assertNotNull(result.getRect2());
    }


    public static boolean checkFileExists(String filePath) {
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    public static StartPage startPage() {
        return new StartPage(currentDriver);
    }

    public static JoinPage joinPage() {
        return new JoinPage(currentDriver);
    }
}
