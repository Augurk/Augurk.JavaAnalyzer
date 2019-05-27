import cucumber.api.java8.En;

public class WrongDefinedLambdaWhenStep implements En {
    public WrongDefinedLambdaWhenStep() {
        When("string1", "string2");
    }
}
