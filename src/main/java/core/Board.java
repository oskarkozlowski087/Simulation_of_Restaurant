package core;

import environment.Cell;
import environment.CellType;
import agents.MovingAgent;
import java.util.List;
import java.util.ArrayList;

public class Board{
private final int width;
private final int height;
private final Cell[][] grid;
private final List<MovingAgent> activeAgents;

    //Konstruktor podający wzrost i wysokość, tworzy nową planszę
public Board(int width, int height) {
    this.width = width;
    this.height = height;
    this.grid = new Cell[width][height];
    this.activeAgents = new ArrayList<>();
}

 // Metoda odpowiedzialna za tworzenie planszy
private void initializeGrid() {
}

public Cell getCell (int x, int y){
    return null;
}

    //sprawdzenie, czy agent może wejść na pole
public boolean canMoveTo(int x, int y){
    Cell cell = getCell(x,y);
    return cell != null && cell.isWalkable() && cell.getOccupant() == null;
}
// metoda rejestruje agenta i stawia go na odpowiedniej komorce
public void registerAgent(MovingAgent agent, int startX, int startY ) {
}

public int getWidth() {
    return width;
}
public int getHeight() {
    return height;
}
public List<MovingAgent> getActiveAgents() {
    return activeAgents;
}
}
