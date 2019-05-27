package io.github.augurk.javaanalyzer.cucumis;

import cucumber.api.java.en.When;
import io.github.augurk.javaanalyzer.annotations.AutomationTarget;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;

public class AntiPatternSteps {
    // This when step actually tests the logic of the Wither method on Plant but we cannot invoke it directly, thus we go through FreezeAndThaw
    @When("When the automated code cannot be invoked directly")
    @AutomationTarget(declaringType = Melothria.class, targetMethod = "wither")
    public void whenTheAutomatedCodeCannotBeInvokedDirectly() {
        // Withering a plant requires a lot of setup, thus it is easier to just invoke it directly
        Plant plant = new Melothria();
        plant.freezeAndThaw();
    }

    // This when step actually tests the logic of the Water method on Gardener that takes a single Plant, but we cannot invoke it directly, thus we go through Water on Plant.
    @When("When only the top level overload should match")
    @AutomationTarget(declaringType = Gardener.class, targetMethod = "water", overloadHandling = OverloadHandling.FIRST)
    public void whenOnlyTheTopLevelOverloadShouldMatch() {
        Plant plant = new Melothria();
        plant.water(new Gardener());
    }

    // This when step actually tests the logic of the Water method on Gardener that takes multiple Plants, but we cannot invoke it directly, thus we go through Water on Plant.
    @When("When only the lowest level overload should match")
    @AutomationTarget(declaringType = Gardener.class, targetMethod = "water", overloadHandling = OverloadHandling.LAST)
    public void WhenOnlyTheLowestLevelOverloadShouldMatch() {
        // Watering multiple plants requires a lot of setup, thus it is easier to test with only 1 plant, but we know the actual implementation is in the lowest overload
        Plant plant = new Melothria();
        plant.water(new Gardener());
    }

    // This when step actually tests the logic of the Water method on Gardener that takes multiple Plants, but we cannot invoke it directly, thus we go through Water on Plant.
    @When("When all overloads should match")
    @AutomationTarget(declaringType = Gardener.class, targetMethod = "water", overloadHandling = OverloadHandling.ALL)
    public void WhenAllOverloadsShouldMatch() {
        // Watering multiple plants requires a lot of setup, thus it is easier to test with only 1 plant, but we know the actual implementation is in the lowest overload
        Plant plant = new Melothria();
        plant.water(new Gardener());
    }
}
