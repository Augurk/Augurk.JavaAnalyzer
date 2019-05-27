import cucumber.api.java.en.When;

public class ClassWithAbstractMethod {
    @When("Class with abstract method")
    public void classWithAbstractMethod() {
        var callable = new ChildClass();
        callable.sayHello("Pete");
    }
}

public abstract class ParentClass {
    public void sayHelloBase(String name) {
        this.greet(name);
    }

    public abstract void greet(String name);
}

public class ChildClass extends ParentClass {
    public void sayHello(String name) {
        super.sayHelloBase(name);
    }

    @Override
    public void greet(String name) {
        System.out.println("Hello, " + name);
    }
}
