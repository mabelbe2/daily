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
import java.net.URL;
import java.util.HashMap;

public class AppiumSession {
    public MobileDriver<MobileElement> driver;
    public AppiumDriverLocalService service;
    public String role;

    public AppiumSession(String role) {
        this.role = role;
    }

    public void startAppiumServer() {
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "debug");
        builder.usingPort(role.equals("host")? 23861: 20452);
//        builder.usingAnyFreePort();
        builder.withAppiumJS(new File(System.getenv("APPIUM_PATH")));

        HashMap<String, String> environment = new HashMap();
        environment.put("PATH", "/usr/local/bin:" + System.getenv("PATH"));
        builder.withEnvironment(environment);
        builder.withLogFile(new File(role+"_appium_server.log"));
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
        if (role.equals("host")) {
            deviceName = "Pixel 5 API 31";
//            appPackage = "com.dailyplayground";
            appPackage = "co.daily.core.dailydemo";
            platformVersion = "Android 12.0";
            udid = "emulator-5554";
        } else {
//            deviceName = "Galaxy S9";
//            appPackage = "co.daily.core.dailydemo";
//            platformVersion = "Android 10.0";
//            udid = "58585a5441573398";
            System.out.println("");
            deviceName = "Pixel 4 XL API 31";
            appPackage = "co.daily.core.dailydemo";
            platformVersion = "Android 12.0";
            udid = "emulator-5556";
        }

        String platformName = "Android";

        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "/src/test/appFiles");
        String fileName = "dailyPlayground.apk";
        File app = new File(appDir, fileName);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appPackage", appPackage);
        caps.setCapability("appActivity", appPackage + ".MainActivity");
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("newCommandTimeout", "2400");
        caps.setCapability("autoGrantPermissions", "true");
//        if (role.equals("guest")) {
            caps.setCapability("udid", udid);
//        }
//        caps.setCapability("app", app.getAbsolutePath());
        MobileDriver<MobileElement> driver = new AndroidDriver<MobileElement>(service.getUrl(), caps);
//        MobileDriver<MobileElement> driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"),caps);
        this.driver = driver;
        return driver;
    }

    public void stopDriver() {
        driver.quit();
    }
}
