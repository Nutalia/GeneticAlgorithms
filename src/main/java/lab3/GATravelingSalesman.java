package lab3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GATravelingSalesman {
    int[][] population; //города в представлении соседства, нумерация городов с 0
    int populationSize; //размер популяции
    int numberOfTowns; //количество городов
    int maxGeneration; //максимальное поколение
    double crossoverProbability; //вероятность кроссинговера
    double mutationProbability; //вероятность мутации
    double curMin = Integer.MAX_VALUE; //текущий мимнимум
    int genMin = 0; //поколение, в котором впервые найден текущий минимум
    int currentGeneration = 0; //текущее поколение
    boolean crossoverType; //тип кроссовера: true - обмен ребрами, false - эвристический
    private boolean mutationType; //тип мутации: true - перестановка городов, false - инверсия
    private final int inversionLength = 4; //длина инвертируемого подтура
    Random r = new Random();

    public GATravelingSalesman(int populationSize, double crossoverProbability, double mutationProbability,
                               int maxGeneration, boolean crossoverType, boolean mutationType) {
        TownCoordinates.getData();
        numberOfTowns = TownCoordinates.number;
        this.populationSize = populationSize;
        population = new int[populationSize][numberOfTowns];
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.maxGeneration = maxGeneration;
        this.crossoverType = crossoverType;
        this.mutationType = mutationType;

        for (int i = 0; i < populationSize; i++) {
            List<Integer> values = new ArrayList<>(); //массив номеров городов
            for (int j = 1; j < numberOfTowns; j++) {
                values.add(j);
            }
            int prevIdx = 0; //индекс, на который надо ставить очередной город
            while (!values.isEmpty()) {
                int idx = r.nextInt(values.size());
                population[i][prevIdx] = values.get(idx);
                prevIdx = values.get(idx);
                values.remove(idx);
            }
            population[i][prevIdx] = 0;
        }
    }

    private double edgeLength(int v1, int v2) {
        double x1 = TownCoordinates.x.get(v1);
        double x2 = TownCoordinates.x.get(v2);
        double y1 = TownCoordinates.y.get(v1);
        double y2 = TownCoordinates.y.get(v2);
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private double pathLength(int[] path) {
        double length = 0;
        int idx = 0;
        for (int i = 0; i < numberOfTowns; i++) {
            length += edgeLength(idx, path[idx]);
            idx = path[idx];
        }
        return length;
    }

    private List<int[]> reproduction() {
        List<int[]> parents = new ArrayList<>();
        int min = min();
        int number = 8;
        for (int i = 0; i < number; i++) {
            parents.add(population[min]);
        }
        for (int i = 0; i < populationSize - number; i++) {
            int idx1 = r.nextInt(populationSize);
            int idx2 = r.nextInt(populationSize);

            if (pathLength(population[idx1]) < pathLength(population[idx2])) {
                parents.add(population[idx1]);
            } else {
                parents.add(population[idx2]);
            }
        }
        return parents;
    }

    private int[] alternatingEdges(int[] parent1, int[] parent2) {
        int[] child = new int[numberOfTowns];
        for (int i = 0; i < numberOfTowns; i++) {
            child[i] = -1;
        }
        int idx = 0;
        int[] currentParent = parent1;
        for (int i = 0; i < numberOfTowns; i++) {
            int value = currentParent[idx];
            var sorted = child.clone(); //отсортированный массив для поиска городов
            Arrays.sort(sorted);
            while (Arrays.binarySearch(sorted, value) >= 0 || value == 0 && i != numberOfTowns - 1) {
                value = r.nextInt(numberOfTowns);
            }
            child[idx] = value;
            idx = value;
            if (currentParent == parent1) {
                currentParent = parent2;
            } else {
                currentParent = parent1;
            }
        }
        return child;
    }

    private int[] heuristicCrossover(int[] parent1, int[] parent2) {
        int[] child = new int[numberOfTowns];
        List<Integer> alreadySaw = new ArrayList<>();
        int idx = r.nextInt(numberOfTowns);
        int save = idx;
        while (alreadySaw.size() != numberOfTowns - 1) {
            if (edgeLength(idx, parent1[idx]) < edgeLength(idx, parent2[idx])) {
                int value = parent1[idx];
                while (alreadySaw.contains(value) || value == idx) {
                    value = parent1[r.nextInt(numberOfTowns)];
                }
                child[idx] = value;
                alreadySaw.add(idx);
                idx = value;
            } else {
                int value = parent2[idx];
                while (alreadySaw.contains(value) || value == idx) {
                    value = parent2[r.nextInt(numberOfTowns)];
                }
                child[idx] = value;
                alreadySaw.add(idx);
                idx = value;
            }
        }
        child[idx] = save;
        return child;
    }

    private int[][] crossover(List<int[]> parents) {
        int[][] newPopulation = new int[populationSize][numberOfTowns];
        int populationIdx = 0;
        while (!parents.isEmpty()) {
            int idx = r.nextInt(parents.size());
            int[] parent1 = parents.get(idx);
            parents.remove(idx);

            idx = r.nextInt(parents.size());
            int[] parent2 = parents.get(idx);
            parents.remove(idx);

            if (r.nextDouble(1) < crossoverProbability) {
                int[] child1;
                int[] child2;
                if (crossoverType) {
                    child1 = alternatingEdges(parent1, parent2);
                    child2 = alternatingEdges(parent2, parent1);
                } else {
                    child1 = heuristicCrossover(parent1, parent2);
                    child2 = heuristicCrossover(parent2, parent1);
                }
                newPopulation[populationIdx++] = child1;
                newPopulation[populationIdx++] = child2;
            } else {
                newPopulation[populationIdx++] = parent1;
                newPopulation[populationIdx++] = parent2;
            }
        }
        return newPopulation;
    }

    private int[] swapTowns(int[] being) {
        int[] path = beingToPath(being);
        int idx1 = r.nextInt(numberOfTowns);
        int idx2;
        do {
            idx2 = r.nextInt(numberOfTowns);
        } while (idx2 == idx1);
        int tmp = path[idx1];
        path[idx1] = path[idx2];
        path[idx2] = tmp;
        return pathToBeing(path);
    }

    private int[] beingToPath(int[] being) {
        int[] path = new int[numberOfTowns];
        int idx = 0;
        for (int i = 0; i < numberOfTowns; i++) {
            path[i] = idx;
            idx = being[idx];
        }
        return path;
    }

    private int[] pathToBeing(int[] path) {
        int[] being = new int[numberOfTowns];
        int idx = path[0];
        int save = idx;
        for (int i = 0; i < numberOfTowns - 1; i++) {
            being[idx] = path[i + 1];
            idx = path[i + 1];
        }
        being[idx] = save;
        return being;
    }

    private int[] inversion(int[] being) {
        int[] path = beingToPath(being);
        int idx1 = r.nextInt(numberOfTowns);
        int[] inverted = new int[inversionLength];
        for (int j = 0; j < inversionLength; j++) {
            if (idx1 + j < numberOfTowns) {
                inverted[inversionLength - 1 - j] = path[idx1 + j];
            } else {
                inverted[inversionLength - 1 - j] = path[idx1 + j - numberOfTowns];
            }
        }
        for (int j = 0; j < inversionLength; j++) {
            if (idx1 + j < numberOfTowns) {
                path[idx1 + j] = inverted[j];
            } else {
                path[idx1 + j - numberOfTowns] = inverted[j];
            }
        }
        return pathToBeing(path);
    }

    private void mutation() {
        for (int i = 0; i < populationSize; i++) {
            if (r.nextDouble(1) < mutationProbability) {
                if (mutationType) {
                    population[i] = swapTowns(population[i]);
                } else {
                    population[i] = inversion(population[i]);
                }
            }
        }
    }

    private int min() {
        int idx = 0;
        for (int i = 1; i < populationSize; i++) {
            if (pathLength(population[i]) < pathLength(population[idx])) {
                idx = i;
            }
        }
        return idx;
    }

    public void runAlgorithm() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < maxGeneration + 1; i++) {
            int minIdx = min();
            if (pathLength(population[minIdx]) < curMin) {
                curMin = pathLength(population[minIdx]);
                genMin = i;
            }
            display(population[minIdx]);
            var parents = reproduction();
            population = crossover(parents);
            mutation();
            currentGeneration++;
        }
        System.out.println("time=" + (System.currentTimeMillis() - start) + "ms,");
        int idx = min();
        System.out.println("min=" + curMin + ", gen=" + genMin + ",\ntour=" + Arrays.toString(population[idx]));
    }

    public void display(int[] being) {
        JFrame frame = new JFrame("Лабораторная №3");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        JPanel panel = new MyPanel(being);
        panel.add(new Label("length=" + pathLength(being) + " generation=" + currentGeneration));
        frame.add(panel);
        frame.show();
    }

    public static void main(String[] args) {
        GATravelingSalesman ga = new GATravelingSalesman(1000, 0.9, 0.5,
                100, false, false);
        ga.runAlgorithm();
    }

    class MyPanel extends JPanel {
        int[] being;

        public MyPanel(int[] being) {
            this.being = being;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            double scale = 0.05; //8
            int offsetX = 800;
            int offsetY = 400;
            g.setColor(Color.RED);
            for (int j = 0; j < numberOfTowns; j++) {
                g.setColor(Color.RED);
                g.fillOval((int) (TownCoordinates.x.get(j).intValue() * scale) - offsetX,
                        (int) (TownCoordinates.y.get(j).intValue() * scale) - offsetY, 4, 4);
            }
            int idx = 0;
            for (int j = 0; j < numberOfTowns; j++) {
                g.setColor(Color.BLACK);
                g.drawLine((int) (TownCoordinates.x.get(idx).intValue() * scale) - offsetX,
                        (int) (TownCoordinates.y.get(idx).intValue() * scale) - offsetY,
                        (int) (TownCoordinates.x.get(being[idx]).intValue() * scale) - offsetX,
                        (int) (TownCoordinates.y.get(being[idx]).intValue() * scale) - offsetY);
                idx = being[idx];
            }
        }
    }
}
