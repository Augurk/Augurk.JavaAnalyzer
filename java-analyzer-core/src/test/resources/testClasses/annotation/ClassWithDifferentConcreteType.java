import cucumber.api.java.en.When;

public class ClassWithDifferentConcreteType {
    @When("Different concrete type")
    public void differentConcreteType() {
        var person = new PickyPerson();
        sayHello(person);
    }

    private void sayHello(Person person) {
        person.sayHello();
    }
}

public class Person {
    public void sayHello() {
        System.out.println("Hello world");
    }
}

public class PickyPerson extends Person {
    @Override
    public void sayHello() {
        System.out.printf("Hello word from picky person");
    }
}
