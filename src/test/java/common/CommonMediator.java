package common;

import environment.AppiumSession;
import io.appium.java_client.ComparesImages;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.imagecomparison.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntry;
import pages.JoinPage;
import plugins.AudioFingerprint;
import plugins.FFmpeg;
import plugins.SoundBoard;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public class CommonMediator {
    public static AppiumSession hostSession;
    public static AppiumSession guestSession;

    public static MobileDriver<MobileElement>hostDriver;
    public static MobileDriver<MobileElement>guestDriver;

    public static MobileDriver<MobileElement>currentDriver;
    public static String currentRole;
    public static String roomUrl;

    public static File actualScreenshotFile;
    public static Properties prop;
    static {
        try {
            InputStream input = new FileInputStream("config.properties");
            prop = new Properties();
            // load a properties file
            prop.load(input);
            roomUrl = prop.getProperty("roomURL");

            // setup host appium server and driver
            hostSession = new AppiumSession("host", prop.getProperty("hostAppiumServerPort"), prop.getProperty("hostDeviceUDID"));
            hostSession.startAppiumServer();
            hostDriver = hostSession.startDriver();

            // setup guest appium server and driver
            guestSession = new AppiumSession("guest", prop.getProperty("guestAppiumServerPort"), prop.getProperty("guestDeviceUDID"));
            guestSession.startAppiumServer();
            guestDriver = guestSession.startDriver();

            setCurrentDriver("host");
            // run the sound file to guest input channel before test beginning, sound file last 5 minutes, should cover the entire test run
            playSoundToMic(prop.getProperty("guestMicInputSourceId"));

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

            String hostComparisonFilePath = "src/test/screenshots/host_comparison.jpg";
            if (checkFileExists(hostComparisonFilePath)) {
                scenario.attach("host_comparison_screenshot(expected left, actual right)", "text/plain", "viz_screenshot_title");
                scenario.attach(readFileToByteArray(hostComparisonFilePath), "image/png", "viz_screenshot");
            }

            String guestComparisonFilePath = "src/test/screenshots/guest_comparison.jpg";
            if (checkFileExists("src/test/screenshots/guest_comparison.jpg")) {
                scenario.attach("guest_comparison_screenshot(expected left, actual right)", "text/plain", "viz_screenshot_title");
                scenario.attach(readFileToByteArray(guestComparisonFilePath), "image/png", "viz_screenshot");
            }
            CommonMediator.setCurrentDriver("host");
            joinPage().leaveCallRoomIfStillInside();
            CommonMediator.setCurrentDriver("guest");
            joinPage().leaveCallRoomIfStillInside();
        }
        hostDriver.closeApp();
        guestDriver.closeApp();
    }

    public static byte[] readFileToByteArray(String filePath) throws IOException {
        File fileToRead = new File(filePath);
        return Files.readAllBytes(fileToRead.toPath());
    }

        // use opencv to compare two images for similarity score (0 - 1)
    public static SimilarityMatchingResult compareImageBySimilarity() throws IOException {
        File ExpectedHostImageFile = new File("src/test/screenshots/expected_screenshot_"+currentRole+".jpg");

        actualScreenshotFile = ((TakesScreenshot)currentDriver).getScreenshotAs(OutputType.FILE);

        SimilarityMatchingResult result = ((ComparesImages) currentDriver)
                .getImagesSimilarity(actualScreenshotFile, ExpectedHostImageFile, new SimilarityMatchingOptions()
                        .withEnabledVisualization());
        Assert.assertTrue(result.getVisualization().length>0);
        Assert.assertTrue(result.getScore()>0);
        System.out.println(result.getScore());
        return result;
    }

    // wait until the current screen is very similar to previous success connection screenshot, then assume connection is success
    public static void waitUntilVideoConnected(Double minThreshold, long timeout) throws InterruptedException, IOException {
        SimilarityMatchingResult result;
        long startTime = System.currentTimeMillis();
        do {
            result = compareImageBySimilarity();
        } while(result.getScore() < minThreshold && (System.currentTimeMillis()-startTime<timeout));

        File viz = new File("src/test/screenshots/"+currentRole+"_comparison.jpg");
        result.storeVisualization(viz);

        Assert.assertTrue("actual score is " + result.getScore() + ", but expected minimum score is " + minThreshold, result.getScore() >= minThreshold);
    }

    public static void takeSampleScreenshot() throws IOException {
        File scrFile = ((TakesScreenshot)currentDriver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("src/test/screenshots/expected_screenshot_" + currentRole +".jpg"));
    }

    // use sound board to play sound to mic/input channel
    public static void playSoundToMic(String guestMicInputSourceId) throws InterruptedException {
        SoundBoard soundToMic = new SoundBoard(guestMicInputSourceId);
        Thread t = new Thread(soundToMic);
        t.start();

        // wait for SoundBoard thread to end on its own
        t.join();
    }

    // use ffmpeg to record specific channel
    public static void captureForDuration(File audioCapture, int durationMs) throws Exception {

        FFmpeg capture = new FFmpeg(audioCapture, Integer.parseInt(prop.getProperty(currentRole + "OutputDeviceNum")));
        Thread t = new Thread(capture);
        t.start();

        // wait for sufficient amount of song to play
        Thread.sleep(durationMs);

        // tell ffmpeg to stop sampling
        capture.stopCollection();

        // wait for ffmpeg thread to end on its own
        t.join();
    }

    public static File captureAudio(int timeoutMs, boolean isSample) throws Exception {
        File audioCapture;

        String filePath = "src/test/audios/"+currentRole + (".wav");
        if (checkFileExists(filePath)) {
            audioCapture = new File( filePath);
            audioCapture.delete();
        }
        audioCapture = new File(filePath);

        CommonMediator.captureForDuration(audioCapture, timeoutMs);
        return audioCapture;
    }

    // check the output is silent
    public static void assertAudioOutputSilence(Boolean isSilentCheck, int timeoutMs) throws Exception {
        // now we calculate the fingerprint of the freshly-captured audio...
        AudioFingerprint fp1 = AudioFingerprint.calcFP(captureAudio(timeoutMs, false));

        // as well as the fingerprint of our baseline audio...
        AudioFingerprint fp2 = AudioFingerprint.calcFP(new File("src/test/audios/noMusic.mp4"));
        // and compare the two
        double comparison = fp1.compare(fp2);
        System.out.println("comparison "+ comparison);
        // finally, we assert that the comparison is sufficiently strong

        Assert.assertTrue("actual comparison score is " + comparison + ", but silenceCheck is " + isSilentCheck, isSilentCheck ? comparison == 100 : comparison != 100);

    }
    public static List<LogEntry> orderLogEntries(List<LogEntry> oldEntries) {
        List<LogEntry> newEntries = new ArrayList<>();
        LogEntry tempEntry;
        for (LogEntry entry : oldEntries) {
            tempEntry = new LogEntry(entry.getLevel(), entry.getTimestamp(), entry.getMessage() + "\n");
            newEntries.add(tempEntry);
        }
        return newEntries;
    }

    public static void captureLog(String role)
            throws Exception {
        setCurrentDriver(role);
        System.out.println(role + ": Saving device log...");
        List<LogEntry> logEntries = currentDriver.manage().logs().get("logcat").filter(Level.ALL);
        logEntries = orderLogEntries(logEntries);
        File logFile = new File("src/test/logs/"+role +  "_logcat.txt");
        PrintWriter log_file_writer = new PrintWriter(logFile);
        log_file_writer.println(logEntries);
        log_file_writer.flush();
        System.out.println(role + ": Saving device log - Done.");
    }

    public static boolean checkFileExists(String filePath) {
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    public static JoinPage joinPage() {
        return new JoinPage(currentDriver);
    }
}
