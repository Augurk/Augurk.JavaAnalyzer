package io.github.augurk.javaanalyzer.cucumis;

import java.util.List;

public class Gardener implements Person {
    @Override
    public void plant() {
        System.out.println("Gardener: I planted a plant, because I it was in my job description.");
    }

    public void plantGherkin() {
        System.out.println("Gardener: I just planted a gherkin!");
    }

    @Override
    public void waterPlants() {
        System.out.println("Gardener: I just watered the plants!");
    }

    void water(List<Plant> plantsToWater) {
        System.out.printf("Gardener: I just watered %d plants.\n", plantsToWater.size());
    }

    void water(Plant plantToWater) {
        water(List.of(plantToWater));
    }

    public void harvestGherkin(Gherkin gherkin) {
        System.out.println("Gardener: I am about to harvest a gherkin!");
        gherkin.cutVine();
    }

    public <T extends Plant> void harvest(T plant){
        System.out.printf("Gardener: I am about to harvest a %s\n", plant.getClass());
        plant.prune();
    }
}
