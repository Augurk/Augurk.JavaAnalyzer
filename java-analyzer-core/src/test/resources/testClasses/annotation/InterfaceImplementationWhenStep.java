import cucumber.api.java.en.When;

public class InterfaceImplementationWhenStep {
    @When("When step which calls interface")
    public void whenStepWithCall() {
        SayHello callable = new SayHelloImpl();
        callable.sayHello("SomeName");
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
