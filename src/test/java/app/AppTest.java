package app;

import common.CommonMediator;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Unit test for simple App.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/features"},
        glue = {"stepDefinitions", "common"},
        plugin = {"pretty", "html:target/cucumber-report.html", "rerun:target/rerun.txt"},
        publish = true,
        monochrome = true
)

public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @AfterClass()
    public static void afterAll() throws Exception { // quit driver so that remote server would not error out
        System.out.println("ending sessions ....");
        if (CommonMediator.hostSession != null) {
            CommonMediator.captureLog("host");
            CommonMediator.hostSession.stopDriver();
            CommonMediator.hostSession.stopAppiumServer();
        }

        if (CommonMediator.guestSession != null) {
            CommonMediator.captureLog("guest");
            CommonMediator.guestSession.stopDriver();
            CommonMediator.guestSession.stopAppiumServer();
        }
    }
}
