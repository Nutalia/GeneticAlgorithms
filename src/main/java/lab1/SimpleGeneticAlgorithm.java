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
import java.util.Comparator;
import java.util.List;

public class SimpleGeneticAlgorithm {
    private List<String> population = new ArrayList<>();
    private final double crossingoverProbability;
    private final double mutationProbability;
    private final double lowBorder = -10.;
    private final double highBorder = 10.;
    private final int maxGenerationNumber;
    private final int chromosomeLength = 15;
    private final double real = -8.84076;
    private double max;
    private int currentGeneration = 0;

    public SimpleGeneticAlgorithm(int maxGenerationNumber, int populationSize, double crossingoverProbability, double mutationProbability) {
        this.maxGenerationNumber = maxGenerationNumber;
        this.crossingoverProbability = crossingoverProbability;
        this.mutationProbability = mutationProbability;
        for (int i = 0; i < populationSize; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < chromosomeLength; j++) {
                if (Math.random() < 0.5) {
                    str.append("0");
                } else {
                    str.append("1");
                }
            }
            population.add(str.toString());
        }
    }

    private double toDouble(String chromosome) {
        int number = 0;
        for (int i = 0; i < chromosomeLength; i++) {
            number *= 2;
            if (chromosome.charAt(i) == '1')
                number += 1;
        }
        return lowBorder + number * (highBorder - lowBorder) / (Math.pow(2., 15.) - 1);
    }

    private double function(double x) {
        return (1.85 - x) * Math.cos(3.5 * x - 0.5);
    }

    public List<String> reproduction() {
        List<String> parents = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            int being1 = (int) (Math.random() * population.size());
            int being2 = (int) (Math.random() * population.size());
            if (function(toDouble(population.get(being1))) > function(toDouble(population.get(being2)))) {
                parents.add(population.get(being1));
            } else {
                parents.add(population.get(being2));
            }
        }
        return parents;
    }

    public List<String> crossingover(List<String> parents) {
        List<String> children = new ArrayList<>();
        while (!parents.isEmpty()) {
            String firstParent = parents.remove((int) (Math.random() * parents.size()));
            String secondParent = parents.remove((int) (Math.random() * parents.size()));
            if (Math.random() >= crossingoverProbability) {
                int k = (int) (Math.random() * (chromosomeLength - 1));
                String firstChild = firstParent.substring(0, k) + secondParent.substring(k);
                String secondChild = secondParent.substring(0, k) + firstParent.substring(k);
                children.add(firstChild);
                children.add(secondChild);
            } else {
                children.add(firstParent);
                children.add(secondParent);
            }
        }
        return children;
    }

    public void mutation() {
        for (String being : population) {
            if (Math.random() >= mutationProbability) {
                int k = (int) (Math.random() * chromosomeLength);
                StringBuilder newBeing = new StringBuilder(being.substring(0, k));
                if (being.charAt(k) == '0') {
                    newBeing.append('1');
                } else {
                    newBeing.append('0');
                }
                newBeing.append(being.substring(k + 1));
            }
        }
    }

    public void algorithm() {
        var maxOptional = population.stream().max(Comparator.comparing(x -> function(toDouble(x))));
        max = maxOptional.map(this::toDouble).orElse(2 * highBorder);
        display();
        double start = System.currentTimeMillis();
        for (int i = 0; i < maxGenerationNumber; i++) {
            List<String> parents = reproduction();
            population = crossingover(parents);
            mutation();
            maxOptional = population.stream().max(Comparator.comparing(x -> function(toDouble(x))));
            max = maxOptional.map(this::toDouble).orElse(2 * highBorder);
            currentGeneration++;
            display();
            if (Math.abs(real - max) <= 0.001) {
                break;
            }
        }

        System.out.println("Поколение=" + currentGeneration +
                ", max=" + max +
                ", время=" + (System.currentTimeMillis() - start) + "ms");
    }

    public void display() {
        XYSeries series = new XYSeries("(1.85-x)cos(3.5x-0.5)");
        XYSeries chromosomes = new XYSeries("хромосомы");
        for (float i = -10; i <= 10 - 0.01; i += 0.01) {
            series.add(i, (1.85 - i) * Math.cos(3.5 * i - 0.5));
        }
        for (String being : population) {
            chromosomes.add(toDouble(being), function(toDouble(being)));
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
                new SimpleGeneticAlgorithm(100, 200, 0.9, 0.5);
        alg.algorithm();
    }
}
