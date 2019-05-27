package io.github.augurk.javaanalyzer.cucumis.support;

import io.github.augurk.javaanalyzer.cucumis.Gardener;
import io.github.augurk.javaanalyzer.cucumis.Person;

public class GardenerDriver {
    private final Gardener gardener;
    private final Person person;

    public GardenerDriver() {
        gardener = new Gardener();
        person = gardener;
    }

    public void waterPlants() {
        // The garderner is a concrete type, as such it will be further explored
        gardener.waterPlants();
    }

    public void waterPlantsIndirectly() {
        // The iGardener is only an interface, there is no way to find out which
        // concrete type has been put into it; as such it cannot be explored any
        // further
        person.waterPlants();
    }
}
