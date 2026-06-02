package agents;

import environment.Table;

public class Client extends MovingAgent {

    private int patience;
    private int eatingTime;
    private boolean isEating;
    private Table assignedTable;

    public Client(int x, int y, int startPatience){
        super(x,y);
        this.isEating = false;
        this.patience = startPatience; //!!!!!!!!!!!
        this.eatingTime = 10;

    }

    public void takeTable(Table table){
        this.isOccupied = true;
        this.assignedTable = table;
        System.out.println("Klient zajął stolik i czeka na kelnera...");
    }

    public Order generateOrder(){
        System.out.println("Klient zamówił danie.");
    }

    public void reciveMeal(){
        this.isEating = true;
        System.out.println("Klient otrzymał danie!");
    }

    public void decrementTime() {
        if (isEating) {
            eatingTime--;
            if (eatingTime <= 0) {
                leaveRestaurant("najedzony i szczęśliwy!!!");
            }
        } else {
            patience--;
            if (patience <= 0) {
                leaveRestaurant("Jego cierpliwość się skończyła...");
            }
        }
    }

    public void leaveRestaurant(String reason) {
        System.out.println("Klient wychodzi z restauracji: " + reason);
        this.assignedTable = null;
        this.order = null;
        this.isOccupied = false;
    }
}

