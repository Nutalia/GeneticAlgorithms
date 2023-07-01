package lab1;

public class Main {
    public static void main(String[] args) {
        SimpleGeneticAlgorithm alg =
                new SimpleGeneticAlgorithm(100, 0.9, 0.5, 100);
        GAResult result = alg.algorithm();
        Visualisation.displayResults(result);
        int lastIndex = result.population.size() - 1;
        System.out.println("Поколение=" + result.generation.get(lastIndex) +
                ", max=" + result.maxValue.get(lastIndex) +
                ", время=" + result.durationInMillis + " мс\n");
    }
}
