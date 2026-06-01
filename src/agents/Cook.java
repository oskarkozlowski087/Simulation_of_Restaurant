package agents;

import environment.Stove;
import environment.Buffer;
import models.Order;


public class Cook extends MovingAgent {
    private Stove assignedStove;
    public Cook(int x, int y){
        super(x, y);
    }
    public void TakeNewOrder(Buffer buffer){
        System.out.println("Kucharz wziął zamówienie z buffera.");
        this.isOccupied = true;
    }
    public void PrepareMeal(Order order){
        System.out.println("Posiłek w trakcie przygotowania...");
    }
    public void DropMeal(Buffer buffer){
        System.out.println("Gotowy posiłek czeka na kelnera");
        this.isOccupied = false;
        this.order = null;
    }


}
