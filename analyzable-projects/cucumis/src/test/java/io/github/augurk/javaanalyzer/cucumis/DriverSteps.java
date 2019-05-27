package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;
import io.github.augurk.javaanalyzer.cucumis.support.GardenerDriver;

public class DriverSteps {
    private GardenerDriver driver;

    public DriverSteps(GardenerDriver driver) {
        this.driver = driver;
    }

    @When("When entrypoint is invoked through a driver directly")
    public void whenEntrypointIsInvokedThroughADriverDirectly() {
        driver.waterPlants();
    }

    @When("When entrypoint is indirectly invoked through a driver")
    public void whenEntrypointIsIndirectlyInvokedThroughADriver() {
        driver.waterPlantsIndirectly();
    }
}
