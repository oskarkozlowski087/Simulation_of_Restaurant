package core;

import environment.Buffer;
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
public Board(int width, int height) { //pamietac zeby przy symulacji okreslic minimalny rozmiar

    this.width = width;
    this.height = height;
    this.grid = new Cell[width][height];
    this.activeAgents = new ArrayList<>();

    initializeGrid();
}

 // Metoda odpowiedzialna za tworzenie planszy i przypisywanie typów do planszy
private void initializeGrid() {
    for(int x = 0; x < width; x++){
        for (int y = 0; y< height; y++) {
            CellType type;

            if (y < 4){ //wielkosc kuchni
                type = CellType.KITCHEN;
            } else if (y == 4){
                if ( x >= width / 3 && x <= (2*width)/3){ // dlugosc buffera (ok 1/3 sciany)
                    type = CellType.BUFFER;
                } else {
                    type = CellType.WALL;
                }
            } else {
                type = CellType.HALL; // reszta to poprostu sala
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

public Cell getCell (int x, int y){
    if (x >= 0 && x < width && y >= 0 && y < height){
        return grid[x][y];
    }
    return null;
}

    // Sprawdza, czy agent może wejść na pole
public boolean canMoveTo(int x, int y){
    Cell cell = getCell(x,y);
    return cell != null && cell.isWalkable() && cell.getOccupant() == null;
}
    // Metoda rejestruje agenta i stawia go na odpowiedniej komorce
    public void registerAgent(MovingAgent agent, int startX, int startY) {
        Cell cell = getCell(startX, startY);
        if (cell != null && cell.getOccupant() == null) {
            cell.setOccupant(agent);
            activeAgents.add(agent);
        }
    }

    // Inicjujemy metodę, która pozwoli wykorzystać wzorzec AgentFactory
    public void spawnAgent(String type, int startX, int startY) {
        Cell cell = getCell(startX, startY);

        // Sprawdzamy, czy pole istnieje i czy jest wolne
        if (cell != null && cell.isWalkable() && cell.getOccupant() == null) {

            // Fabryka tworzy agenta wysyła prosbę do fabryki i dostaje w zamian obiekt
            MovingAgent newAgent = AgentFactory.createAgent(type, startX, startY);

            if (newAgent != null) {
                cell.setOccupant(newAgent);   // Przypisujemy agenta do komórki
                activeAgents.add(newAgent);  // Agent jest dodawany do listy aktywnych agentów
            }
        } else {
            System.out.println("Nie można utworzyć agenta " + type + " na pozycji (" + startX + ", " + startY + ") - pole zajęte lub nieprawidłowe!");
        }
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
