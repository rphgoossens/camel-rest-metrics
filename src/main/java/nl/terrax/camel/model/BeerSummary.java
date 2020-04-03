package nl.terrax.camel.model;

public class BeerSummary {

    private String name;
    private int total;

    public BeerSummary() {
    }

    public BeerSummary(String name, int total) {
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
