import cucumber.api.java8.En;

public class SimpleClassWithSingleExpressionLambdaWhenStep implements En {
    public SimpleClassWithSingleExpressionLambdaWhenStep() {
        When("When with single expression lambda", () ->
            System.out.println("Hello from signle expression lambda when step"));
    }
}
