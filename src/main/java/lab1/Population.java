package lab1;

import java.util.ArrayList;

public class Population extends ArrayList<String>{
    private int populationSize;

    public Population(int size) {
        super();
        this.populationSize = size;
    }

    public Population(Population other) {
        super(other);
        this.populationSize = other.populationSize;
    }

    public int getSize() {
        return populationSize;
    }

    public void generatePopulation(int chromosomeLength) {
        clear();
        for (int i = 0; i < populationSize; i++) {
            String being = generateBeing(chromosomeLength);
            add(being);
        }
    }

    private String generateBeing(int chromosomeLength) {
        StringBuilder being = new StringBuilder();
        for (int j = 0; j < chromosomeLength; j++) {
            if (Math.random() < 0.5) {
                being.append("0");
            } else {
                being.append("1");
            }
        }
        return being.toString();
    }

    public String chooseRandomBeing() {
        int beingIndex = (int) (Math.random() * size());
        return get(beingIndex);
    }

    public String chooseRandomBeingAndRemove() {
        int beingIndex = (int)(Math.random() * size());
        return remove(beingIndex);
    }

    public void reduceToOriginalSize() {
        subList(0,populationSize);
    }
}
