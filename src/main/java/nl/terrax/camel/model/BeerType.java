package nl.terrax.camel.model;

public enum BeerType {
    LAGER("Lager"), ALE("Ale");

    private final String name;

    private BeerType(String name) {
        this.name = name;
    }


    public String toString() {
        return this.name;
    }
}

