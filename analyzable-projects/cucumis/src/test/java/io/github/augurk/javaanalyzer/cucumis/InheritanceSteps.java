package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;
import io.github.augurk.javaanalyzer.cucumis.support.InheritedGardener;

public class InheritanceSteps {
    @When("When entrypoint is invoked on inherited automation class")
    public void whenEntrypointIsInvokedOnInheritedAutomationClass() {
        // The InheritedGardener is defined within the test namespace
        new InheritedGardener().plantGherkin();
    }

    @When("When entrypoint is invoked through an inherited automation class")
    public void whenEntrypointIsInvokedThroughAnInheritedAutomationClass() {
        // The InheritedGardener is so much more efficient
        new InheritedGardener().plantGherkinAndWaterIt();
    }

    @When("When same method is invoked with different concrete types")
    public void whenSameMethodIsInvokedWithDifferentConcreteTypes() {
        var gardener = new Gardener();
        gardener.harvestGherkin(new Gherkin());
        gardener.harvestGherkin(new PickyGherkin());
    }

    @When("When an instance method is invoked from its base")
    public void whenAnInstanceMethodIsInvokedFromItsBase() {
        // The Bloom method is only defined on the base
        new Melothria().bloom();
    }

    @When("When a base method is called from a far off generations")
    public void whenABaseMethodIsCalledFromFarOffGenerations() {
        Gherkin gherkin = new StubbornGherkin();
        gherkin.cutVine();
    }

    @When("When this actually means that")
    public void whenThisActuallyMeansThat() {
        Plant plant = new Melothria();
        // The FreezeAndThaw method uses the this-operator
        // to reference an abstract method
        plant.freezeAndThaw();
    }

    @When("When an inherited instance method is invoked indirectly")
    public void whenAnInheritedInstanceMethodIsInvokedIndirectly() {
        PickyGherkin gherkin = new PickyGherkin();
        prepareAndCutVine(gherkin);
    }

    private static void prepareAndCutVine(Gherkin gherkin) {
        cutTheVine(gherkin);
    }

    private static void cutTheVine(Gherkin gherkin) {
        gherkin.cutVine();
    }
}
