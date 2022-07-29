package stepDefinitions;

import Common.CommonMediator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class test {
    @Given("host opens the app")
    public void hostOpensTheApp() {
        CommonMediator.startPage().waitUntilPageLoaded();
    }

    @And("{word} clicks {string}")
    public void clicks(String role, String btnName) {
        CommonMediator.setCurrentDriver(role);
        CommonMediator.currentDriver = CommonMediator.hostDriver;
        if (btnName.equals("create demo room")) {
            CommonMediator.roomUrl = CommonMediator.startPage().createDemoRoomUrl();
            System.out.println(CommonMediator.roomUrl);
        } else {
            CommonMediator.startPage().clickJoinCall();
        }

    }

    @When("guest opens the app")
    public void guestOpensTheApp() {
    }

    @And("guest paste in the demo room link")
    public void guestPasteInTheDemoRoomLink() {
        CommonMediator.startPage().pasteInRoomUrl(CommonMediator.roomUrl);
    }

}
