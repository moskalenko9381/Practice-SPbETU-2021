package spbetu.prim.controller;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;
import lombok.extern.slf4j.Slf4j;
import spbetu.prim.loggers.GraphLogger;
import spbetu.prim.model.*;
import spbetu.prim.view.*;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public ScrollPane logTextArea;

    @FXML
    public AnchorPane secondAnchorPane;

    private GraphView view;
    private Graph graph;
    private ActionType actionType;
    private boolean scrollPaneClickedFlag;
    private ScrollPaneLog scrollPaneLog;
    private GraphLogger graphLogger;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view = new GraphView();
        actionType = ActionType.ADD_NODE;

        scrollPaneLog = new ScrollPaneLog(logTextArea); //ScrollPane - Вывод информации на окно с логами
        graphLogger = new GraphLogger(scrollPaneLog);   // Добавление через ScrollPane информации о графе

        graph = new Graph(graphLogger);
    }

    public void anchorPaneClicked(MouseEvent mouseEvent) {
        log.info("anchorPane was clicked");
        if (scrollPaneClickedFlag) {
            scrollPaneClickedFlag = false;
        } else if (actionType == ActionType.ADD_NODE) {
            log.info("Adding a node");
            StackPane stackPane = view.addNode(mouseEvent);
            anchorPane.getChildren().add(stackPane);
            stackPane.setOnMouseClicked(this::stackPaneClicked);
        } else if (actionType == ActionType.CHANGE_WEIGHT) {
            actionType = ActionType.ADD_NODE;
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
        Pane pane = view.addEdge(mouseEvent, askWeight());

        if (pane == null)
            return;

        pane.getChildren().get(2).setOnMouseClicked(this::weightClicked);
        pane.getChildren().get(0).setOnMouseClicked(this::lineClicked);
        pane.setPickOnBounds(false);
        anchorPane.getChildren().add(pane);
        addEdgeToGraph(pane);
    }

    public void addEdgeToGraph(Pane pane) {
        Text text = (Text) pane.getChildren().get(2);
        StackPane firstNode = (StackPane) pane.getChildren().get(1);
        StackPane secondNode = (StackPane) pane.getChildren().get(3);
        graph.addNewEdge(view.getNodeId(firstNode), view.getNodeId(secondNode), Integer.parseInt(text.getText()));
    }

    public void lineClicked(MouseEvent mouseEvent) {
        log.info("Line was clicked");
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
        Text weightText = (Text) mouseEvent.getSource();
        String newWeight = askWeight();
        if (view.checkWeight(newWeight))
            weightText.setText(askWeight());
        else
            log.warn("Wrong weight");
    }

    public String askWeight() {
        log.info("Showing the weight window");
        actionType = ActionType.CHANGE_WEIGHT;
        return new WeightWindow().getWeight();
    }

    public void clearClicked() {
        log.info("Clearing the scene");
        anchorPane.getChildren().remove(2, anchorPane.getChildren().size());
        graph.graphStartAgain();
        view.clear();
    }

    public void deleteClicked() {
        log.info("Delete an item was clicked");
        if (actionType == ActionType.DELETE) {
            actionType = ActionType.ADD_NODE;
            return;
        }

        actionType = ActionType.DELETE;
    }

    public void scrollPaneClicked() {
        log.info("Scroll pane was clicked");
        scrollPaneClickedFlag = true;
    }

    public void clearLoggingArea() {
        scrollPaneLog.clear();      //Очистка окна с логами
    }

    public void cancelSelection() {
        if (actionType != ActionType.CONNECT_NODES)
            return;

        log.info("Escape button was pressed, clearing the choose of the node");
        actionType = ActionType.ADD_NODE;
    }

    public void aboutClicked() {
        log.info("About clicked");
        new AboutWindow().show();
    }

    public void faqClicked() {
        log.info("FAQ clicked");
        new FAQWindow().show();
    }

    public void nextStepClicked() {
        log.info("Next step in algorithm");
        Edge edge = graph.runAlgorithmByStep();

        if (edge == null || edge.getVertexTo() == null || edge.getVertexFrom() == null) {
            new InfoWindow().show("The minimum spanning tree was found");
            return;
        }

        view.addEdgeToTree(edge.getVertexFrom().getNumber(), edge.getVertexTo().getNumber(), edge.getWeight());
    }

    public void runClicked() {
        log.info("Run algorithm");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Edge edge;
                while ((edge = graph.runAlgorithmByStep()) != null
                        && edge.getVertexFrom() != null && edge.getVertexTo() != null) {
                    view.addEdgeToTree(edge.getVertexFrom().getNumber(), edge.getVertexTo().getNumber(), edge.getWeight());

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        log.info("Couldn't stop the thread: " + e.getMessage());
                    }

                }

                return null;
            }
        };

        new Thread(task).start();
        task.setOnSucceeded(this::doneRun);
    }

    private void doneRun(WorkerStateEvent workerStateEvent) {
        new InfoWindow().show("The minimum spinning tree was found");
    }

    public void resetClicked() {
        log.info("Reset the algorithm");
        view.resetGraph();
        graph.graphStartAgain();
    }

    public void prevStepClicked() {
        log.info("Prev step clicked");
    }
}