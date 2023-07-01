package lab1;

public class BeingEvaluation {
    static public double lowBorder;
    static public double highBorder;

    static public int comparisonFunction(String x, String y) {
        if(fitnessFunctionValueOfBeing(x) > fitnessFunctionValueOfBeing(y)) {
            return -1;
        } else if(fitnessFunctionValueOfBeing(x) < fitnessFunctionValueOfBeing(y)) {
            return 1;
        }else {
            return 0;
        }
    }
    static public double fitnessFunctionValueOfBeing(String being) {
        return fitnessFunction(convertBinaryToDouble(being));
    }

    static public double fitnessFunction(double x) {
        return (1.85 - x) * Math.cos(3.5 * x - 0.5);
    }

    static public double convertBinaryToDouble(String chromosome) {
        int resultNumber = 0;
        int chromosomeLength = chromosome.length();
        for (int i = 0; i < chromosomeLength; i++) {
            resultNumber *= 2;
            if (chromosome.charAt(i) == '1') {
                resultNumber += 1;
            }
        }
        return lowBorder + resultNumber*(highBorder-lowBorder) / (Math.pow(2.,chromosomeLength)-1);
    }
}
