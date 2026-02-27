package synth;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.file.Path;

import static synth.Config.SYNTH_START_TIMEOUT_MS;

public class MainController {

    @FXML
    public TextField cmd;

    private FluidSynthService synth;

    @FXML
    private ListView<ListItem> soundFontList;

    @FXML
    private ListView<ListItem> instrumentsList;

    @FXML
    private Spinner<Integer> channelSpinner;

    @FXML
    private Spinner<Integer> presetsSpinner;

    ListItem[] channels = new ListItem[16];

    @FXML
    public void initialize() throws Exception {

        channelSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 1)
        );

        presetsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1)
        );

        this.channels = SnapshotIO.load(Path.of("saves/" + this.presetsSpinner.getValue() + ".save"));

        this.cmd.setText("noteon 0 56 120");

        this.synth = new FluidSynthService();
        new Thread(() -> {
            try {
                Thread.sleep(SYNTH_START_TIMEOUT_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                try {
                    feelFontsList();
                    this.presetsSpinner.getValueFactory().setValue(Integer.parseInt(SaveLoadIO.load(Path.of("preset.save"))));
                    loadPreset();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }).start();
    }

    private void feelFontsList() throws IOException {
        ObservableList<ListItem> items = synth.getSoundFonts();
        soundFontList.setItems(items);
    }

    private void feelInstrumentsList(int sfId) throws IOException {
        ObservableList<ListItem> items = synth.getInstruments(sfId);
        instrumentsList.setItems(items);
    }

    @FXML
    private void loadSoundFont() throws Exception {
        int sfId = soundFontList.getSelectionModel().getSelectedItem().getId();
        feelInstrumentsList(sfId);
    }

    @FXML
    private void loadProgram() throws Exception {
        int ch = channelSpinner.getValue() - 1;
        int sf = soundFontList.getSelectionModel().getSelectedItem().getId();
        int bank = instrumentsList.getSelectionModel().getSelectedItem().getBank();
        int program = instrumentsList.getSelectionModel().getSelectedItem().getProgram();
        synth.selectProgram(ch, sf, bank, program);

        ListItem itm = new ListItem(ch, String.format("%d", ch));
        itm.setFont(sf);
        itm.setChannel(ch);
        itm.setBank(bank);
        itm.setProgram(program);
        this.channels[ch] = itm;
    }

    public void shutdown() throws Exception {
        synth.close();
    }

    public void runCmd() throws Exception {
        synth.sendCommand(cmd.getText());
    }

    public void reset() throws Exception {
        for (int i = 0; i <= 15; i++) {
            synth.sendCommand("cc " + i + " 120 0");
            synth.sendCommand("cc " + i + " 123 0");
            ListItem itm = new ListItem(i, String.format("%s", i));
            this.channels[i] = itm;
            synth.selectProgram(itm.getChannel(), itm.getFont(), itm.getBank(), itm.getProgram());
        }
    }

    public void savePreset() throws Exception {
        SnapshotIO.save(Path.of("saves/" + this.presetsSpinner.getValue() + ".save"), this.channels);
    }

    public void loadPreset() throws Exception {
        this.channels = SnapshotIO.load(Path.of("saves/" + this.presetsSpinner.getValue() + ".save"));
        for (int i = 0; i <= 15; i++) {
            ListItem itm = this.channels[i];
            synth.selectProgram(itm.getChannel(), itm.getFont(), itm.getBank(), itm.getProgram());
        }
        updateSpinnerValues();
    }

    public void updateSpinnerValues() throws Exception {
        ListItem sf = this.channels[this.channelSpinner.getValue() - 1];
        int idxSf = 0;

        for (int i = 0; i < soundFontList.getItems().size(); i++) {
            ListItem item = soundFontList.getItems().get(i);
            if (sf.getFont() == item.getFont()) {
                idxSf = i;
                break;
            }
        }

        this.soundFontList.getSelectionModel().select(idxSf);
        this.soundFontList.scrollTo(idxSf);
        loadSoundFont();

        ListItem pr = this.channels[this.channelSpinner.getValue() - 1];
        int idxPr = 0;

        for (int i = 0; i < instrumentsList.getItems().size(); i++) {
            ListItem item = instrumentsList.getItems().get(i);
            if (pr.getFont() == item.getFont() && pr.getProgram() == item.getProgram()) {
                idxPr = i;
                break;
            }
        }

        this.instrumentsList.getSelectionModel().select(idxPr);
        this.instrumentsList.scrollTo(idxPr);

    }

    public void savePresetId() throws Exception {
        SaveLoadIO.save(Path.of("preset.save"), this.presetsSpinner.getValue().toString());
    }
}
