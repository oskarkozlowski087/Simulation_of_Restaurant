package environment;
import agents.MovingAgent;

/**
 * Reprezentuje pojedynczą komórkę (pole) na planszy restauracji.
 * Przechowuje informacje o typie, położeniu, zajmującym ją agencie
 * oraz ewentualnym wyposażeniu (stolik, kuchenka, bufet).
 */
public class Cell {
    private final int x;
    private final int y;
    private final CellType type;
    private MovingAgent occupant;
    private Table table;
    private boolean isStove;
    private Buffer buffer;

    /**
     * Tworzy nową komórkę planszy o podanych współrzędnych i typie.
     *
     * @param x    współrzędna X
     * @param y    współrzędna Y
     * @param type typ komórki
     */
    public Cell(int x, int y, CellType type){
        this.x = x;
        this.y = y;
        this.type = type;
        this.occupant = null;
        this.table = null;
        this.isStove = false;
        this.buffer = null;
    }

    /**
     * Sprawdza, czy komórka jest przechodnia (nie zawiera stolika).
     *
     * @return true, jeśli pole jest przechodnie
     */
    public boolean isWalkable() {
        return this.table == null;
    }

    /** Zwraca współrzędną X komórki. */
    public int getX() { return x; }
    /** Zwraca współrzędną Y komórki. */
    public int getY() { return y; }
    /** Zwraca typ komórki. */
    public CellType getType() { return type; }

    /** Zwraca agenta zajmującego komórkę lub null. */
    public MovingAgent getOccupant() { return occupant; }
    /** Ustawia agenta zajmującego komórkę. */
    public void setOccupant(MovingAgent agent) { this.occupant = agent; }

    /** Zwraca stolik znajdujący się na tej komórce lub null. */
    public Table getTable() { return table; }
    /** Ustawia stolik na tej komórce. */
    public void setTable(Table table) { this.table = table; }

    /** Sprawdza, czy na komórce znajduje się kuchenka. */
    public boolean isStove() { return isStove; }
    /** Ustawia, czy na komórce znajduje się kuchenka. */
    public void setStove(boolean isStove) { this.isStove = isStove; }

    /** Zwraca bufet znajdujący się na tej komórce lub null. */
    public Buffer getBuffer() { return buffer; }
    /** Ustawia bufet na tej komórce. */
    public void setBuffer(Buffer buffer) { this.buffer = buffer; }
}
