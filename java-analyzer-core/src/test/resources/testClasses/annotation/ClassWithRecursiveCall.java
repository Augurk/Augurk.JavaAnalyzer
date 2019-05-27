import cucumber.api.java.en.When;

public class ClassWithRecursiveCall {
    @When("Class with recursive call")
    public void calssWithRecursiveCall() {
        var person = new RecursivePerson();
        person.sayHello();
    }
}

public class RecursivePerson {
    public void sayHello() {
        sayHello();
        System.out.println("Hello, world!");
    }
}
