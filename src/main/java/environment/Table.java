package environment;

/**
 * Reprezentuje stolik w sali restauracyjnej.
 * Przechowuje informację o położeniu oraz czy jest aktualnie zajęty przez klienta.
 */
public class Table {
    private int x;
    private int y;
    private boolean isOccupied;
    /**
     * Tworzy nowy stolik o podanych współrzędnych.
     * Początkowo stolik jest wolny.
     *
     * @param x współrzędna X stolika
     * @param y współrzędna Y stolika
     */
    public Table(int x, int y){
        this.x = x;
        this.y = y;
        this.isOccupied = false;
    }
    /** Zwraca współrzędną X stolika. */
    public int getX(){
        return x;
    }
    /** Zwraca współrzędną Y stolika. */
    public int getY() {
        return y;
    }
    /** Sprawdza, czy stolik jest zajęty. */
    public boolean getIsOccupied(){
        return isOccupied;
    }
    /** Ustawia stan zajętości stolika. */
    public void setOccupied(boolean occupied){
        this.isOccupied = occupied;
    }
}
