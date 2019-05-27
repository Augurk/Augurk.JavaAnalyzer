package io.github.augurk.javaanalyzer.cucumis;

import java.util.List;

import cucumber.api.java.en.When;

public class ComplexSteps {
    @When("When entrypoint calls list.of function with primitives")
    public void whenFunctionIsCalledWithListOfWithPrimitives() {
        var gardener = new Gardener();
        waterPlants(gardener, List.of(1, 2));
    }

    private void waterPlants(Gardener gardener, List<Integer> numberOfPlants) {
        int sum = numberOfPlants.stream().mapToInt(i -> i).sum();

        for (int i = 0; i < sum; i++) {
            gardener.water(new Melothria());
        }
    }
}
