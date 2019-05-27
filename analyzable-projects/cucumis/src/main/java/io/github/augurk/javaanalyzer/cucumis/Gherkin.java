package io.github.augurk.javaanalyzer.cucumis;

public class Gherkin {
    public void onWater(WaterEventArgs args)
    {
        grow();
    }

    public void onPlant(PlantEventArgs args) {
        this.setInitialSize("Seed");
    }


    protected void grow() {
        System.out.println("Gherkin: I'm growing, weeee!");
    }

    void cutVine() {
        System.out.println("Gherkin: Someone just cut my vine! Goodbye cruel world!");
    }

    private void setInitialSize(String size) {
        // setInitialSize(size);
        System.out.printf("Gherkin: I'm size $s", size);
    }
}
