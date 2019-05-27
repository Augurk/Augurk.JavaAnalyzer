import cucumber.api.java.en.When;

public class WhenStepUsingInterfaceImplementationInSameClass {
    @When("When step using interface implementation")
    public void whenStepUsingInterfaceImplementationInSameClass() {
        SayHelloImpl target = new SayHelloImpl();
        target.sayHello();
    }
}

public interface SayHello {
    void sayHello();
    void sayHello(String name);
}

public class SayHelloImpl implements SayHello {
    public void sayHello() {
        sayHello("name");
    }

    public void sayHello(String name) {
        System.out.println("Hello, " + name + "!");
    }
}

