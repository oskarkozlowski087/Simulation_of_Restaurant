package environment;
import agents.MovingAgent;

public class Cell {
    private final int x;
    private final int y;
    private final CellType type;
    private MovingAgent occupant;
    private Table table;
    private boolean isStove;
    private Buffer buffer;

    public Cell(int x, int y, CellType type){
        this.x = x;
        this.y = y;
        this.type = type;
        this.occupant = null;
        this.table = null;
        this.isStove = false;
        this.buffer = null;
    }

    public boolean isWalkable() {
        return this.table == null;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public CellType getType() { return type; }

    public MovingAgent getOccupant() { return occupant; }
    public void setOccupant(MovingAgent agent) { this.occupant = agent; }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public boolean isStove() { return isStove; }
    public void setStove(boolean isStove) { this.isStove = isStove; }

    public Buffer getBuffer() { return buffer; }
    public void setBuffer(Buffer buffer) { this.buffer = buffer; }
}
