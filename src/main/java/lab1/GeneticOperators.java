package lab1;

import java.util.List;

public class GeneticOperators {
    double crossingoverProbability;
    double mutationProbability;

    public GeneticOperators(double crossingoverProbability, double mutationProbability) {
        this.crossingoverProbability = crossingoverProbability;
        this.mutationProbability = mutationProbability;
    }

    public Population reproduction(Population population) {
        Population parents = new Population(population.getSize());
        for (int i = 0; i < population.getSize(); i++) {
            String being1 = population.chooseRandomBeing();
            String being2 = population.chooseRandomBeing();
            if (BeingEvaluation.comparisonFunction(being1, being2) == -1) {
                parents.add(being1);
            } else {
                parents.add(being2);
            }
        }
        return parents;
    }

    public Population crossingover(Population population) {
        Population parentsAndChildren = new Population(population);
        while (population.size() > 1) {
            String parent1 = population.chooseRandomBeingAndRemove();
            String parent2 = population.chooseRandomBeingAndRemove();
            if (Math.random() <= crossingoverProbability) {
                List<String> children = createChildren(parent1,parent2);
                parentsAndChildren.addAll(children);
            }
        }
        return parentsAndChildren;
    }

    private List<String> createChildren(String parent1, String parent2) {
        int crossingoverPoint = (int) (Math.random() * (parent1.length() - 1));
        String child1 = createChild(parent1, parent2, crossingoverPoint);
        String child2 = createChild(parent2, parent1, crossingoverPoint);
        return List.of(child1,child2);
    }

    private String createChild(String parent1, String parent2, int crossingoverPoint) {
        return parent1.substring(0,crossingoverPoint) + parent2.substring(crossingoverPoint);
    }

    public Population mutation(Population population) {
        Population mutatedPopulation = new Population(population);
        for (String being : population) {
            if (Math.random() <= mutationProbability) {
                String mutatedBeing = mutateRandomBit(being);
                mutatedPopulation.add(mutatedBeing);
            }
        }
        return mutatedPopulation;
    }

    private String mutateRandomBit(String being) {
        int bitIndex = (int) (Math.random() * being.length());
        StringBuilder newBeing = new StringBuilder(being.substring(0, bitIndex));
        if (being.charAt(bitIndex) == '0') {
            newBeing.append('1');
        } else {
            newBeing.append('0');
        }
        newBeing.append(being.substring(bitIndex + 1));
        return newBeing.toString();
    }

    public Population reduction(Population population) {
        population.sort(BeingEvaluation::comparisonFunction);
        population.reduceToOriginalSize();
        return  population;
    }
}
