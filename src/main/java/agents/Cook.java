package agents;

import models.Order;
import environment.Buffer;
import environment.Stove;
import models.OrderStatus;

public class Cook extends MovingAgent {

    private Stove assignedStove;
    private Buffer buffer;
    private int cookingTimer = 0;

    public Cook(int x, int y, Stove assignedStove, Buffer buffer) {
        super(x, y);
        this.assignedStove = assignedStove;
        this.buffer = buffer;
    }

    @Override
    public void scanBoard() {

        // 1. puste ręce - idziemy po zamówienie
        if (this.order == null) {
            if (this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
                takeNewOrder();
            } else {
                this.tarX = this.buffer.getX();
                this.tarY = this.buffer.getY();
                move();
            }
        }
        // 2. danie w buforze lub sie robi
        else if (this.order.getStatus() == OrderStatus.W_BUFORZE || this.order.getStatus() == OrderStatus.W_PRZYGOTOWANIU) {
            if (this.x == this.assignedStove.getX() && this.y == this.assignedStove.getY()) {
                prepareMeal(); // Odpalamy wrzucenie na palnik lub kontynuujemy odliczanie
            } else {
                this.tarX = this.assignedStove.getX();
                this.tarY = this.assignedStove.getY();
                move();
            }
        }
        // 3.  gotowe danie - odnosimy na Bufor
        else if (this.order.getStatus() == OrderStatus.GOTOWE) {
            if (this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
                dropMeal();
            } else {
                this.tarX = this.buffer.getX();
                this.tarY = this.buffer.getY();
                move();
            }
        }
    }

    public void takeNewOrder() {
        Order presentOrder = this.buffer.takeOrder();

        if (presentOrder == null) {
            return;
        }

        this.order = presentOrder;
        System.out.println("Kucharz wziął zamówienie z buffera.");
        this.isOccupied = true;
    }

    public void prepareMeal() {
        if (!this.assignedStove.isOccupied()) {
            this.assignedStove.insertOrder(this.order); //stove zmienia status wiec tu go nie dodajemy
        }

        // kolejne ticki i kroki w petli simulation
        this.cookingTimer++;
        int requiredTime = this.order.getTimeOrder();

        // Sprawdzamy, czy gotowe
        if (this.cookingTimer >= requiredTime) {
            this.order = this.assignedStove.takeOutOrder();
            this.order.setStatus(OrderStatus.GOTOWE);
            this.cookingTimer = 0;
            System.out.println("Posiłek gotowy! Zdejmuję z palnika.");
        } else {
            System.out.println("Posiłek w trakcie przygotowania... (" + this.cookingTimer + "/" + requiredTime + ")");
        }
    }

    public void dropMeal() {
        if (this.order != null && this.order.getStatus() == OrderStatus.GOTOWE) {
            this.buffer.addReadyMeal(this.order);
            System.out.println("Gotowy posiłek czeka na kelnera.");

            // Kucharz znow zostaje bez dania
            this.order = null;
            this.isOccupied = false;
        }
    }

    //metoda do chodzenia
    private void move() {
        if (this.x < this.tarX) this.x++;
        else if (this.x > this.tarX) this.x--;

        if (this.y < this.tarY) this.y++;
        else if (this.y > this.tarY) this.y--;
    }
}