import cucumber.api.java8.En;

public class SimpleClassWithGivenLambda implements En {
    public SimpleClassWithGivenLambda() {
        Given("Given expression", () -> System.out.println("Given body"));
    }
}
