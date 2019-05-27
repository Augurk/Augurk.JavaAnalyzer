import cucumber.api.java.en.When;

public class SimpleClassWithWhenStepAndCall {
    @When("When step with call")
    public void whenStepWithCall() {
        var callable = new CallableClazz();
        callable.sayHello();
    }
}

public class CallableClazz {
    public void sayHello() {
        System.out.println("Hello from callable");
    }
}
