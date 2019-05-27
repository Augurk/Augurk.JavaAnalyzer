import cucumber.api.java.en.When;

public class SimpleClassWithWhenStep {
    @When("When step without call")
    public void whenStepWithoutCall() {

    }

    public void thisMethodShouldNotBePickedUp() {

    }
}
