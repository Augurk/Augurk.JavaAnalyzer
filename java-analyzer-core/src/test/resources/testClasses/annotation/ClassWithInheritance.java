import cucumber.api.java.en.When;

public class ClassWithInheritance {
    @When("Class with inheritance")
    public void methodWithCalledOnInheritanceClass() {
        ParentClass target = new ChildClass();
        target.sayHello();
    }
}

public abstract class ParentClass {
    public abstract void sayHello();
}

public class ChildClass extends ParentClass {
    @Override
    public void sayHello() {
        System.out.println("Hello, world!");
    }
}
