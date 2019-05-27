import cucumber.api.java.en.When;

public class ClassWithMethodCallsWithinClass {
    @When("Method calls within class")
    public void methodCallsWithinClass() {
        methodCall1();
    }

    private void methodCall1() {
        methodCall2("Method 1");
    }

    private void methodCall2(String name) {
        var sayHello = new SayHello();
        sayHello.sayHello(name);
    }
}

public class SayHello {
    public void sayHello(String name) {
        System.out.println("Hello, " + name);
    }
}
