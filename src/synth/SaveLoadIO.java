package synth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class SaveLoadIO {
    public static void save(Path file, String value) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write(value);
            writer.newLine();
        }
    }

    public static String load(Path file) throws Exception {
        String value = "1";
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line = reader.readLine();
            value = line.trim();
        }
        return value;
    }
}
