package environment;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class DriverConfig {
    public static MobileDriver<MobileElement> setUpDriver(String role) throws MalformedURLException {
        String deviceName;

        if (role.equals("host")) {
            deviceName = "Pixel 3 XL API 30";
        } else {
            deviceName = "Pixel 5 API 30";
        }

        String platformName = "Android";
        String platformVersion = "Android 11.0";

        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "/src/test/appFiles");
        String fileName = "dailyPlayground.apk";
        File app = new File(appDir, fileName);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appActivity", "");
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("newCommandTimeout", "2400");
        caps.setCapability("app", app.getAbsolutePath());
        MobileDriver<MobileElement> driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"),
                caps);
        return driver;
    }
}
