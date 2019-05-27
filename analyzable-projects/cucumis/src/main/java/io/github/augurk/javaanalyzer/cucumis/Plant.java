package io.github.augurk.javaanalyzer.cucumis;

public abstract class Plant {
    public void water(Gardener gardener) {
        gardener.water(this);
    }

    public void freezeAndThaw() {
        // Not really frost-resistant, unless proven otherwise
        this.wither();
    }

    public void bloom() {
        // After 10 minutes:
        wither();
    }

    public void prune() {
        // Default implementation does nothing
    }

    public abstract void wither();
}
