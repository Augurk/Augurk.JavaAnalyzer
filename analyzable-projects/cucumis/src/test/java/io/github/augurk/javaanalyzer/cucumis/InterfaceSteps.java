package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;
import io.github.augurk.javaanalyzer.cucumis.support.MockedGardener;

public class InterfaceSteps {
    @When("When entrypoint is an interface implementation")
    public void whenEntrypointIsAnInterfaceImplementation() {
        Person gardener = new Gardener();
        gardener.waterPlants();
    }

    @When("When entrypoint is invoked after invocation on interface")
    public void whenEntrypointIsInvokedAfterInvocationOnInterface() {
        // First let the mocked gardener plant something
        Person gardener = new MockedGardener();
        gardener.plant();

        // Then let the real gardener water it
        new Gardener().waterPlants();
    }

    @When("When entrypoint is invoked through an interface implementation")
    public void whenEntrypointIsInvokedThroughAnInterfaceImplementation() {
        // The mocked gardener implements an interface from the system
        // under test but the real entrypoint is the call it does on the
        // provided Gherkin.
        Person gardener = new MockedGardener();
        gardener.waterPlants();
    }
}
