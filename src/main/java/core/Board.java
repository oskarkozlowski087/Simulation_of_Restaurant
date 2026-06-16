package core;

import environment.Buffer;
import environment.Cell;
import environment.CellType;
import agents.MovingAgent;
import java.util.List;
import java.util.ArrayList;

/**
 * Reprezentuje planszę (mapę) restauracji w symulacji.
 * Zarządza siatką komórek ({@link Cell}) oraz listą aktywnych agentów.
 * Odpowiada za inicjalizację układu pomieszczeń (kuchnia, bufet, sala).
 */
public class Board{
private final int width;
private final int height;
private final Cell[][] grid;
private final List<MovingAgent> activeAgents;

    /**
     * Tworzy nową planszę o podanych wymiarach i inicjalizuje siatkę komórek.
     *
     * @param width  szerokość planszy (liczba kolumn)
     * @param height wysokość planszy (liczba wierszy)
     */
public Board(int width, int height) {

    this.width = width;
    this.height = height;
    this.grid = new Cell[width][height];
    this.activeAgents = new ArrayList<>();

    initializeGrid();
}

 /**
  * Inicjalizuje siatkę komórek planszy, przypisując każdemu polu odpowiedni typ:
  * kuchnia (y &lt; 4), bufet lub ściana (y == 4), sala (y > 4).
  * Dla komórek typu BUFFER tworzy dodatkowo obiekt {@link Buffer}.
  */
private void initializeGrid() {
    for(int x = 0; x < width; x++){
        for (int y = 0; y< height; y++) {
            CellType type;

            if (y < 4){
                type = CellType.KITCHEN;
            } else if (y == 4){
                if ( x >= width / 3 && x <= (2*width)/3){
                    type = CellType.BUFFER;
                } else {
                    type = CellType.WALL;
                }
            } else {
                type = CellType.HALL;
            }

            grid[x][y] = new Cell(x, y, type);
            if (type == CellType.BUFFER) {
                grid[x][y].setBuffer(new Buffer(x, y));
            }
            System.out.print("[" + grid[x][y].getType() + "]");

        }
        System.out.println();

    }

}

    /**
     * Zwraca komórkę planszy na podanych współrzędnych.
     *
     * @param x współrzędna X
     * @param y współrzędna Y
     * @return komórka na podanej pozycji lub null, jeśli współrzędne są poza zakresem
     */
public Cell getCell (int x, int y){
    if (x >= 0 && x < width && y >= 0 && y < height){
        return grid[x][y];
    }
    return null;
}

    /**
     * Sprawdza, czy agent może wejść na pole o podanych współrzędnych.
     * Pole musi istnieć, być przechodnie i niezajęte przez innego agenta.
     *
     * @param x współrzędna X
     * @param y współrzędna Y
     * @return true, jeśli pole jest dostępne do wejścia
     */
public boolean canMoveTo(int x, int y){
    Cell cell = getCell(x,y);
    return cell != null && cell.isWalkable() && cell.getOccupant() == null;
}
    /**
     * Rejestruje agenta na planszy i umieszcza go na wskazanej komórce startowej.
     *
     * @param agent  agent do zarejestrowania
     * @param startX współrzędna X startowa
     * @param startY współrzędna Y startowa
     */
    public void registerAgent(MovingAgent agent, int startX, int startY) {
        Cell cell = getCell(startX, startY);
        if (cell != null && cell.getOccupant() == null) {
            cell.setOccupant(agent);
            activeAgents.add(agent);
        }
    }

    /**
     * Tworzy nowego agenta za pomocą fabryki {@link AgentFactory}
     * i umieszcza go na planszy, jeśli docelowe pole jest wolne i przechodnie.
     *
     * @param type   typ agenta ("COOK", "WAITER", "CLIENT")
     * @param startX współrzędna X startowa
     * @param startY współrzędna Y startowa
     */
    public void spawnAgent(String type, int startX, int startY) {
        Cell cell = getCell(startX, startY);

        if (cell != null && cell.isWalkable() && cell.getOccupant() == null) {

            MovingAgent newAgent = AgentFactory.createAgent(type, startX, startY);

            if (newAgent != null) {
                cell.setOccupant(newAgent);
                activeAgents.add(newAgent);
            }
        } else {
            System.out.println("Nie można utworzyć agenta " + type + " na pozycji (" + startX + ", " + startY + ") - pole zajęte lub nieprawidłowe!");
        }
    }

/** Zwraca szerokość planszy. */
public int getWidth() {
    return width;
}
/** Zwraca wysokość planszy. */
public int getHeight() {
    return height;
}
/** Zwraca listę aktywnych agentów na planszy. */
public List<MovingAgent> getActiveAgents() {
    return activeAgents;
}
}
