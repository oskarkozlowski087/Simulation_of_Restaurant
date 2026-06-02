package environment;

public class Table {
    private int x;
    private int y;
    private boolean isOccupied;
    public Table(int x, int y){
        this.x = x;
        this.y = y;
        this.isOccupied = false;
    }
    //gettery aby można było ustawić target
    public int getX(){
        return x;
    }
    public int getY() {
        return y;
    }
    public boolean getIsOccupied(){
        return isOccupied;
    }
    //settery aby np aby klient mógł zmienić occupation stolika
    public void setOccupied(boolean occupied){
        this.isOccupied = occupied;
    }
}
