package lab1;

import java.util.ArrayList;
import java.util.List;

public class GAResult {
    public final List<Population> population;
    public final List<Double> maxValue;
    public final List<Integer> generation;
    public double durationInMillis;

    public GAResult() {
        this.population = new ArrayList<>();
        this.maxValue = new ArrayList<>();
        this.generation = new ArrayList<>();
        this.durationInMillis = 0;
    }

    public void addToLists(Population population, double maxValue, int generation) {
        this.population.add(population);
        this.maxValue.add(maxValue);
        this.generation.add(generation);
    }
}
