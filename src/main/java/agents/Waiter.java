package agents;

import core.SimulationStats;
import environment.Table;
import environment.Buffer;
import models.Order;
import models.OrderStatus;
import java.util.List;

public class Waiter extends MovingAgent {

    private Buffer buffer;
    private SimulationStats stats;

    private List<Client> allClients;

    private Client currentTargetClient = null;

    public Waiter(int x, int y, Buffer buffer, List<Client> allClients, SimulationStats stats) {
        super(x, y);
        this.buffer = buffer;
        this.allClients = allClients;
        this.stats = stats;
    }

    @Override
    public void scanBoard() {
        // 1. niosę gotowe danie -> dostarczam
        if (this.order != null && this.order.getStatus() == OrderStatus.GOTOWE) {
            Client target = this.order.getClient();
            if (target == null) return;
            if (this.x == target.getX() && this.y == target.getY()) {
                deliverOrder();
            } else {
                this.tarX = target.getX();
                this.tarY = target.getY();
                move();
            }
            return;
        }

        // 2. niosę zamówienie -> zostawiam w buforze
        if (this.order != null && this.order.getStatus() == OrderStatus.ZLOZONE) {
            if (this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
                dropOrderAtBuffer();
            } else {
                this.tarX = this.buffer.getX();
                this.tarY = this.buffer.getY();
                move();
            }
            return;
        }

        // 3. puste ręce i jestem przy buforze -> biorę gotowe danie
        if (this.order == null && this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
            if (pickOrderFromBuffer()) {
                return;
            }
        }

        // 4. puste ręce -> szukam klienta lub idę do bufora
        if (this.order == null) {
            if (this.currentTargetClient == null) {
                findMostImpatientClientToServe();
            }
            if (this.currentTargetClient != null) {
                if (this.x == this.currentTargetClient.getX() && this.y == this.currentTargetClient.getY()) {
                    pickUpOrder();
                } else {
                    this.tarX = this.currentTargetClient.getX();
                    this.tarY = this.currentTargetClient.getY();
                    move();
                }
            } else if (this.buffer.getReadyCount() > 0) {
                this.tarX = this.buffer.getX();
                this.tarY = this.buffer.getY();
                move();
            }
        }
    }

    private void findMostImpatientClientToServe() {
        int lowestPatience = 999999;
        Client mostImpatientClient = null;

        for (Client client : allClients) {
            if (client.wantsToOrder()) {
                if (client.getPatience() < lowestPatience) {
                    lowestPatience = client.getPatience();
                    mostImpatientClient = client;
                }
            }
        }

        if (mostImpatientClient != null) {
            this.currentTargetClient = mostImpatientClient;
            mostImpatientClient.setAssignedWaiter(this);
        }
    }

    public void pickUpOrder() {
        Order clientOrder = this.currentTargetClient.takeOrder();

        if (clientOrder != null) {
            this.order = clientOrder;
            this.isOccupied = true;
            System.out.println("Kelner odebrał zamówienie od stolika.");
        }
    }

    public void deliverOrder() {
        this.currentTargetClient.reciveMeal();
        if (stats != null) stats.onMealDelivered();
        System.out.println("Danie zostało podane!");

        this.order = null;
        this.isOccupied = false;
        this.currentTargetClient = null;
    }

    public void dropOrderAtBuffer() {
        this.order.setStatus(OrderStatus.W_BUFORZE);
        this.buffer.addOrder(this.order);
        if (stats != null) stats.onWaiterTrip();
        System.out.println("Kelner zostawił zamówienie na ladzie dla kucharza.");

        this.order = null;
        this.currentTargetClient = null;
    }

    public boolean pickOrderFromBuffer() {
        Order readyMeal = this.buffer.takeReadyMeal();

        if (readyMeal != null) {
            this.order = readyMeal;
            this.currentTargetClient = readyMeal.getClient();
            System.out.println("Kelner odebrał gotowe danie z lady.");
            return true;
        }
        return false;
    }

    private void move() {
        if (this.x < this.tarX) this.x++;
        else if (this.x > this.tarX) this.x--;

        if (this.y < this.tarY) this.y++;
        else if (this.y > this.tarY) this.y--;
    }
}
