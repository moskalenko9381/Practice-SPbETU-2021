package spbetu.prim.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import spbetu.prim.view.GraphView;
import spbetu.prim.view.WeightWindow;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    private GraphView view;
    private ActionType actionType;
    private WeightWindow weightWindow;
    private Text weightText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view = new GraphView();
        actionType = ActionType.ADD_NODE;
        weightWindow = null;
        weightText = null;
    }

    public void anchorPaneClicked(MouseEvent mouseEvent) {
        log.info("anchorPane was clicked");
        if (actionType == ActionType.ADD_NODE) {
            log.info("Adding a node");
            StackPane stackPane = view.addNode(mouseEvent);
            anchorPane.getChildren().add(stackPane);
            stackPane.setOnMouseClicked(this::stackPaneClicked);
        }
    }

    public void stackPaneClicked(MouseEvent mouseEvent) {
        if (actionType == ActionType.DELETE) {
            log.info("Removing the stackPane");
            view.removeNode((StackPane) mouseEvent.getSource());
            return;
        } else if (actionType != ActionType.CONNECT_NODES) {
            log.info("StackPane clicked. The node was chosen");
            actionType = ActionType.CONNECT_NODES;
            view.chooseNode(mouseEvent);
            return;
        }

        log.info("The second node was chosen");
        Pane pane = view.addEdge(mouseEvent);

        if (pane == null)
            return;

        pane.getChildren().get(2).setOnMouseClicked(this::weightClicked);
        pane.getChildren().get(0).setOnMouseClicked(this::lineClicked);
        pane.setPickOnBounds(false);
        anchorPane.getChildren().add(pane);
        weightText = (Text) pane.getChildren().get(2);
        askWeight();
    }

    public void lineClicked(MouseEvent mouseEvent) {
        if (actionType == ActionType.DELETE) {
            log.info("Removing the line");
            view.removeNode((Line) mouseEvent.getSource());
        }
    }

    public void weightClicked(MouseEvent mouseEvent) {
        if (actionType == ActionType.DELETE) {
            log.info("Removing the weight");
            view.removeNode((Text) mouseEvent.getSource());
            return;
        }

        log.info("Weight was clicked");
        weightText = (Text) mouseEvent.getSource();
        askWeight();
    }

    public void askWeight() {
        log.info("Showing the weight window");
        actionType = ActionType.CHANGE_WEIGHT;
        Stage stage = new Stage();
        stage.setOnHiding(this::weightWindowClosed);
        weightWindow = new WeightWindow();
        weightWindow.start(stage);
    }

    private void weightWindowClosed(WindowEvent windowEvent) {
        weightText.setText(weightWindow.getText());
        log.info("The weight is " + weightText.getText());
        actionType = ActionType.ADD_NODE;
    }

    public void clearClicked() {
        log.info("Clearing the scene");
        anchorPane.getChildren().clear();
        view.clear();
    }

    public void cancelSelection() {
        log.info("Escape button was pressed, clearing the choose of the node");
        actionType = ActionType.ADD_NODE;
    }

    public void deleteClicked() {
        log.info("Delete an item was clicked");
        if (actionType == ActionType.DELETE) {
            actionType = ActionType.ADD_NODE;
            return;
        }

        actionType = ActionType.DELETE;
    }
}
