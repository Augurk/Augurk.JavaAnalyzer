package io.github.augurk.javaanalyzer.cucumis.support;

import io.github.augurk.javaanalyzer.cucumis.Gardener;

public class InheritedGardener extends Gardener {
    public void plantGherkinAndWaterIt() {
        plantGherkin();
        waterPlants();
    }
}
