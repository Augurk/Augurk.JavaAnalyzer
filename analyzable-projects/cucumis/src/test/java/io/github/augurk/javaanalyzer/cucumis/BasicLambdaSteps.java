package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java8.En;

public class BasicLambdaSteps implements En {
    public BasicLambdaSteps() {
        When("When (lambda) entrypoint is invoked directly", () -> {
            var gardener = new Gardener();
            gardener.plantGherkin();
        });

        When("When (lambda) entrypoint is surrounded by other invocations", () -> {
            System.out.println("Invoking implementation");
            var gardener = new Gardener();
            gardener.plantGherkin();
            System.out.println("End invocation");
        });

        When("When (lambda) two separate entrypoints are invoked", () -> {
            var gardener = new Gardener();
            gardener.plantGherkin();
            gardener.waterPlants();
        });
    }
}
