package nl.terrax.camel.model;

public enum BeerType {
    LAGER("Lager"), ALE("Ale");

    private final String name;

    BeerType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

