package synth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;

import static synth.Config.*;

public class Main extends Application {

    private static FluidSynthService synth;

    @Override
    public void start(Stage stage) {

        FXMLLoader loader;
        Scene scene;

        try {
            if (FLUIDSYNTH_TURN_RIGHT_90) {
                loader = new FXMLLoader(getClass().getResource("layout.fxml"));
                Parent root = loader.load();
                Pane wrapper = new Pane();
                wrapper.getChildren().add(root);
                root.setRotate(90);
                root.setLayoutX(80);
                root.setLayoutY(-80);
                scene = new Scene(wrapper, WINDOW_HEIGHT, WINDOW_WIDTH);
            } else {
                loader = new FXMLLoader(getClass().getResource("layout.fxml"));
                scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            }

            MainController controller = loader.getController();

            stage.setTitle(APP_TITLE);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint(""); // скрыть ESC-подсказку
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);

            stage.setOnCloseRequest(e -> {
                try {
                    controller.shutdown();
                    controller.savePresetId();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            stage.show();
        } catch (Exception e) {
            showError("Application Error", "Unexpected error occurred", e);
        }

    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            showError("Application Error", "Unexpected error occurred", e);
        }
    }

    private static void showError(String header, String message, Throwable ex) {
        ex.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        // полный текст исключения
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expandableContent = new GridPane();
        expandableContent.setMaxWidth(Double.MAX_VALUE);
        expandableContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expandableContent);
        alert.getDialogPane().setExpanded(true); // сразу раскрыть

        alert.showAndWait();
    }
}