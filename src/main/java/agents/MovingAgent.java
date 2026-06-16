package agents;

import models.Order;

/**
 * Abstrakcyjna klasa bazowa dla wszystkich agentów poruszających się po planszy.
 * Zawiera wspólne właściwości: pozycję, cel podróży, stan zajętości oraz aktualne zamówienie.
 */
public abstract class MovingAgent {
    protected int x;
    protected int y;
    protected int tarX;
    protected int tarY;
    protected boolean isOccupied;

    protected Order order;

    /**
     * Tworzy nowego agenta na podanej pozycji.
     *
     * @param x współrzędna X początkowa
     * @param y współrzędna Y początkowa
     */
    public MovingAgent(int x, int y){
        this.x = x;
        this.y = y;
        this.isOccupied = false;
    }

    /** Sprawdza, czy agent jest aktualnie zajęty. */
    public boolean isOccupied(){
        return isOccupied;
    }

    /**
     * Metoda wywoływana w każdym ticku symulacji.
     * Powinna zawierać logikę podejmowania decyzji przez agenta.
     */
    public void scanBoard(){

    }
    /** Zwraca współrzędną X agenta. */
    public int getX() {
        return this.x;
    }

    /** Zwraca współrzędną Y agenta. */
    public int getY() {
        return this.y;
    }

    /** Zwraca docelową współrzędną X agenta. */
    public int getTarX() {
        return tarX;
    }

    /** Zwraca docelową współrzędną Y agenta. */
    public int getTarY() {
        return tarY;
    }


}
