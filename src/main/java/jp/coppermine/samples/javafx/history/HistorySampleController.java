package jp.coppermine.samples.javafx.history;

import static javafx.scene.input.KeyCode.ENTER;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import jp.coppermine.poortoys.history.FileHistory;
import jp.coppermine.poortoys.history.History;
import jp.coppermine.poortoys.javafx.history.FileHistoryOperation;
import jp.coppermine.poortoys.javafx.history.HistoryView;

public class HistorySampleController implements Initializable, FileHistoryOperation {

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
                textField.clear();
            }
        }
    }
    
    private History history = new FileHistory(getPath());
    
    @Override
    public History getHistory() {
        return history;
    }

    private HistoryView historyView = new HistoryView();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeHistory();
        loadKeywords();
        historyView.attach(textField, () -> getKeywords());
    }

}
