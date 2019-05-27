package io.github.augurk.javaanalyzer.cucumis;

public class Melothria extends Plant {
    @Override
    public void wither() {
        rot();
    }

    private void rot() {
        System.out.println("Melothria: Oh no! I'm rotting! Save yourselves!");
    }
}
