package jp.coppermine.samples.javafx.history;

import static java.util.stream.Collectors.toList;
import static javafx.scene.input.KeyCode.ENTER;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import jp.coppermine.tools.history.Command;
import jp.coppermine.tools.history.FileHistory;
import jp.coppermine.tools.history.History;

public class HistorySampleController implements Initializable {

    @FXML
    private TextField textField;
    
    /**
     * Handles to action of key press.
     * 
     * @param event action event when key pressed.
     */
    @FXML
    public void onKeyEnterAction(KeyEvent event) {
        if (event.getCode() == ENTER) {
            if (!textField.getText().isEmpty()) {
                updateKeywords(textField.getText());
            }
        }
    }

    private final Path dir = Paths.get(System.getProperty("user.home"), ".javafx-history-sample");
    private final Path path = dir.resolve("history.txt");

    private HistoryView historyView = new HistoryView();
    
    private History history = new FileHistory(path);
    
    /**
     * Initializes keywords history.
     */
    private void loadKeywords() {
        Platform.runLater(() -> {
            history.load();
            history.shrink(LocalDateTime.now().minusDays(30));
            history.save();
        });
    }
    
    /**
     * Updates keywords history.
     */
    private void updateKeywords(String keyword) {
        Platform.runLater(() -> {
            history.append(Command.of(keyword));
            history.save();
        });
    }
    
    /**
     * Obtains keywords.
     * 
     * @return keywords, never null 
     */
    private List<String> getKeywords() {
        if (Files.notExists(path)) {
            history.clear();
        }
        
        return history.list().stream().map(e -> e.getCommand()).collect(toList());
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                if (Files.notExists(dir)) {
                    Files.createDirectories(dir);
                }
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        
        loadKeywords();
        historyView.attach(textField, () -> getKeywords());
    }

}
