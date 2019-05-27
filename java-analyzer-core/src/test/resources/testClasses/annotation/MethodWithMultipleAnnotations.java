import cucumber.api.java.en.When;
import org.junit.Test;

public class MethodWithMultipleAnnotations {
    @Test
    @When("Method with multiple annotations")
    public void methodWithMultipleAnnotations() {
        System.out.println("Hi from tis method");
    }
}
