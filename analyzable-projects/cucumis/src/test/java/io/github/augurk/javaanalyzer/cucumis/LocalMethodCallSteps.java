package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;

public class LocalMethodCallSteps {
    @When("When a local method is called within the entrypoint")
    public void whenALocalMethodIsCalledWithinTheEntrypoint() {
        Gherkin gherkin = new Gherkin();
        // The OnWater will make a local call without the "this." identifier
        gherkin.onWater(new WaterEventArgs());
    }

    @When("When an explicit base method is called within the entrypoint")
    public void whenAnExplicitBaseMethodIsCalledWithinTheEntrypoint() {
        Gherkin gherkin = new PickyGherkin();
        // The OnWater will make a base call with the "base." identifier
        gherkin.onWater(new WaterEventArgs());
    }

    @When("When a local method is called within the entrypoint explicitly on this")
    public void WhenALocalMethodIsCalledWithinTheEntrypointExplicitlyOnThis() {
        Gherkin gherkin = new Gherkin();
        gherkin.onPlant(new PlantEventArgs());
    }
}
