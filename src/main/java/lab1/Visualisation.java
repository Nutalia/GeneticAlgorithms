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

public class Visualisation {
    static public void displayResults(GAResult result) {
        int size = result.population.size();
        for(int i = 0; i < size; i++) {
            display(result.population.get(i), result.generation.get(i), result.maxValue.get(i));
        }
    }
    static private void display(Population population, int currentGeneration, double maxValue) {
        XYSeries chartLine = createChart();
        XYSeries chromosomes = createChromosomes(population);

        XYSeriesCollection xyDataset = new XYSeriesCollection();
        xyDataset.addSeries(chartLine);
        xyDataset.addSeries(chromosomes);

        String generation;
        if (currentGeneration == 0) {
            generation = new String("Начальная популяция");
        } else {
            generation = new String("Поколение=" + currentGeneration);
        }

        JFreeChart chart = ChartFactory
                .createXYLineChart(generation + ", max=" + maxValue, "x", "y",
                        xyDataset, PlotOrientation.VERTICAL,
                        true, true, true);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        configureChart(renderer);
        configureChromosomes(renderer);
        plot.setRenderer(renderer);

        configureAndShowFrame(chart);
    }

    static XYSeries createChart() {
        XYSeries chart = new XYSeries("(1.85-x)cos(3.5x-0.5)");
        for (double i = -10; i <= 10 - 0.01; i += 0.01) {
            chart.add(i, BeingEvaluation.fitnessFunction(i));
        }
        return chart;
    }

    static XYSeries createChromosomes(Population population) {
        XYSeries chromosomes = new XYSeries("хромосомы");
        for (String being : population) {
            chromosomes.add(BeingEvaluation.convertBinaryToDouble(being), BeingEvaluation.fitnessFunctionValueOfBeing(being));
        }
        return chromosomes;
    }

    static void configureChart(XYLineAndShapeRenderer renderer) {
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesShape(0, new Line2D.Double());
    }

    static void configureChromosomes(XYLineAndShapeRenderer renderer) {
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesShape(1, new Ellipse2D.Double(0, 0, 3, 3));
        renderer.setSeriesLinesVisible(1, false);
    }

    static void configureAndShowFrame(JFreeChart chart) {
        JFrame frame =
                new JFrame("Лабораторная №1");
        frame.getContentPane()
                .add(new ChartPanel(chart));
        frame.setSize(1080, 720);
        frame.setLocation(200, 100);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
