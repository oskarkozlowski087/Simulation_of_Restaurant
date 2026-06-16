package environment;

import models.Order;
import models.OrderStatus;

/**
 * Reprezentuje pojedynczą kuchenkę (palnik) w kuchni restauracji.
 * Odpowiada za przyjmowanie zamówień do realizacji oraz wydawanie gotowych dań.
 */
public class Stove {
    private int x;
    private int y;
    private Order currentDish;

    /**
     * Tworzy nową kuchenkę o podanych współrzędnych.
     *
     * @param x współrzędna X kuchenki
     * @param y współrzędna Y kuchenki
     */
    public Stove(int x, int y){
        this.x = x;
        this.y = y;
        currentDish = null;
    }
    /**
     * Umieszcza zamówienie na kuchence i zmienia jego status na W_PRZYGOTOWANIU.
     *
     * @param order zamówienie do przygotowania
     */
    public void insertOrder(Order order){
        this.currentDish = order;
        this.currentDish.setStatus(OrderStatus.W_PRZYGOTOWANIU);
    }
    /**
     * Zdejmuje gotowe danie z kuchenki.
     *
     * @return gotowe danie (zamówienie)
     */
    public Order takeOutOrder(){
        Order finishedDish = this.currentDish;
        this.currentDish = null;
        return finishedDish;
    }
    /**
     * Sprawdza, czy kuchenka jest zajęta (czy znajduje się na niej danie).
     *
     * @return true, jeśli kuchenka jest zajęta
     */
    public boolean isOccupied() {
        return this.currentDish != null;
    }

    /** Zwraca współrzędną X kuchenki. */
    public int getX() {
        return x;
    }

    /** Zwraca współrzędną Y kuchenki. */
    public int getY() {
        return y;
    }

    /** Zwraca aktualnie przygotowywane danie lub null. */
    public Order getCurrentDish() {
        return currentDish;
    }
}

