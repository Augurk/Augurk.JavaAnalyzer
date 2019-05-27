import cucumber.api.java.en.When;

public class WhenAnnotationWithTimeout {
    @When(value = "When annotation with timeout", timeout = 10)
    public void whenAnnotationWithTimeout() {

    }
}
