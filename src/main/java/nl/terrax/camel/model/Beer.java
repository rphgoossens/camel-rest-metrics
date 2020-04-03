package nl.terrax.camel.model;

public class Beer {

    private String name;
    private BeerType type;

    public Beer() {
    }

    public Beer(String name, BeerType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BeerType getType() {
        return type;
    }

    public void setType(BeerType type) {
        this.type = type;
    }
}
