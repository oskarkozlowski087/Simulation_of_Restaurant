package gui;

import agents.Client;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class RestaurantApp extends Application {

    private static final int CELL = 45;
    private Simulation simulation;
    private Pane boardPane;
    private Label tickLabel;
    private Label bufferLabel;
    private VBox logBox;
    private ScrollPane logScroll;
    private VBox agentInfoBox;
    private Button startButton;
    private HBox agentBar;
    private AnimationTimer timer;
    private volatile boolean running = false;
    private double speedMultiplier = 1.0;

    @Override
    public void start(Stage primaryStage) {
        simulation = new Simulation();
        simulation.init();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        root.setTop(createTopBar());

        VBox centerBox = new VBox(8);
        centerBox.setPadding(new Insets(10));
        centerBox.setAlignment(Pos.CENTER);

        Pane boardWrapper = new Pane();
        boardWrapper.setStyle("-fx-background-color: #2d2d44; -fx-border-color: #4a4a6a; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
        boardWrapper.setEffect(new DropShadow(12, Color.web("#00000080")));

        boardPane = new Pane();
        boardPane.setPadding(new Insets(8));
        boardWrapper.getChildren().add(boardPane);
        centerBox.getChildren().add(boardWrapper);

        agentBar = createAgentStatusBar();
        centerBox.getChildren().add(agentBar);

        root.setCenter(centerBox);
        root.setRight(createRightPanel());

        Scene scene = new Scene(root, 1100, 780);
        primaryStage.setTitle("Symulacja Restauracji");
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshUI();

        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (!running) return;
                if (lastUpdate == 0) { lastUpdate = now; return; }
                long delay = (long) (120_000_000 / speedMultiplier);
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

    private HBox createTopBar() {
        HBox bar = new HBox(14);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #1a252f); -fx-border-color: #4a6a8a; -fx-border-width: 0 0 1 0;");

        Label title = new Label("🍽 Restauracja");
        title.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 20px; -fx-font-weight: bold;");

        startButton = new Button("▶ Start");
        startButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 22; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        startButton.setOnAction(e -> toggleSimulation());

        Button resetButton = new Button("⟳ Reset");
        resetButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 22; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        resetButton.setOnAction(e -> resetSimulation());

        tickLabel = new Label("Tick: 0");
        tickLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label speedLabel = new Label("⚡ Prędkość:");
        speedLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 13px;");

        Slider speedSlider = new Slider(0.25, 4.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setPrefWidth(140);
        speedSlider.valueProperty().addListener((obs, old, val) -> speedMultiplier = val.doubleValue());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(title, spacer, startButton, resetButton, tickLabel, speedLabel, speedSlider);
        return bar;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(12));
        panel.setPrefWidth(270);
        panel.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #4a6a8a; -fx-border-width: 0 0 0 1;");

        Label infoTitle = new Label("📊 Status");
        infoTitle.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");

        bufferLabel = new Label("📦 Buffer: 0 zamówień, 0 dań");
        bufferLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 13px;");
        bufferLabel.setPadding(new Insets(4, 0, 4, 0));

        agentInfoBox = new VBox(4);

        Label logLabel = new Label("📋 Log zdarzeń:");
        logLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold; -fx-font-size: 13px;");
        logLabel.setPadding(new Insets(8, 0, 2, 0));

        logBox = new VBox(2);
        logScroll = new ScrollPane(logBox);
        logScroll.setFitToWidth(true);
        logScroll.setStyle("-fx-background: #1a252f; -fx-background-color: #1a252f;");
        logScroll.setPrefHeight(300);

        panel.getChildren().addAll(infoTitle, bufferLabel, agentInfoBox, logLabel, logScroll);
        VBox.setVgrow(logScroll, Priority.ALWAYS);
        return panel;
    }

    private HBox createAgentStatusBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(6, 10, 6, 10));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #2d2d44; -fx-border-color: #4a4a6a; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        bar.setPrefHeight(36);
        return bar;
    }

    private void updateAgentStatusBar(HBox bar) {
        bar.getChildren().clear();
        int cookCount = simulation.getCooks().size();
        int waiterCount = simulation.getWaiters().size();
        int clientCount = 0;
        for (Client c : simulation.getClients()) {
            if (c.getAssignedTable() != null) clientCount++;
        }

        Label cooksL = new Label("👨‍🍳 Kucharze: " + cookCount);
        cooksL.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label waitersL = new Label("🧑‍💼 Kelnerzy: " + waiterCount);
        waitersL.setStyle("-fx-text-fill: #3498db; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label clientsL = new Label("👤 Klienci: " + clientCount + "/6");
        clientsL.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label tablesL = new Label("🪑 Stoliki: " + countFreeTables() + "/6 wolnych");
        tablesL.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 13px; -fx-font-weight: bold;");

        bar.getChildren().addAll(cooksL, waitersL, clientsL, tablesL);
    }

    private int countFreeTables() {
        int free = 0;
        for (Table t : simulation.getTables()) {
            if (!t.getIsOccupied()) free++;
        }
        return free;
    }

    private void toggleSimulation() {
        if (running) {
            running = false;
            startButton.setText("▶ Start");
        } else {
            simulation.startSimulation();
            running = true;
            startButton.setText("⏸ Stop");
            Platform.runLater(this::refreshUI);
        }
    }

    private void resetSimulation() {
        running = false;
        startButton.setText("▶ Start");
        simulation = new Simulation();
        simulation.init();
        refreshUI();
    }

    private void refreshUI() {
        tickLabel.setText("⚙ Tick: " + simulation.getTick());
        updateBoardPane();
        updateBufferLabel();
        updateLog();
        updateAgentInfo();
    }

    private void updateBoardPane() {
        boardPane.getChildren().clear();
        Board board = simulation.getBoard();
        int w = board.getWidth();
        int h = board.getHeight();
        boardPane.setPrefSize(w * CELL + 16, h * CELL + 16);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Cell cell = board.getCell(x, y);
                double px = 8 + x * CELL;
                double py = 8 + (h - 1 - y) * CELL;

                Rectangle rect = new Rectangle(px, py, CELL, CELL);
                rect.setArcWidth(4);
                rect.setArcHeight(4);

                switch (cell.getType()) {
                    case KITCHEN -> rect.setFill(Color.web("#3a3a5c"));
                    case HALL -> rect.setFill(Color.web("#f5e6d3"));
                    case WALL -> rect.setFill(Color.web("#6d3a1f"));
                    case BUFFER -> rect.setFill(Color.web("#ffd700"));
                    default -> rect.setFill(Color.web("#e0e0e0"));
                }
                rect.setStroke(Color.web("#22222244"));
                rect.setStrokeWidth(0.5);
                boardPane.getChildren().add(rect);

                if (cell.isStove()) {
                    Stove stove = null;
                    for (Stove s : simulation.getStoves()) {
                        if (s.getX() == x && s.getY() == y) { stove = s; break; }
                    }
                    Rectangle stoveRect = new Rectangle(px + 3, py + 3, CELL - 6, CELL - 6);
                    stoveRect.setArcWidth(6);
                    stoveRect.setArcHeight(6);
                    stoveRect.setFill(stove != null && stove.isOccupied() ? Color.web("#ff4500") : Color.web("#8B4513"));
                    stoveRect.setStroke(Color.web("#553311"));
                    stoveRect.setStrokeWidth(1.5);
                    boardPane.getChildren().add(stoveRect);
                    Text sText = new Text(px + 8, py + 30, "🍳");
                    sText.setFont(Font.font(20));
                    boardPane.getChildren().add(sText);
                }

                if (cell.getTable() != null) {
                    boolean occ = cell.getTable().getIsOccupied();
                    Rectangle tRect = new Rectangle(px + 4, py + 4, CELL - 8, CELL - 8);
                    tRect.setArcWidth(8);
                    tRect.setArcHeight(8);
                    tRect.setFill(occ ? Color.web("#a0522d") : Color.web("#cd853f"));
                    tRect.setStroke(Color.web("#5c2e00"));
                    tRect.setStrokeWidth(1.5);
                    boardPane.getChildren().add(tRect);
                    Text tText = new Text(px + 10, py + 30, occ ? "🍽" : "🪑");
                    tText.setFont(Font.font(20));
                    boardPane.getChildren().add(tText);
                }

                if (cell.getBuffer() != null) {
                    int pend = simulation.getBuffer().getPendingCount();
                    int ready = simulation.getBuffer().getReadyCount();
                    Text bText = new Text(px + 4, py + 14, "📋");
                    bText.setFont(Font.font(16));
                    boardPane.getChildren().add(bText);
                    Text bCount = new Text(px + 24, py + 14, pend + "/" + ready);
                    bCount.setFill(Color.web("#5c4a00"));
                    bCount.setFont(Font.font(10));
                    boardPane.getChildren().add(bCount);
                }

                MovingAgent occupant = cell.getOccupant();
                if (occupant != null) {
                    String icon;
                    Color col;
                    if (occupant instanceof Cook) {
                        icon = "👨‍🍳"; col = Color.RED;
                    } else if (occupant instanceof Waiter) {
                        icon = "🧑‍💼"; col = Color.DODGERBLUE;
                    } else {
                        icon = "👤"; col = Color.FORESTGREEN;
                    }

                    Circle shadow = new Circle(px + CELL / 2 + 1, py + CELL / 2 + 1, 15, Color.web("#00000040"));
                    boardPane.getChildren().add(shadow);

                    Circle bg = new Circle(px + CELL / 2, py + CELL / 2, 15, col);
                    bg.setStroke(Color.WHITE);
                    bg.setStrokeWidth(2);
                    boardPane.getChildren().add(bg);

                    Text agentIcon = new Text(px + CELL / 2 - 10, py + CELL / 2 + 7, icon);
                    agentIcon.setFont(Font.font(16));
                    boardPane.getChildren().add(agentIcon);

                    if (occupant instanceof Client) {
                        Client cl = (Client) occupant;
                        double pct = cl.getPatience() / 10.0;
                        if (pct < 0) pct = 0;
                        Rectangle patienceBar = new Rectangle(px + 4, py + CELL - 6, (CELL - 8) * pct, 4);
                        patienceBar.setFill(pct > 0.5 ? Color.LIME : pct > 0.25 ? Color.ORANGE : Color.RED);
                        patienceBar.setArcWidth(2);
                        patienceBar.setArcHeight(2);
                        boardPane.getChildren().add(patienceBar);
                    }
                }
            }
        }
    }

    private void updateBufferLabel() {
        Buffer b = simulation.getBuffer();
        bufferLabel.setText("📦 Buffer: " + b.getPendingCount() + " zamówień, " + b.getReadyCount() + " dań");
    }

    private void updateLog() {
        List<String> msgs = simulation.getLogMessages();
        logBox.getChildren().clear();
        int start = Math.max(0, msgs.size() - 40);
        for (int i = start; i < msgs.size(); i++) {
            Label l = new Label(msgs.get(i));
            l.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 10px; -fx-font-family: monospace;");
            logBox.getChildren().add(l);
        }
        logScroll.setVvalue(1.0);
    }

    private void updateAgentInfo() {
        agentInfoBox.getChildren().clear();
        for (Cook c : simulation.getCooks()) {
            String status = c.isOccupied() ? "gotuje" : "wolny";
            Label l = new Label("👨‍🍳 Kucharz (" + c.getX() + "," + c.getY() + ") → (" + c.getTarX() + "," + c.getTarY() + ") " + status);
            l.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");
            agentInfoBox.getChildren().add(l);
        }
        for (Waiter w : simulation.getWaiters()) {
            String status = w.isOccupied() ? "niesie danie" : "szuka klienta";
            Label l = new Label("🧑‍💼 Kelner (" + w.getX() + "," + w.getY() + ") → (" + w.getTarX() + "," + w.getTarY() + ") " + status);
            l.setStyle("-fx-text-fill: #3498db; -fx-font-size: 11px;");
            agentInfoBox.getChildren().add(l);
        }
        for (Client c : simulation.getClients()) {
            if (c.getAssignedTable() == null) continue;
            Table t = c.getAssignedTable();
            String status = c.isOccupied() ? "je" : "czeka";
            Label l = new Label("👤 Klient stolik(" + t.getX() + "," + t.getY() + ") cierpliwosc:" + c.getPatience() + " " + status);
            l.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 11px;");
            agentInfoBox.getChildren().add(l);
        }

        updateAgentStatusBar(agentBar);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
