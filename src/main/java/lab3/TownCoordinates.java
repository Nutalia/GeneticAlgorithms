package lab3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TownCoordinates {
    static List<Double> x = new ArrayList<>();
    static List<Double> y = new ArrayList<>();
    static int number;

    static void getData() {
        if (Files.exists(Paths.get("src/main/resources/data.txt"))) {
            try {
                var lines = Files.lines(Paths.get("src/main/resources/data.txt")).toList();
                for (String line : lines) {
                    var characters = line.split("\s");
                    x.add(Double.parseDouble(characters[1]));
                    y.add(Double.parseDouble(characters[2]));
                }
                number = x.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
