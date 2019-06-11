import cucumber.api.java.Before;
import cucumber.api.java.en.When;

public class CreateAndCallMethodWithObject {
    private OrderService service;

    @Before
    public void setup() {
        service = new OrderService();
    }

    @When("Create object and call method")
    public void objectCreationAndMethodCall() {
        Order order = new Order();
        service.save(order);
    }
}

public class OrderService {
    public void save(Order order) {
        System.out.println("Save order");
    }
}

public class Order { }
