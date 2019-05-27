package io.github.augurk.javaanalyzer.cucumis;

public class PickyGherkin extends Gherkin {
    @Override
    public void onWater(WaterEventArgs args) {
        if (args.isAcidFree()) {
            super.grow();
        } else {
            System.out.println("Gerkin: I cannot grow on this water.");
        }
    }

    @Override
    void cutVine() {
        super.cutVine();
        System.out.println("PickyGherkin: I believe it was the Gardener, in the veggy-garden with the scissors!");
    }
}
