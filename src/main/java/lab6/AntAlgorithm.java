package lab6;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class AntAlgorithm {
    private List<List<Double>> matrix; //матрица смежности вершин
    private double[][] pheromones; //феромоны на дугах
    private int[][] paths; //пути, которые строят муравьи
    private double alpha; //коэффициент концентрации феромона
    private double rho; //коэффициент испарения феромона
    private int populationSize; //количество муравьев
    private int townNumber; //количество городов
    private int currentGeneration; //текущее поколение
    private int maxGeneration; //максимальное количество поколений
    List<Double> x = new ArrayList<>(); //x-координаты городов
    List<Double> y = new ArrayList<>(); //y-координаты городов
    private Random random = new Random();

    public AntAlgorithm(int populationSize, double alpha, double rho, int maxGeneration) {
        fillMatrix();
        this.populationSize = populationSize;
        this.townNumber = matrix.size();
        pheromones = new double[townNumber][townNumber];
        paths = new int[populationSize][townNumber];
        this.alpha = alpha;
        this.rho = rho;
        this.maxGeneration = maxGeneration;

        clearPaths();
    }

    private void fillMatrix() {
        if (Files.exists(Paths.get("src/main/resources/data.txt"))) {
            try {
                var lines = Files.lines(Paths.get("src/main/resources/data.txt")).toList();
                for (String line : lines) {
                    var characters = line.split("\s");
                    x.add(Double.parseDouble(characters[1]));
                    y.add(Double.parseDouble(characters[2]));
                }
                matrix = new ArrayList<>();
                for (int i = 0; i < x.size(); i++) {
                    matrix.add(new ArrayList<>());
                }
                for(int i = 0; i < x.size(); i++) {
                    for (int j = 0; j < x.size(); j++) {
                        Double x1 = x.get(i);
                        Double x2 = x.get(j);
                        Double y1 = y.get(i);
                        Double y2 = y.get(j);
                        Double dist = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
                        matrix.get(i).add(dist);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearPaths() {
        for(int i = 0; i < populationSize; i++) {
            for(int j = 0; j < townNumber; j++) {
                paths[i][j] = -1;
            }
        }
    }

    private void initialization() {
        double sum = 0;
        for(int i = 0; i < townNumber; i++) {
            for(int j = i + 1; j < townNumber; j++) {
                sum += matrix.get(i).get(j);
            }
        }
        double rang = sum/(townNumber*(townNumber - 1)*2);
        for(int i = 0; i < townNumber; i++) {
            for(int j = i + 1; j < townNumber; j++) {
                double value;
                if(matrix.get(i).get(j) < rang) {
                    value = random.nextDouble(0.025, 0.05);
                } else {
                    value = random.nextDouble(0.025);
                }
                pheromones[i][j] = value;
                pheromones[j][i] = value;
            }
        }
    }

    private int chooseVertex(int current, int ant) {
        double sum = 0; //то, что стоит в знаменателе формулы
        for(int i = 1; i < townNumber; i++) {
            if(!containsVertex(paths[ant], i) && i != current) {
                sum += Math.pow(pheromones[current][i], alpha);
            }
        }
        List<Integer> vertexes = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        for(int i = 1; i < townNumber; i++) {
            //работаем только с вершинами, которые не посещались
            if(!containsVertex(paths[ant], i) && i != current) {
                double probability = Math.pow(pheromones[current][i], alpha) / sum;
                vertexes.add(i);
                probabilities.add(probability);
            }
        }
        sort(probabilities, vertexes);
        double rand = random.nextDouble(1);
        double prob = 0;
        for(int i = 0; i < probabilities.size(); i++) {
            prob += probabilities.get(i);
            if(prob >= rand) {
                int vertex = vertexes.get(i);
                paths[ant][current] = vertex;
                return vertex;
            }
        }
        int vertex = vertexes.get(vertexes.size() - 1);
        paths[ant][current] = vertex;
        return vertex;
    }

    private void sort(List<Double> probabilities, List<Integer> vertexes) {
        for(int i = 0; i < probabilities.size(); i++) {
            boolean flag = true;
            for(int j = 0; j < probabilities.size() - 1 - i; j++) {
                if(probabilities.get(j) > probabilities.get(j + 1)) {
                    flag = false;
                    swapD(probabilities, j);
                    swapI(vertexes, j);
                }
            }
            if(flag)
                return;
        }
    }

    private void swapD(List<Double> list, int idx) {
        double tmp1 = list.get(idx);
        double tmp2 = list.get(idx + 1);
        list.remove(idx + 1);
        list.remove(idx);
        list.add(idx, tmp2);
        list.add(idx + 1, tmp1);
    }

    private void swapI(List<Integer> list, int idx) {
        int tmp1 = list.get(idx);
        int tmp2 = list.get(idx + 1);
        list.remove(idx + 1);
        list.remove(idx);
        list.add(idx, tmp2);
        list.add(idx + 1, tmp1);
    }

    private boolean containsVertex(int[] path, int vertex) {
        for(int i = 0; i < townNumber; i++) {
            if(path[i] == vertex) {
                return true;
            }
        }
        return false;
    }

    private void evaporation() {
        for(int i = 0; i < townNumber; i++) {
            for(int j = i + 1; j < townNumber; j++) {
                pheromones[i][j] = (1 - rho)*pheromones[i][j];
                pheromones[j][i] = (1 - rho)*pheromones[j][i];
            }
        }
    }

    private double pathLength(int ant) {
        double res = 0;
        int current = 0;
        do {
            int vertex = paths[ant][current];
            res += matrix.get(current).get(vertex);
            current = vertex;
        } while(current != 0);
        return res;
    }

    private void marking(double delta, int ant) {
        int current = 0;
        do {
            int vertex = paths[ant][current];
            pheromones[current][vertex] += delta;
            pheromones[vertex][current] += delta;
            current = vertex;
        } while(current != 0);
    }

    public void display(int ant, double min) {
        JFrame frame = new JFrame("Лабораторная №6");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        JPanel panel = new MyPanel(Arrays.copyOf(paths[ant], townNumber));
        panel.add(new Label("length=" + min + " generation=" + currentGeneration));
        frame.add(panel);
        frame.show();
    }

    public void run() {
        //инициализация феромонов на дугах
        long start = System.currentTimeMillis();
        initialization();
        int minGeneration = -1;
        double minPath = -1;
        for(currentGeneration = 0; currentGeneration <= maxGeneration; currentGeneration++) {
            for (int i = 0; i < populationSize; i++) {
                int current = 0; //вершина, в которой находиться муравей на данной итерации
                //построение пути
                for (int j = 0; j < townNumber - 1; j++) {
                    current = chooseVertex(current, i);
                }
                paths[i][current] = 0;
            }
            evaporation(); //испарение феромона
            double min = -1;
            int minIdx = -1;
            for (int i = 0; i < populationSize; i++) {
                double length = pathLength(i);
                double delta = 1 / length;
                //изменение концентрации феромона
                marking(delta, i);
                if (min > length || minIdx == -1) {
                    min = length;
                    minIdx = i;
                }
            }
            //вывод особи с наименьшей длиной пути
            display(minIdx, min);
            if(min < minPath || minGeneration == -1) {
                minPath = min;
                minGeneration = currentGeneration;
            }
            clearPaths();
        }
        System.out.println("Поколение:" + minGeneration + "\nвремя:" + (System.currentTimeMillis() - start)
        + "ms\nзначение:" + minPath);
    }

    public static void main(String[] args) {
        AntAlgorithm antAlgorithm = new AntAlgorithm(200, 6, 0.3, 25);
        antAlgorithm.run();
    }

    class MyPanel extends JPanel {
        int[] ant;

        public MyPanel(int[] ant) {
            this.ant = ant;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            double scale = 0.05;
            int offsetX = 800;
            int offsetY = 400;
            g.setColor(Color.RED);
            for (int j = 0; j < townNumber; j++) {
                g.setColor(Color.RED);
                g.fillOval((int) (x.get(j).intValue() * scale) - offsetX,
                        (int) (y.get(j).intValue() * scale) - offsetY, 4, 4);
            }
            int idx = 0;
            for (int j = 0; j < townNumber; j++) {
                g.setColor(Color.BLACK);
                g.drawLine((int) (x.get(idx).intValue() * scale) - offsetX,
                        (int) (y.get(idx).intValue() * scale) - offsetY,
                        (int) (x.get(ant[idx]).intValue() * scale) - offsetX,
                        (int) (y.get(ant[idx]).intValue() * scale) - offsetY);
                idx = ant[idx];
            }
        }
    }
}
