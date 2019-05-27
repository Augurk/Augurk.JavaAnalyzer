import cucumber.api.java.en.When;

public class ClassWithIndirectInterfaceCall {
    private final PersonDriver driver;

    @When("Class with indirect interface call")
    public void indirectInterfaceCall() {
        driver.sayHiIndirectly();
    }
}

public interface Person {
    void sayHi();
}

public class PersonImpl implements Person {
    public void sayHi() {
        System.out.println("hi");
    }
}

public class PersonDriver {
    private final Person person;

    public void sayHiIndirectly() {
        person.sayHi();
    }
}
