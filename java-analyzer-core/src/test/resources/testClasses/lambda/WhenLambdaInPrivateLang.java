import cucumber.api.java8.En_pirate;

public class WhenLambdaInPrivateLang implements En_pirate {
    public WhenLambdaInPrivateLang() {
        Blimey("This is a pirate when step", () -> {});
    }
}
