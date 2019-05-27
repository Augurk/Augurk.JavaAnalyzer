import java.util.List;

public class UsingListOf {
    public void usingListOf() {
        var callable = new Callable();
        callable.setList(List.of(1, 2)); // List.of call with primative types
    }
}

public class Callable {
    // Analyzer should step into this function
    public void setList(List<Integer> list) {
        System.out.println("Hello from set list");
    }
}
