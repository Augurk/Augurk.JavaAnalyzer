package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java8.En;
import io.github.augurk.javaanalyzer.cucumis.support.MockedGardener;

public class InterfaceLambdaSteps implements En {
    public InterfaceLambdaSteps() {
        When("When (lambda) entrypoint is an interface implementation", () -> {
            Person gardener = new Gardener();
            gardener.waterPlants();
        });

        When("When (lambda) entrypoint is invoked after invocation on interface", () -> {
            // First let the mocked gardener plant something
            Person gardener = new MockedGardener();
            gardener.plant();

            // Then let the real gardener water it
            new Gardener().waterPlants();
        });

        When("When (lambda) entrypoint is invoked through an interface implementation", () -> {
            // The mocked gardener implements an interface from the system
            // under test but the real entrypoint is the call it does on the
            // provided Gherkin.
            Person gardener = new MockedGardener();
            gardener.waterPlants();
        });

        When("When (lambda) this actually means that", () -> {
            Plant plant = new Melothria();
            // The FreezeAndThaw method uses the this-operator
            // to reference an abstract method
            plant.freezeAndThaw();
        });
    }
}
