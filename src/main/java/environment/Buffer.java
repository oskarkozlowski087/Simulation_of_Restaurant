package environment;


import models.Order;
import java.util.ArrayList;
import java.util.List;

public class    Buffer {
    private int x;
    private int y;
    private List<Order> pendingOrders; // Karteczki dla kucharzy
    private List<Order> readyMeals;    // Gotowe dania dla kelnerów
    private int maxPending;

    public Buffer(int x, int y) {
        this.x = x;
        this.y = y;

        this.pendingOrders = new ArrayList<>();
        this.readyMeals = new ArrayList<>();
        this.maxPending = 0;
    }
    // METODY DLA KELNERA
    public void addOrder(Order order){
        pendingOrders.add(order);
        if (pendingOrders.size() > maxPending) maxPending = pendingOrders.size();
    }
    public Order takeReadyMeal(){
        if (readyMeals.isEmpty()) {
            return null;
        }
        return readyMeals.remove(0);
    }
    //METODY DLA KUCHARZA
    public void addReadyMeal(Order order) {
        readyMeals.add(order);
    }

    public Order takeOrder() {
        if (pendingOrders.isEmpty()) {
            return null;
        }
        return pendingOrders.remove(0);
    }
    public int getPendingCount() {
        return pendingOrders.size();
    }

    public int getReadyCount() {
        return readyMeals.size();
    }

    public int getMaxPending() {
        return maxPending;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
