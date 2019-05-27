import cucumber.api.java8.En;

public class WhenLambdaWithTimeout implements En {
    public WhenLambdaWithTimeout() {
        When("When lambda with timeout", 10, () -> {
            System.out.println("Hi from when lambda with timeout");
        });
    }
}
