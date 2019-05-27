package io.github.augurk.javaanalyzer.cucumis.support;

import io.github.augurk.javaanalyzer.cucumis.Gherkin;
import io.github.augurk.javaanalyzer.cucumis.Person;
import io.github.augurk.javaanalyzer.cucumis.WaterEventArgs;

public class MockedGardener implements Person {
    public void plant()
    {
        // Do nothing to limit test output length
    }

    public void waterPlants()
    {
        new Gherkin().onWater(new WaterEventArgs());
    }
}
