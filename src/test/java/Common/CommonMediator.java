package Common;

import environment.DriverConfig;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pages.StartPage;

public class CommonMediator {
    public static MobileDriver<MobileElement>hostDriver;
    public static MobileDriver<MobileElement>guestDriver;

    public static MobileDriver<MobileElement>currentDriver;
    public static String roomUrl;

    static {
        try {
            hostDriver = DriverConfig.setUpDriver("host");
//            guestDriver = DriverConfig.setUpDriver("guest");
            guestDriver = null;
            currentDriver = hostDriver;
        } catch (Exception e) {
            System.out.print("Timeout exception" + e);
            throw new RuntimeException();
        }
    }

    public static void setCurrentDriver(String role) {
        if (role.equals("host")) {
            currentDriver = hostDriver;
        } else {
            currentDriver = guestDriver;
        }
    }
    @Before()
    public void beforeScenarioLaunchApp() {
        currentDriver.launchApp();
    }

    @After()
    public void afterScenarioCloseApp(Scenario scenario) throws Exception {
        if (scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) currentDriver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "screenshot");
            String pageSource = currentDriver.getPageSource();

            scenario.attach(pageSource, "text/plain", "pageSource");
        }
    }
    public static StartPage startPage() {
        return new StartPage(currentDriver);
    }
}
