package synth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class SnapshotIO {
    public static void save(Path file, ListItem[] states) throws Exception {
        if (states.length != 16) {
            throw new IllegalArgumentException("Массив должен содержать ровно 16 элементов");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            for (int i = 0; i < 16; i++) {
                ListItem itm = states[i];
                String line = String.format("%d,%d,%d,%d",
                        itm.getFont(),
                        itm.getChannel(),
                        itm.getBank(),
                        itm.getProgram());
                writer.write(line);
                writer.newLine();
            }
        }
    }


    public static ListItem[] load(Path file) throws Exception {
        ListItem[] states = new ListItem[16];

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                int channel = Integer.parseInt(parts[1]);
                ListItem itm = new ListItem(channel, String.format("%s", channel));
                itm.setFont(Integer.parseInt(parts[0]));
                itm.setChannel(Integer.parseInt(parts[1]));
                itm.setBank(Integer.parseInt(parts[2]));
                itm.setProgram(Integer.parseInt(parts[3]));
                states[channel] = itm;
            }
        }
        for (int i = 0; i < 16; i++) {
            if (states[i] == null) {
                states[i] = new ListItem(i, String.format("%d", i)); // дефолтное значение
            }
        }
        return states;
    }
}
