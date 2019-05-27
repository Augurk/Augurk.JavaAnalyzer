package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;

public class GenericSteps {
    @When("When a generic method is invoked")
    public void whenAGenericMethodIsInvoked() {
        Gardener gardener = new Gardener();
        gardener.harvest(new Melothria());
    }
}
