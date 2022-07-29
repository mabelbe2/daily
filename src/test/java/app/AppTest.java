package app;

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
        glue = {"stepDefinitions"},
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
    public static void afterAll() { // quit driver so that remote server would not error out
//        final MobileDriver<MobileElement> driver = Common.CommonMediator.driver;
//        driver.quit();
    }
}
