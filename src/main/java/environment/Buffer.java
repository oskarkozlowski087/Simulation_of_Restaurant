package environment;


import models.Order;
import java.util.ArrayList;
import java.util.List;

public class Buffer {
    private int x;
    private int y;
    private List<Order> pendingOrders; // Karteczki dla kucharzy
    private List<Order> readyMeals;    // Gotowe dania dla kelnerów

    public Buffer(int x, int y) {
        this.x = x;
        this.y = y;

        this.pendingOrders = new ArrayList<>();
        this.readyMeals = new ArrayList<>();
    }
    // METODY DLA KELNERA
    public void addOrder(Order order){
        pendingOrders.add(order);

    }
    public Order takeReadyMeal(){
        if (readyMeals.isEmpty()) {
            return null;
        }
        return readyMeals.remove(0);
    }
    public void addReadyMeal(Order order) {
        readyMeals.add(order);
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
