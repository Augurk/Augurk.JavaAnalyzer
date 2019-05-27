import cucumber.api.java.en.When;

public class ClassImplementingInterface {
    @When("When step using interface implementation")
    public void whenStepUsingInterfaceImplementation() {
        SayHelloImpl target = new SayHelloImpl();
        target.sayHello("name");
    }
}

public interface SayHello {
    void sayHello(String name);
}

public class SayHelloImpl implements SayHello {
    public void sayHello(String name) {
        System.out.println("Hello, " + name + "!");
    }
}

