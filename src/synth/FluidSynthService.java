package synth;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

import static synth.Config.*;

public class FluidSynthService {

    private Process process;
    private BufferedWriter writer;
    private BufferedReader reader;

    private final StringBuilder outputBuffer = new StringBuilder();

    public FluidSynthService() throws IOException, InterruptedException {
        startProcess();
    }

    private void startProcess() throws IOException, InterruptedException {

        File sfDir = new File(SF2_DIRECTORY);
        if (!sfDir.exists() || !sfDir.isDirectory()) {
            throw new RuntimeException("soundfonts directory '" + SF2_DIRECTORY + "' not found");
        }

        // -a alsa -m alsa_seq -o midi.autoconnect=1 -- Linux
        // -a coreaudio -m coremidi -o midi.autoconnect=1 -- Mac

        List<String> command = new ArrayList<>();
        command.add(FLUIDSYNTH_PATH);
        command.add("-a");
        command.add(FLUIDSYNTH_AUDIO_DRIVER);
        command.add("-m");
        command.add(FLUIDSYNTH_MIDI_DRIVER);
        if (FLUIDSYNTH_AUTO_CONNECT_MIDI_DEVICES) {
            command.add("-o");
            command.add("midi.autoconnect=1");
        }
        command.add("-o");
        command.add("midi.portname=FluidSynth_Main");

        File[] files = sfDir.listFiles((d, name) -> name.endsWith(".sf2"));

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (File f : files) {
                command.add(f.getAbsolutePath());
            }
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        process = pb.start();

        writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream()));

        reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));


        // даём движку время запуститься
        Thread.sleep(SYNTH_START_TIMEOUT_MS);

        // запускаем поток для чтения вывода
        startReaderThread();
    }

    public void sendCommand(String cmd) throws IOException {
        writer.write(cmd + "\n");
        writer.flush();
    }

    public void selectProgram(int channel, int soundFontId, int bankId, int programId) throws IOException {
        sendCommand("select " + channel + " " + soundFontId + " " + bankId + " " + programId);
    }

    // завершение
    public void close() throws IOException {
        sendCommand("quit");
        writer.close();
        process.destroy();
    }

    public void playNote(int channel, int key, int velocity, int durationMs) throws IOException, InterruptedException {
        // включаем ноту
        sendCommand("noteon " + channel + " " + key + " " + velocity);

        // ждём нужную длительность
        Thread.sleep(durationMs);

        // выключаем ноту
        sendCommand("noteoff " + channel + " " + key);
    }

    private void startReaderThread() {
        new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FluidSynth] " + line);
                    synchronized (outputBuffer) {
                        outputBuffer.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "FluidSynth-Reader").start();
    }

    private synchronized String sendCommandWithOutput(String cmd) throws IOException {
        synchronized (outputBuffer) {
            outputBuffer.setLength(0); // очищаем старое
        }

        writer.write(cmd + "\n");
        writer.flush();

        try {
            Thread.sleep(SYNTH_OUTPUT_TIMEOUT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        synchronized (outputBuffer) {
            return outputBuffer.toString();
        }
    }

    /**
     * Получаем список SF2 из синта
     */
    public ObservableList<ListItem> getSoundFonts() throws IOException {
        String output = sendCommandWithOutput("fonts");

        ObservableList<ListItem> list = FXCollections.observableArrayList();

        for (String line : output.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // ищем строки, которые оканчиваются на .sf2
            if (line.endsWith(".sf2")) {
                String[] parts = line.split("\\s+");
                int id = Integer.parseInt(parts[0]);
                String path = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

                // оставляем только имя файла без расширения
                String fileName = new File(path).getName();
                if (fileName.endsWith(".sf2")) {
                    fileName = fileName.substring(0, fileName.length() - 4);
                }
                ListItem itm = new ListItem(id, fileName);
                itm.setFont(id);
                list.add(itm);
            }
        }

        Collections.reverse(list);
        return list;
    }

    public ObservableList<ListItem> getInstruments(int id) throws IOException {
        String output = sendCommandWithOutput("inst " + id);

        ObservableList<ListItem> list = FXCollections.observableArrayList();

        int idx = 1;
        for (String line : output.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("> inst")) continue;
            String[] parts = line.split("\\s+");

            String bankAndProgram = parts[0];
            String[] bankAndProgramArr = bankAndProgram.split("-");
            String bank = bankAndProgramArr[0];
            String program = bankAndProgramArr[1];

            String name = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            String title = bankAndProgram + " " + name;

            ListItem itm = new ListItem(idx, title);
            itm.setBankProgram(bank, program);
            itm.setFont(id);
            list.add(itm);

            idx++;
        }

        return list;
    }
}