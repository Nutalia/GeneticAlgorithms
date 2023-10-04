package lab1;

public class SimpleGeneticAlgorithm {
    static private final double lowBorder = -10.;
    static private final double highBorder = 10.;
    private final int chromosomeLength = 15;
    private final double realMaxValue = 10.6869;

    private Population population;
    private final GeneticOperators operators;
    private final int maxGenerationNumber;
    private double obtainedMaxValue;

    public SimpleGeneticAlgorithm(int populationSize, double crossingoverProbability, double mutationProbability, int maxGenerationNumber) {
        this.population = new Population(populationSize);
        this.operators = new GeneticOperators(crossingoverProbability, mutationProbability);
        this.maxGenerationNumber = maxGenerationNumber;
        this.obtainedMaxValue = Double.NEGATIVE_INFINITY;
        BeingEvaluation.lowBorder = lowBorder;
        BeingEvaluation.highBorder = highBorder;
    }

    public GAResult algorithm() {
        double startTimestamp = System.currentTimeMillis();
        GAResult result = new GAResult();
        population.generatePopulation(chromosomeLength);
        findMaxFitnessFunctionValue();
        result.addToLists(population, obtainedMaxValue, 0);
        int currentGeneration;
        for (currentGeneration = 1; currentGeneration <= maxGenerationNumber; currentGeneration++) {
            population = operators.reproduction(population);
            population = operators.crossingover(population);
            population = operators.mutation(population);
            population = operators.reduction(population);
            findMaxFitnessFunctionValue();
            result.addToLists(population, obtainedMaxValue, currentGeneration);
            if (Math.abs(realMaxValue - obtainedMaxValue) <= 0.001) {
                break;
            }
        }
        double finishTimestamp = System.currentTimeMillis();
        result.durationInMillis = finishTimestamp - startTimestamp;
        return result;
    }

    private void findMaxFitnessFunctionValue() {
        obtainedMaxValue = Double.NEGATIVE_INFINITY;
        for(String being : population) {
            double fitnessFunctionValue = BeingEvaluation.fitnessFunctionValueOfBeing(being);
            if (fitnessFunctionValue > obtainedMaxValue) {
                obtainedMaxValue = fitnessFunctionValue;
            }
        }
    }
}
