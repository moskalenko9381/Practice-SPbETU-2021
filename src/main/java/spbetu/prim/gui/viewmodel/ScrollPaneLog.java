package spbetu.prim.gui.viewmodel;

import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ScrollPaneLog {

    private final Text text;
    private final ScrollPane scrollPaneLog;

    public ScrollPaneLog(ScrollPane scrollPane) {
        text = new Text();
        text.setFont(Font.font("Times new roman", FontWeight.NORMAL, 16));
        scrollPaneLog = scrollPane;
        scrollPaneLog.setContent(text);
    }

    public void addNewMessage(String message) {
        text.setText(text.getText() + message + '\n');
    }

    public void clear() {
        text.setText("");
    }
}