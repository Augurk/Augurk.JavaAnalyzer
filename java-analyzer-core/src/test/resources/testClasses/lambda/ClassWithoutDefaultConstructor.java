import cucumber.api.java8.En;

public class ClassWithoutDefaultConstructor implements En {
    public ClassWithoutDefaultConstructor(String arg1) {
        When("Not a valid constructor", () -> {});
    }
}
