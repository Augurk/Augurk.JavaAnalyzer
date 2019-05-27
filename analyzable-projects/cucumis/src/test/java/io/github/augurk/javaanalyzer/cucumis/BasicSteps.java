package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;

public class BasicSteps {
    @When(value = "When entrypoint is invoked directly")
    public void whenEntryPointIsInvokedDirectly() {
        var gardener = new Gardener();
        gardener.plantGherkin();
    }

    @When("When entrypoint is surrounded by other invocations")
    public void whenEntryPointIsSurroundedByOtherInvocations() {
        System.out.println("Invoking implementation");
        var gardener = new Gardener();
        gardener.plantGherkin();
        System.out.println("End invocation");
    }

    @When("When two separate entrypoints are invoked")
    public void whenTwoEntryPointsAreInvoked() {
        var gardener = new Gardener();
        gardener.plantGherkin();
        gardener.waterPlants();
    }
}
