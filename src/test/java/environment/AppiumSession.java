package environment;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.junit.Assert;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;

public class AppiumSession {
    public MobileDriver<MobileElement> driver;
    public AppiumDriverLocalService service;
    public String role;
    public int port;
    public String deviceUDID;

    public AppiumSession(String role, String portToRunAppiumServer, String deviceUDID) {
        this.role = role;
        this.port = Integer.parseInt(portToRunAppiumServer);
        this.deviceUDID = deviceUDID;
    }

    public void startAppiumServer() { // need APPIUM_PATH and NODE_PATH environment variable set
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "debug");
        builder.usingPort(this.port);
        builder.withAppiumJS(new File(System.getenv("APPIUM_PATH")));

        HashMap<String, String> environment = new HashMap();
        environment.put("PATH", "/usr/local/bin:" + System.getenv("PATH"));
        builder.withEnvironment(environment);
        builder.withLogFile(new File("src/test/logs/"+role+"_appium_server.log"));
        service = AppiumDriverLocalService.buildService(builder);
        service.clearOutPutStreams();
        service.start();
        Assert.assertTrue(service.isRunning());
    }

    public void stopAppiumServer() {
        service.stop();
    }

    public MobileDriver<MobileElement> startDriver() throws MalformedURLException {
        String deviceName;
        String appPackage;
        String platformVersion;
        String udid;
        if (role.equals("host")) { // use virtual audio : output sunflower 2ch, mic blackhole 16ch
            deviceName = "Pixel 5 API 31";
            appPackage = "co.daily.core.dailydemo";
            platformVersion = "Android 12.0";
            udid = this.deviceUDID; // this is the unique id for appium to select to right device

        } else { // use virtual mic: output blackhole 64ch, mic blackhole 2ch

            System.out.println("");
            deviceName = "Pixel 4 XL API 31";
            appPackage = "co.daily.core.dailydemo";
            platformVersion = "Android 12.0";
            udid = this.deviceUDID;
        }

        String platformName = "Android";

        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "/src/test/appFiles");
        String fileName = "dailyDemo.apk";
        File app = new File(appDir, fileName);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appPackage", appPackage);
        caps.setCapability("appActivity", appPackage + ".MainActivity");
//        caps.setCapability("app", app.getAbsolutePath());
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("newCommandTimeout", "2400");
        caps.setCapability("autoGrantPermissions", "true");
        caps.setCapability("udid", udid);

        MobileDriver<MobileElement> driver = new AndroidDriver<MobileElement>(service.getUrl(), caps);
        this.driver = driver;
        return driver;
    }

    public void stopDriver() {
        driver.quit();
    }
}
