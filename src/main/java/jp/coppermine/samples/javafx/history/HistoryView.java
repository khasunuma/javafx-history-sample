package jp.coppermine.samples.javafx.history;

import static java.util.stream.Collectors.toList;
import static javafx.geometry.Orientation.VERTICAL;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class HistoryView {
    
    private final ListView<String> history;
    
    private final long count;
    
    private TextField textField;
    
    private Supplier<List<String>> keywordSupplier;
    
    public HistoryView() {
        history = new ListView<>();
        history.setOpacity(1.0);
        history.setOrientation(VERTICAL);
        history.setOnMousePressed(this::onMousePressedAction);
        history.setItems(FXCollections.observableArrayList());
        history.setVisible(false);
        count = Long.getLong("ifocatcher.ui.history.count", 10L);
    }
    
    public Pane getRoot(Node node) {
        while (node.getParent() instanceof Pane) {
            return (Pane) node.getParent();
        }
        return getRoot(node);
    }
    
    public HistoryView attach(TextField textField, Supplier<List<String>> keywordSupplier) {
        return attach(getRoot(textField), textField, keywordSupplier);
    }
    
    public HistoryView attach(Pane parent, TextField textField, Supplier<List<String>> keywordSupplier) {
        history.setLayoutX(textField.getLayoutX());
        history.setLayoutY(textField.getLayoutY() + textField.getPrefHeight());
        history.setPrefWidth(textField.getPrefWidth());
        parent.getChildren().add(history);
        
        this.textField = textField;
        textField.setOnKeyReleased(this::onTextFieldKeyReleasedAction);
        textField.setOnMouseClicked(this::onTextFieldMouseClickedAction);
        textField.textProperty().addListener(
                (observable, oldValue, newValue) -> history.setItems(
                        FXCollections.observableList(getKeywords().stream()
                                .filter(s -> s.startsWith(newValue))
                                .limit(count).collect(Collectors.toList()))));
        
        this.keywordSupplier = keywordSupplier;
        
        return this;
    }
    
    /**
     * Obtains history of keywords.
     */
    private List<String> getKeywords() {
        return keywordSupplier.get().stream().distinct().collect(toList());
    }

    private void onMousePressedAction(MouseEvent event) {
        Optional.ofNullable(history.getSelectionModel().getSelectedItem()).ifPresent(textField::setText);
        history.setVisible(false);
    } 
    
    private void onTextFieldMouseClickedAction(MouseEvent event) {
        IntStream.of(event.getClickCount()).limit(1L).filter(i -> i >= 2).forEach(i -> showItems(!history.isVisible()));
    }
    
    private void onTextFieldKeyReleasedAction(KeyEvent event) {
        int items = history.getItems().size();
        int index = history.getSelectionModel().getSelectedIndex();
        switch (event.getCode()) {
        case DOWN:
            showItems(true);
            Optional.of(items).filter(c -> c > 0).map(c -> (index + 1) % c).ifPresent(
                    history.getSelectionModel()::select);
            break;
        case UP:
            showItems(true);
            Optional.of(items).filter(c -> c > 0).map(c -> (c + index - 1) % c).ifPresent(
                    history.getSelectionModel()::select);
            break;
        case ENTER:
            Optional.of(items).filter(c -> c > 0).ifPresent(
                    c -> Optional.ofNullable(history.getSelectionModel().getSelectedItem()).ifPresent(
                            textField::setText));
            // fall through
        case ESCAPE:
            history.setVisible(false);
            break;
        default:
            showItems(!textField.getText().isEmpty());
            break;
        }
    }
    
    private void showItems(boolean visible) {
        if (visible) {
            history.setItems(
                    FXCollections.observableList(getKeywords().stream()
                            .filter(s -> !s.trim().isEmpty())
                            .filter(s -> s.startsWith(textField.getText()))
                            .limit(count).collect(Collectors.toList())));
            history.setPrefHeight((history.getItems().size() * 20.0 + 2.0));
            history.setVisible(!history.getItems().isEmpty());
        } else {
            history.setVisible(false);
        }
    }
}
