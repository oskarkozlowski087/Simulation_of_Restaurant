package agents;

import environment.Table;
import models.Order;
import java.util.List;

public class Client extends MovingAgent {
    private int patience;
    private int eatingTime;
    private boolean isEating;
    private Table assignedTable;
    private List<Table> allTables;
    private boolean hasLeft = false;

    // "Kieszeń" na kelnera, który aktualnie obsługuje tego klienta
    private Waiter assignedWaiter = null;

    public Client(int x, int y, int startPatience, List<Table> allTables) {
        super(x, y);
        this.isEating = false;
        this.patience = startPatience;
        this.eatingTime = 10;
        this.assignedTable = null;
        this.allTables = allTables;
    }

    @Override
    public void scanBoard() {
        // 1. Klient stoi w drzwiach
        if (assignedTable == null) {
            findAndClaimFreeTable(); // proba znalezienia stolika

            if (assignedTable == null) {
                patience--; //traci cierpliwosc zanim usiadzie
                if (patience <= 0) {
                    leaveRestaurant("Zbyt mało wolnych stolików, uciekam!");
                }
            }
        }
        //Movement
        else if (this.x != assignedTable.getX() || this.y != assignedTable.getY()) {
            this.tarX = assignedTable.getX();
            this.tarY = assignedTable.getY();
            move();
        }
        //czekanie na kelnera przy stoliku
        else if (!isEating) {
            if (this.order == null && this.assignedWaiter == null) {
                generateOrder();
            }

            //client traci cierpliwosc tylko wtedy jak nie obsluzyl go kelner. Usuwa to problem z jedzeniem.
            if (this.assignedWaiter == null) {
                patience--;
                if (patience <= 0) {
                    leaveRestaurant("Nikt do mnie nie podszedł, wychodzę!");
                }
            }
        }
        else if (isEating) {
            eatingTime--;
            if (eatingTime <= 0) {
                leaveRestaurant("Najedzony i szczęśliwy!!!");
            }
        }
    }

    private void findAndClaimFreeTable() {
        for (Table table : allTables) {
            if (!table.getIsOccupied()) {
                table.setOccupied(true);
                takeTable(table);
                return;
            }
        }
    }

    public void takeTable(Table table) {
        this.isOccupied = true;
        this.assignedTable = table;
        System.out.println("Klient zajął stolik i idzie w jego stronę...");
    }

    public Order generateOrder() {
        System.out.println("Klient usiadł i wymyślił zamówienie.");
        this.order = new Order(20);
        return this.order;
    }

    public void reciveMeal() {
        this.isEating = true;
        this.assignedWaiter = null;
        System.out.println("Klient otrzymał danie i zaczyna jeść!");
    }

    public void leaveRestaurant(String reason) {
        System.out.println("Klient wychodzi z restauracji: " + reason);
        if (assignedTable != null) {
            assignedTable.setOccupied(false); // Zwalnia stolik dla innych
        }

        this.assignedTable = null;
        this.order = null;
        this.isOccupied = false;
        this.assignedWaiter = null;
        this.hasLeft = true;
    }

    // metoda chodzenia
    private void move() {
        if (this.x < this.tarX) this.x++;
        else if (this.x > this.tarX) this.x--;

        if (this.y < this.tarY) this.y++;
        else if (this.y > this.tarY) this.y--;
    }

    // gettery i settery
    public Table getAssignedTable() { return assignedTable; }
    public Waiter getAssignedWaiter() { return this.assignedWaiter; }
    public void setAssignedWaiter(Waiter waiter) { this.assignedWaiter = waiter; }

    public Order takeOrder() {
        Order o = this.order;
        this.order = null; // Oddaje karteczkę kelnerowi
        return o;
    }

    public boolean wantsToOrder() {
        boolean isAtTable = (this.assignedTable != null && this.x == this.assignedTable.getX() && this.y == this.assignedTable.getY());
        return (!this.isEating && this.assignedWaiter == null && isAtTable && this.order != null);
    }

    public int getPatience() { return this.patience; }
    public boolean hasLeft() {
        return this.hasLeft;
    }
}