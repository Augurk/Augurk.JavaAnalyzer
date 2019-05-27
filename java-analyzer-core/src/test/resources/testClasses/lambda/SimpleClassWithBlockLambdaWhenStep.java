import cucumber.api.java8.En;

public class SimpleClassWithBlockLambdaWhenStep implements En {
    public SimpleClassWithBlockLambdaWhenStep() {
        When("When step with block lambda expression", () -> {
            System.out.println("Hello from lambda when step");
        });
    }
}
