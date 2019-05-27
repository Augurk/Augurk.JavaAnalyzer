package io.github.augurk.javaanalyzer.cucumis;

public class StubbornGherkin extends PickyGherkin {
    @Override
    void cutVine() {
        super.cutVine();
        // I don't do printLine!
        System.out.print("StubbornGherkin: Fine, cut the vine! If that's how you want it...");
    }
}
