package environment;

import models.Order;
import models.OrderStatus;

public class Stove {
    private int x;
    private int y;
    private Order currentDish;

    public Stove(int x, int y){
        this.x = x;
        this.y = y;
        currentDish = null;
    }
    public void insertOrder(Order order){
        this.currentDish = order;
        this.currentDish.setStatus(OrderStatus.W_PRZYGOTOWANIU);
    }
    public Order takeOutOrder(){
        Order finishedDish = this.currentDish;
        this.currentDish = null;
        return finishedDish;
    }
    // Sprawdza, czy palnik jest zajęty.
    // Jeśli currentDish nie jest nullem, to znaczy, że coś tam leży.
    public boolean isOccupied() {
        return this.currentDish != null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Order getCurrentDish() {
        return currentDish;
    }
}

