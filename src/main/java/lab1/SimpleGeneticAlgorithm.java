package lab1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class SimpleGeneticAlgorithm {
    private final double lowBorder = -10.;
    private final double highBorder = 10.;
    private final int chromosomeLength = 15;
    private final double real = 10.6869;

    private List<String> population;
    private int populationSize;
    private final double crossingoverProbability;
    private final double mutationProbability;
    private final int maxGenerationNumber;
    private double max;
    private int currentGeneration;

    public SimpleGeneticAlgorithm(int populationSize, double crossingoverProbability, double mutationProbability, int maxGenerationNumber) {
        population = null;
        this.populationSize = populationSize;
        this.crossingoverProbability = crossingoverProbability;
        this.mutationProbability = mutationProbability;
        this.maxGenerationNumber = maxGenerationNumber;
        this.max = Double.NEGATIVE_INFINITY;
        this.currentGeneration = 0;
    }

    public void algorithm() {
        double start = System.currentTimeMillis();
        generatePopulation(populationSize);
        for (currentGeneration = 0; currentGeneration < maxGenerationNumber; currentGeneration++) {
            reproduction();
            crossingover();
            mutation();
            reduction();
            findMaxFitnessFunctionValue();
            if (Math.abs(real - max) <= 0.001) {
                break;
            }
        }
        double finish = System.currentTimeMillis();
        System.out.println("Поколение=" + currentGeneration +
                ", max=" + max +
                ", время=" + (finish - start) + "ms");
    }

    private void generatePopulation(int size) {
        population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            StringBuilder newBeing = new StringBuilder();
            for (int j = 0; j < chromosomeLength; j++) {
                if (Math.random() < 0.5) {
                    newBeing.append("0");
                } else {
                    newBeing.append("1");
                }
            }
            population.add(newBeing.toString());
        }
    }

    private void reproduction() {
        List<String> parents = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            String being1 = chooseRandomBeing();
            String being2 = chooseRandomBeing();
            double fitnessFunctionValueOfBeing1 = fitnessFunctionValueOfBeing(being1);
            double fitnessFunctionValueOfBeing2 = fitnessFunctionValueOfBeing(being2);
            if (fitnessFunctionValueOfBeing1 > fitnessFunctionValueOfBeing2) {
                parents.add(being1);
            } else {
                parents.add(being2);
            }
        }
        population = parents;
    }

    private String chooseRandomBeing() {
        int beingIndex = (int) (Math.random() * population.size());
        return population.get(beingIndex);
    }

    private double fitnessFunctionValueOfBeing(String being) {
        return fitnessFunction(convertBinaryToDouble(being));
    }

    private double fitnessFunction(double x) {
        return (1.85 - x) * Math.cos(3.5 * x - 0.5);
    }

    private double convertBinaryToDouble(String chromosome) {
        int resultNumber = 0;
        for (int i = 0; i < chromosomeLength; i++) {
            resultNumber *= 2;
            if (chromosome.charAt(i) == '1')
                resultNumber += 1;
        }
        return lowBorder + resultNumber*(highBorder-lowBorder) / (Math.pow(2.,chromosomeLength)-1);
    }

    private void crossingover() {
        List<String> children = new ArrayList<>(population);
        while (population.size() > 1) {
            String parent1 = chooseRandomBeingAndRemove();
            String parent2 = chooseRandomBeingAndRemove();
            if (Math.random() <= crossingoverProbability) {
                List<String> newChildren = createChildren(parent1,parent2);
                children.addAll(newChildren);
            }
        }
        population = children;
    }

    private String chooseRandomBeingAndRemove() {
        int beingIndex = (int)(Math.random() * population.size());
        return population.remove(beingIndex);
    }

    private List<String> createChildren(String parent1, String parent2) {
        int crossingoverPoint = (int) (Math.random() * (chromosomeLength - 1));
        String child1 = createChild(parent1, parent2, crossingoverPoint);
        String child2 = createChild(parent2, parent1, crossingoverPoint);
        return List.of(child1,child2);
    }

    private String createChild(String parent1, String parent2, int crossingoverPoint) {
        return parent1.substring(0,crossingoverPoint) + parent2.substring(crossingoverPoint);
    }

    private void mutation() {
        List<String> mutatedPopulation = new ArrayList<>(population);
        for (String being : population) {
            if (Math.random() <= mutationProbability) {
                String mutatedBeing = mutateRandomBit(being);
                mutatedPopulation.add(mutatedBeing);
            }
        }
        population = mutatedPopulation;
    }

    private String mutateRandomBit(String being) {
        int bitIndex = (int) (Math.random() * chromosomeLength);
        StringBuilder newBeing = new StringBuilder(being.substring(0, bitIndex));
        if (being.charAt(bitIndex) == '0') {
            newBeing.append('1');
        } else {
            newBeing.append('0');
        }
        newBeing.append(being.substring(bitIndex + 1));
        return newBeing.toString();
    }

    private void reduction() {
        population.sort(this::comparisonFunction);
        population = population.subList(0, populationSize);
    }

    private int comparisonFunction(String x, String y) {
        if(fitnessFunctionValueOfBeing(x) > fitnessFunctionValueOfBeing(y)) {
            return -1;
        } else if(fitnessFunctionValueOfBeing(x) < fitnessFunctionValueOfBeing(y)) {
            return 1;
        }else {
            return 0;
        }
    }

    private void findMaxFitnessFunctionValue() {
        max = Double.NEGATIVE_INFINITY;
        for(String being : population) {
            double fitnessFunctionValue = fitnessFunctionValueOfBeing(being);
            if (fitnessFunctionValue > max) {
                max = fitnessFunctionValue;
            }
        }
    }

    public void display() {
        XYSeries series = new XYSeries("(1.85-x)cos(3.5x-0.5)");
        XYSeries chromosomes = new XYSeries("хромосомы");
        for (float i = -10; i <= 10 - 0.01; i += 0.01) {
            series.add(i, (1.85 - i) * Math.cos(3.5 * i - 0.5));
        }
        for (String being : population) {
            chromosomes.add(convertBinaryToDouble(being), fitnessFunction(convertBinaryToDouble(being)));
        }
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(series);
        xyDataset.addSeries(chromosomes);
        String str;
        if (currentGeneration == 0) {
            str = new String("Начальная популяция");
        } else str = new String("Поколение=" + currentGeneration);
        JFreeChart chart = ChartFactory
                .createXYLineChart(str + ", max=" + max, "x", "y",
                        xyDataset, PlotOrientation.VERTICAL,
                        true, true, true);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesShape(0, new Line2D.Double());
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesShape(1, new Ellipse2D.Double(0, 0, 3, 3));
        renderer.setSeriesLinesVisible(1, false);
        plot.setRenderer(renderer);
        JFrame frame =
                new JFrame("Лабораторная №1");
        frame.getContentPane()
                .add(new ChartPanel(chart));
        frame.setSize(1080, 720);
        frame.setLocation(200, 100);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        SimpleGeneticAlgorithm alg =
                new SimpleGeneticAlgorithm(100, 0.9, 0.5, 100);
        alg.algorithm();
    }
}
