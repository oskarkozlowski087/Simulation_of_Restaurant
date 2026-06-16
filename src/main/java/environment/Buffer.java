package environment;


import models.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje bufet (ladę) w restauracji, służący jako punkt pośredni
 * do przekazywania zamówień między kelnerami a kucharzami.
 * Przechowuje oczekujące zamówienia (dla kucharzy) oraz gotowe dania (dla kelnerów).
 */
public class    Buffer {
    private int x;
    private int y;
    private List<Order> pendingOrders;
    private List<Order> readyMeals;
    private int maxPending;

    /**
     * Tworzy nowy bufet o podanych współrzędnych.
     *
     * @param x współrzędna X bufetu
     * @param y współrzędna Y bufetu
     */
    public Buffer(int x, int y) {
        this.x = x;
        this.y = y;

        this.pendingOrders = new ArrayList<>();
        this.readyMeals = new ArrayList<>();
        this.maxPending = 0;
    }
    /**
     * Dodaje nowe zamówienie do kolejki oczekujących (dla kucharzy).
     * Aktualizuje maksymalną długość kolejki, jeśli jest to nowe maksimum.
     *
     * @param order zamówienie do dodania
     */
    public void addOrder(Order order){
        pendingOrders.add(order);
        if (pendingOrders.size() > maxPending) maxPending = pendingOrders.size();
    }
    /**
     * Pobiera gotowe danie z bufetu (dla kelnera).
     *
     * @return gotowe danie lub null, jeśli brak gotowych dań
     */
    public Order takeReadyMeal(){
        if (readyMeals.isEmpty()) {
            return null;
        }
        return readyMeals.remove(0);
    }
    /**
     * Dodaje gotowe danie do bufetu (przez kucharza).
     *
     * @param order gotowe danie do dodania
     */
    public void addReadyMeal(Order order) {
        readyMeals.add(order);
    }

    /**
     * Pobiera oczekujące zamówienie z bufetu (dla kucharza).
     *
     * @return oczekujące zamówienie lub null, jeśli brak zamówień
     */
    public Order takeOrder() {
        if (pendingOrders.isEmpty()) {
            return null;
        }
        return pendingOrders.remove(0);
    }
    /** Zwraca liczbę oczekujących zamówień. */
    public int getPendingCount() {
        return pendingOrders.size();
    }

    /** Zwraca liczbę gotowych dań. */
    public int getReadyCount() {
        return readyMeals.size();
    }

    /** Zwraca maksymalną zanotowaną długość kolejki oczekujących zamówień. */
    public int getMaxPending() {
        return maxPending;
    }

    /** Zwraca współrzędną X bufetu. */
    public int getX() {
        return x;
    }

    /** Zwraca współrzędną Y bufetu. */
    public int getY() {
        return y;
    }
}
