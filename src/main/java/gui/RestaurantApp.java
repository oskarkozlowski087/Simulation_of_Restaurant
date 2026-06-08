package gui;

import agents.Cook;
import agents.MovingAgent;
import agents.Waiter;
import core.Board;
import core.Simulation;
import environment.Buffer;
import environment.Cell;
import environment.Stove;
import environment.Table;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class RestaurantApp extends Application {

    private static final int CELL_SIZE = 40;
    private Simulation simulation;
    private Pane boardPane;
    private Label tickLabel;
    private Label bufferLabel;
    private VBox logBox;
    private ScrollPane logScroll;
    private Button startButton;
    private AnimationTimer timer;
    private volatile boolean running = false;
    private double speedMultiplier = 1.0;

    @Override
    public void start(Stage primaryStage) {
        simulation = new Simulation();
        simulation.init();

        BorderPane root = new BorderPane();

        VBox topBar = createTopBar();
        root.setTop(topBar);

        boardPane = new Pane();
        root.setCenter(boardPane);

        VBox rightPanel = createRightPanel();
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 960, 720);
        primaryStage.setTitle("Symulacja Restauracji");
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshUI();

        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (!running) return;
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                long delay = (long) (100_000_000 / speedMultiplier);
                if (now - lastUpdate >= delay) {
                    lastUpdate = now;
                    simulation.tick();
                    refreshUI();
                    if (!simulation.isRunning()) {
                        running = false;
                        startButton.setText("Start");
                    }
                }
            }
        };
        timer.start();
    }

    private VBox createTopBar() {
        VBox bar = new VBox(5);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #2c3e50;");

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);

        startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 20;");
        startButton.setOnAction(e -> toggleSimulation());

        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 20;");
        resetButton.setOnAction(e -> resetSimulation());

        tickLabel = new Label("Tick: 0");
        tickLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label speedLabel = new Label("Prędkość:");
        speedLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        Slider speedSlider = new Slider(0.25, 4.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setMinorTickCount(3);
        speedSlider.setSnapToTicks(false);
        speedSlider.setPrefWidth(150);
        speedSlider.valueProperty().addListener((obs, old, val) -> {
            speedMultiplier = val.doubleValue();
        });

        controls.getChildren().addAll(startButton, resetButton, tickLabel, speedLabel, speedSlider);
        bar.getChildren().add(controls);
        return bar;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(280);
        panel.setStyle("-fx-background-color: #ecf0f1;");

        bufferLabel = new Label("Buffer: 0 zamówień, 0 gotowych");
        bufferLabel.setStyle("-fx-font-size: 13px;");

        Label logLabel = new Label("Log zdarzeń:");
        logLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        logBox = new VBox(2);
        logScroll = new ScrollPane(logBox);
        logScroll.setFitToWidth(true);
        logScroll.setPrefHeight(400);

        panel.getChildren().addAll(bufferLabel, logLabel, logScroll);
        VBox.setVgrow(logScroll, Priority.ALWAYS);
        return panel;
    }

    private void toggleSimulation() {
        if (running) {
            running = false;
            startButton.setText("Start");
        } else {
            simulation.startSimulation();
            running = true;
            startButton.setText("Stop");
            platformRefreshUI();
        }
    }

    private void resetSimulation() {
        running = false;
        startButton.setText("Start");
        simulation = new Simulation();
        simulation.init();
        refreshUI();
    }

    private void platformRefreshUI() {
        Platform.runLater(this::refreshUI);
    }

    private void refreshUI() {
        tickLabel.setText("Tick: " + simulation.getTick());
        updateBoardPane();
        updateBufferLabel();
        updateLog();
    }

    private void updateBoardPane() {
        boardPane.getChildren().clear();
        Board board = simulation.getBoard();
        int width = board.getWidth();
        int height = board.getHeight();
        boardPane.setPrefSize(width * CELL_SIZE + 2, height * CELL_SIZE + 2);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = board.getCell(x, y);
                double px = x * CELL_SIZE;
                double py = (height - 1 - y) * CELL_SIZE;
                Rectangle rect = new Rectangle(px, py, CELL_SIZE, CELL_SIZE);

                switch (cell.getType()) {
                    case KITCHEN -> rect.setFill(Color.web("#4a4a4a"));
                    case HALL -> rect.setFill(Color.web("#f5f0e1"));
                    case WALL -> rect.setFill(Color.web("#8B4513"));
                    case BUFFER -> rect.setFill(Color.web("#FFD700"));
                    default -> rect.setFill(Color.web("#e0e0e0"));
                }

                rect.setStroke(Color.web("#333333"));
                rect.setStrokeWidth(1);
                boardPane.getChildren().add(rect);

                if (cell.isStove()) {
                    Stove stove = null;
                    for (Stove s : simulation.getStoves()) {
                        if (s.getX() == x && s.getY() == y) {
                            stove = s;
                            break;
                        }
                    }
                    String label = "S";
                    if (stove != null && stove.isOccupied()) {
                        label = "S🔥";
                    }
                    Text t = new Text(px + 4, py + 26, label);
                    t.setFill(Color.WHITE);
                    t.setFont(Font.font(16));
                    boardPane.getChildren().add(t);
                }

                if (cell.getTable() != null) {
                    boolean occupied = cell.getTable().getIsOccupied();
                    String label = occupied ? "T👤" : "T";
                    Text t = new Text(px + 4, py + 26, label);
                    t.setFill(Color.web("#5c2e00"));
                    t.setFont(Font.font(16));
                    boardPane.getChildren().add(t);
                }

                if (cell.getBuffer() != null) {
                    int pending = simulation.getBuffer().getPendingCount();
                    int ready = simulation.getBuffer().getReadyCount();
                    String label = "B(" + pending + "/" + ready + ")";
                    Text t = new Text(px + 2, py + 26, label);
                    t.setFill(Color.web("#5c4a00"));
                    t.setFont(Font.font(14));
                    boardPane.getChildren().add(t);
                }

                MovingAgent occupant = cell.getOccupant();
                if (occupant != null) {
                    Circle circle;
                    String letter;
                    if (occupant instanceof Cook) {
                        circle = new Circle(px + CELL_SIZE / 2, py + CELL_SIZE / 2, 12, Color.RED);
                        letter = "K";
                    } else if (occupant instanceof Waiter) {
                        circle = new Circle(px + CELL_SIZE / 2, py + CELL_SIZE / 2, 12, Color.BLUE);
                        letter = "W";
                    } else {
                        circle = new Circle(px + CELL_SIZE / 2, py + CELL_SIZE / 2, 12, Color.GREEN);
                        letter = "C";
                    }
                    boardPane.getChildren().add(circle);
                    Text agentText = new Text(px + CELL_SIZE / 2 - 6, py + CELL_SIZE / 2 + 5, letter);
                    agentText.setFill(Color.WHITE);
                    agentText.setFont(Font.font(12));
                    boardPane.getChildren().add(agentText);
                }
            }
        }
    }

    private void updateBufferLabel() {
        Buffer buffer = simulation.getBuffer();
        int pending = buffer.getPendingCount();
        int ready = buffer.getReadyCount();
        bufferLabel.setText("Buffer: " + pending + " zamówień, " + ready + " gotowych");
    }

    private void updateLog() {
        List<String> msgs = simulation.getLogMessages();
        logBox.getChildren().clear();
        int start = Math.max(0, msgs.size() - 30);
        for (int i = start; i < msgs.size(); i++) {
            Label l = new Label(msgs.get(i));
            l.setStyle("-fx-font-size: 11px; -fx-font-family: monospace;");
            logBox.getChildren().add(l);
        }
        logScroll.setVvalue(1.0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
