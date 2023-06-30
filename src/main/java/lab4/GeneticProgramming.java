package lab4;

import java.util.*;
import java.util.List;

public class GeneticProgramming {
    private final String[] terminal = {"x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "2"}; //терминальное множество
    private final String[] functional = {"+", "^"}; //функциональное множество
    private List<String[]> population = new ArrayList<>(); //популяция
    private final int populationSize; //размер популяции
    private final double crossoverProbability; //вероятность кроссинговера
    private final double mutationProbability; //вероятность мутации
    private final int minHeight = 2; //минимально возможная максимальная глубина дерева
    private final int maxHeight = 7; //максимально возможная максимальная глубина дерева
    private final int maxGeneration; //максимальное поколение
    private final Random r = new Random();
    //значения переменных из обучающей выборки
    private static final double[][] variables = {{1.72, 0.195, 3.579, 0.957, 2.795, 1.079, 4.041, 3.537, 2.75, 4.485},
            {2.543, 4.769, 1.664, 0.431, 2.926, 3.399, 0.494, 1.936, 2.561, 1.171},
            {0.888, 0.323, 2.222, 0.09, 4.595, 0.011, 3.015, 0.125, 4.289, 4.744},
            {3.722, 2.079, 2.279, 5.073, 2.148, 3.998, 4.660, 4.939, 3.427, 3.489},
            {3.001, 3.266, 4.449, 4.583, 1.903, 1.205, 0.271, 3.171, 1.729, 2.106},
            {2.053, 1.945, 2.353, 2.883, 1.171, 4.371, 3.041, 4.621, 2.146, 1.825},
            {0.908, 0.433, 0.333, 3.626, 1.089, 2.404, 3.92, 3.514, 2.073, 4.452},
            {1.248, 0.976, 4.168, 4.321, 0.28, 3.844, 0.687, 3.759, 1.606, 2.289},
            {0.768, 2.219, 0.6, 4.589, 3.991, 4.214, 5.017, 0.9, 1.491, 4.539},
            {0.37, 1.49, 3.416, 4.812, 2.418, 3.867, 4.854, 1.996, 4.211, 1.491},
            {4.209, 2.948, 0.248, 2.836, 0.12, 0.46, 2.393, 0.235, 0.038, 2.484},
            {4.618, 1.515, 3.087, 2.204, 3.68, 2.883, 4.019, 4.941, 1.737, 0.06},
            {4.544, 0.285, 3.551, 1.695, 1.469, 1.909, 3.919, 4.06, 2.098, 3.237},
            {3.451, 4.379, 3.083, 2.94, 0.279, 4.592, 4.936, 0.812, 3.583, 2.55},
            {1.836, 3.536, 3.42, 3.935, 0.348, 1.042, 1.763, 4.872, 4.459, 0.83},
            {2.435, 0.503, 2.71, 3.888, 3.198, 4.031, 0.351, 3.698, 2.09, 3.046},
            {2.142, 0.954, 4.736, 4.697, 0.945, 1.707, 0.329, 4.271, 2.161, 4.657},
            {1.736, 2.185, 4.23, 4.3, 2.412, 4.625, 3.206, 0.431, 5.109, 1.396},
            {1.51, 0.128, 2.054, 3.996, 5.118, 0.303, 4.027, 0.173, 0.773, 0.927},
            {2.769, 1.13, 4.549, 0.654, 4.069, 4.288, 1.289, 2.84, 2.593, 2.885}};
    //требуемые значения из обучающей выборки
    private final double[] values = {82.215556, 64.201638, 76.95930999999999, 139.729374, 83.09704, 80.86027700000001,
            73.068084, 75.82888799999999, 109.424474, 105.48274700000002, 46.690159,
            103.44903400000001, 88.735963, 114.761665, 91.67781899999999, 82.56366399999999,
            98.50391099999999, 109.89408399999999, 66.47266499999999, 89.78307799999999};

    public GeneticProgramming(int populationSize, double crossoverProbability, double mutationProbability, int maxGeneration) {
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.maxGeneration = maxGeneration;
        initialPopulation();
    }

    private void initialPopulation() {
        boolean type = true; //текущий тип генерации: true - полная, false - растущая
        for (int i = 0; i < populationSize; i++) {
            int height = r.nextInt(minHeight, maxHeight); //максимальная глубина
            String[] being;
            if (type) {
                being = fullGeneration(height);
                type = false;
            } else {
                being = growingGeneration(height);
                type = true;
            }
            population.add(being);
        }
    }

    private String[] fullGeneration(int height) {
        int nodeNumber = (int) Math.pow(2, height) - 1; //количество узлов
        String[] result = new String[nodeNumber];
        for (int i = 0; i < (nodeNumber - 1) / 2; i++) {
            result[i] = functional[r.nextInt(functional.length)];
        }
        for (int i = (nodeNumber - 1) / 2; i < nodeNumber; i++) {
            result[i] = terminal[r.nextInt(terminal.length)];
        }
        return result;
    }

    private String[] growingGeneration(int height) {
        int nodeNumber = (int) Math.pow(2, height) - 1;//максимальное количество узлов
        String[] result = new String[nodeNumber];
        result[0] = functional[r.nextInt(functional.length)];//корень дерева обязательно функциональный символ
        int i = 1;
        while (i != 0) {
            if (result[i] == null) {
                //функциональный символ выбирается чаще, пока не достигнуты листья
                if (i < (nodeNumber - 1) / 2 && r.nextDouble(1) < 0.7) {
                    result[i] = functional[r.nextInt(functional.length)];
                    i = 2 * i + 1;
                } else {
                    result[i] = terminal[r.nextInt(terminal.length)];
                }
            } else {
                if ((i & 1) == 0) {
                    i = (i - 2) / 2;
                } else {
                    i++;
                }
            }
        }
        return result;
    }

    private List<String[]> reproduction() {
        List<String[]> parents = new ArrayList<>();
        while (parents.size() != populationSize) {
            int idx1 = r.nextInt(population.size());
            int idx2;
            do {
                idx2 = r.nextInt(population.size());
            } while (idx2 == idx1);
            if (fitnessFunction(population.get(idx1)) < fitnessFunction(population.get(idx2))) {
                parents.add(population.get(idx1));
            } else {
                parents.add(population.get(idx2));
            }
        }
        return parents;
    }

    private int height(int idx) {
        int h = 0;
        while (idx != 0) {
            h++;
            if ((idx & 1) == 1) {
                idx = (idx - 1) / 2;
            } else {
                idx = (idx - 2) / 2;
            }
        }
        return h;
    }

    private List<String[]> crossover(List<String[]> parents) {
        int iterations = parents.size() / 2;
        List<String[]> newPopulation = new ArrayList<>(parents);
        for (int i = 0; i < iterations; i++) {
            int idx = r.nextInt(parents.size());
            String[] parent1 = parents.get(idx);
            parents.remove(idx);
            idx = r.nextInt(parents.size());
            String[] parent2 = parents.get(idx);
            parents.remove(idx);
            if (r.nextDouble(1) < crossoverProbability) {
                int idx1;
                do {
                    idx1 = r.nextInt(parent1.length);
                } while (parent1[idx1] == null);
                int h1 = height(parent1, idx1);
                int h01 = height(idx1);
                int idx2;
                int h2;
                int h02;
                do {
                    do {
                        idx2 = r.nextInt(parent2.length);
                    } while (parent2[idx2] == null);
                    h2 = height(parent2, idx2);
                    h02 = height(idx2);
                } while (h01 + h2 > maxHeight || h02 + h1 > maxHeight ||
                        h01 + h2 < minHeight || h02 + h1 < minHeight);
                int size = (int) Math.pow(2, h01 + h2) - 1;
                String[] child1;
                if (size < parent1.length) {
                    child1 = new String[parent1.length];
                } else {
                    child1 = new String[size];
                }
                String[] child2;
                size = (int) Math.pow(2, h02 + h1) - 1;
                if (size < parent2.length) {
                    child2 = new String[parent2.length];
                } else {
                    child2 = new String[size];
                }
                for (int j = 0; j < child1.length && j < parent1.length; j++) {
                    child1[j] = parent1[j];
                }
                for (int j = 0; j < child2.length && j < parent2.length; j++) {
                    child2[j] = parent2[j];
                }
                child1 = copy(child1, parent2, idx1, idx2);
                child2 = copy(child2, parent1, idx2, idx1);
                newPopulation.add(child1);
                newPopulation.add(child2);
            }
        }
        return newPopulation;
    }

    private List<String[]> mutation(List<String[]> newPopulation) {
        List<String[]> finalPopulation = new ArrayList<>(newPopulation);
        for (String[] strings : finalPopulation) {
            if (r.nextDouble(1) < mutationProbability) {
                int idx;
                do {
                    idx = r.nextInt(strings.length);
                } while (strings[idx] == null);
                String symbol = strings[idx];
                if (isTerminal(symbol)) {
                    do {
                        strings[idx] = terminal[r.nextInt(terminal.length)];
                    } while (strings[idx].equals(symbol));
                } else {
                    do {
                        strings[idx] = functional[r.nextInt(functional.length)];
                    } while (strings[idx].equals(symbol));
                }
            }
        }
        return finalPopulation;
    }

    private String[] copy(String[] child, String[] parent, int idx1, int idx2) {
        String[] newChild = new String[child.length];
        System.arraycopy(child, 0, newChild, 0, child.length);
        int level = 1;
        while (idx1 < newChild.length) {
            for (int j = 0; j < level; j++) {
                if (idx2 < parent.length) {
                    newChild[idx1 + j] = parent[idx2 + j];
                } else {
                    newChild[idx1 + j] = null;
                }
            }
            idx1 = 2 * idx1 + 1;
            idx2 = 2 * idx2 + 1;
            level *= 2;
        }
        return newChild;
    }

    private int height(String[] being, int idx) {
        if (isTerminal(being[idx])) {
            return 1;
        } else {
            int h1 = height(being, 2 * idx + 1) + 1;
            int h2 = height(being, 2 * idx + 2) + 1;
            return Math.max(h1, h2);
        }
    }

    private boolean isTerminal(String node) {
        for (String s : terminal) {
            if (node.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private double toTerminal(String being, int num) {
        int i;
        for (i = 0; i < terminal.length; i++) {
            if (being.equals(terminal[i])) {
                break;
            }
        }
        if (i == terminal.length - 1) {
            return 2;
        } else {
            return variables[num][i];
        }
    }

    private double convert(String[] being, int idx, int num) {
        if (isTerminal(being[idx])) {
            return toTerminal(being[idx], num);
        } else {
            if (being[idx].equals("+")) {
                double res = convert(being, 2 * idx + 1, num);
                res += convert(being, 2 * idx + 2, num);
                return res;
            } else if (being[idx].equals("^")) {
                double op1 = convert(being, 2 * idx + 1, num);
                double op2 = convert(being, 2 * idx + 2, num);
                return Math.pow(op1, op2);
            }
        }
        return 0;
    }

    private String toFormula(String[] being, int idx, boolean flag) {
        if (isTerminal(being[idx])) {
            return being[idx];
        } else {
            StringBuilder str = new StringBuilder();
            if (being[idx].equals("+")) {
                if (flag)
                    str.append("(");
                str.append(toFormula(being, 2 * idx + 1, flag));
                str.append("+");
                str.append(toFormula(being, 2 * idx + 2, flag));
                if (flag)
                    str.append(")");
                return str.toString();
            } else if (being[idx].equals("^")) {
                str.append("(");
                str.append(toFormula(being, 2 * idx + 1, flag));
                str.append(")^");
                str.append(toFormula(being, 2 * idx + 2, true));
                return str.toString();
            }
        }
        return "";
    }

    private double fitnessFunction(String[] being) {
        double result = 0;
        for (int i = 0; i < values.length; i++) {
            double res;
            res = convert(being, 0, i);
            result += Math.abs((res - values[i]));
        }
        return result/values.length;
    }

    public void run() {
        population.sort(Comparator.comparingDouble(this::fitnessFunction));
        var min = population.get(0);
        //System.out.println("Начальное поколение, ошибка:" + fitnessFunction(min) + ",\nформула:" + toFormula(min, 0, false));
        //out(min);
        double minFunc = fitnessFunction(min);
        int minGen = 0;
        long begin = System.currentTimeMillis();
        for (int i = 1; i <= maxGeneration; i++) {
            List<String[]> parents = reproduction();
            List<String[]> newPopulation = crossover(parents);
            newPopulation = mutation(newPopulation);
            newPopulation.sort(Comparator.comparingDouble(this::fitnessFunction));
            while (newPopulation.size() != populationSize) {
                newPopulation.remove(newPopulation.size() - 1);
            }
            min = newPopulation.get(0);
            double func = fitnessFunction(min);
            System.out.println("Поколение:" + i + ", ошибка:" + func + ",\nформула:" + toFormula(min, 0, false));
            out(min);
            if(func < minFunc) {
                minFunc = func;
                minGen = i;
            }
            population = newPopulation;
        }
        System.out.println("Минимальная ошибка на " + minGen + " поколении, ошибка:" + minFunc + ", время работы:" + (System.currentTimeMillis() - begin) + "мс");
    }

    public static void main(String[] args) {
        GeneticProgramming gp = new GeneticProgramming(200, 0.9, 0.7, 100);
        gp.run();
    }

    private void out(String[] being) {
        int h = height(being, 0); //высота дерева
        String arrowSymbol = "|";
        int width = (int) Math.pow(2,h-1)*6;
        int idx = 0;
        for (int i = 0; i < h; i++) {
            StringBuilder arrows = new StringBuilder();
            int x = width / 2 - 3;
            int x1 = width / 4 - 1;
            for (int j = 0; j < (int) Math.pow(2, i); j++) {
                if(being[idx] != null) {
                    for (int k = 0; k < x1; k++) {
                        System.out.print(" ");
                        arrows.append(" ");
                    }
                    if(isTerminal(being[idx])) {
                        for (int k = x1; k < x; k++) {
                            System.out.print(" ");
                            arrows.append(" ");
                        }
                    } else {
                        arrows.append(arrowSymbol);
                        for (int k = x1; k < x; k++) {
                            System.out.print("_");
                            if (k != x - 1)
                                arrows.append(" ");
                        }
                    }
                    if(being[idx].length() == 1)
                        System.out.print("[ " + being[idx] + " ]");
                    else if(being[idx].length() == 2)
                        System.out.print("[" + being[idx] + " ]");
                    else System.out.print("[" + being[idx] + "]");
                    arrows.append("     ");
                    if(isTerminal(being[idx])) {
                        for (int k = x1; k < x; k++) {
                            System.out.print(" ");
                            arrows.append(" ");
                        }
                    } else {
                        for (int k = x1; k < x; k++) {
                            System.out.print("_");
                            if(k != x - 1)
                                arrows.append(" ");
                        }
                        arrows.append(arrowSymbol);
                    }
                    for (int k = 0; k < x1 + 1; k++) {
                        System.out.print(" ");
                        arrows.append(" ");
                    }
                } else {
                    for(int k = 0; k < 2*x + 5 + 1; k++) {
                        System.out.print(" ");
                        arrows.append(" ");
                    }
                }
                idx++;
            }
            System.out.println();
            System.out.println(arrows);
            width /= 2;
        }
    }
}
