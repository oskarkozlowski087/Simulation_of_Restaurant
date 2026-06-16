package agents;

import core.SimulationStats;
import environment.Table;
import environment.Buffer;
import models.Order;
import models.OrderStatus;
import java.util.List;

/**
 * Reprezentuje kelnera w symulacji restauracji.
 * Kelner odbiera zamówienia od klientów, dostarcza je do bufetu,
 * a następnie przenosi gotowe dania z bufetu do klientów.
 */
public class Waiter extends MovingAgent {

    private Buffer buffer;
    private SimulationStats stats;

    private List<Client> allClients;

    private Client currentTargetClient = null;

    /**
     * Tworzy nowego kelnera na podanej pozycji z przypisanym buforem i listą klientów.
     *
     * @param x           współrzędna X początkowa
     * @param y           współrzędna Y początkowa
     * @param buffer      bufet (lada) do przekazywania zamówień
     * @param allClients  lista wszystkich klientów w restauracji
     * @param stats       obiekt statystyk symulacji
     */
    public Waiter(int x, int y, Buffer buffer, List<Client> allClients, SimulationStats stats) {
        super(x, y);
        this.buffer = buffer;
        this.allClients = allClients;
        this.stats = stats;
    }

    /**
     * Główna logika decyzyjna kelnera wywoływana w każdym ticku.
     * Priorytety:
     * 1. Dostarczenie gotowego dania do klienta.
     * 2. Zostawienie nowego zamówienia w buforze.
     * 3. Odebranie gotowego dania z bufetu.
     * 4. Znalezienie najbardziej niecierpliwego klienta do obsłużenia.
     */
    @Override
    public void scanBoard() {
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

        if (this.order == null && this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
            if (pickOrderFromBuffer()) {
                return;
            }
        }

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

    /**
     * Znajduje najbardziej niecierpliwego klienta, który chce złożyć zamówienie.
     */
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

    /**
     * Odbiera zamówienie od klienta.
     */
    public void pickUpOrder() {
        Order clientOrder = this.currentTargetClient.takeOrder();

        if (clientOrder != null) {
            this.order = clientOrder;
            this.isOccupied = true;
            System.out.println("Kelner odebrał zamówienie od stolika.");
        }
    }

    /**
     * Dostarcza gotowe danie do klienta i aktualizuje statystyki.
     */
    public void deliverOrder() {
        this.currentTargetClient.reciveMeal();
        if (stats != null) stats.onMealDelivered();
        System.out.println("Danie zostało podane!");

        this.order = null;
        this.isOccupied = false;
        this.currentTargetClient = null;
    }

    /**
     * Zostawia zamówienie w buforze dla kucharza i aktualizuje statystyki.
     */
    public void dropOrderAtBuffer() {
        this.order.setStatus(OrderStatus.W_BUFORZE);
        this.buffer.addOrder(this.order);
        if (stats != null) stats.onWaiterTrip();
        System.out.println("Kelner zostawił zamówienie na ladzie dla kucharza.");

        this.order = null;
        this.currentTargetClient = null;
    }

    /**
     * Pobiera gotowe danie z bufetu.
     *
     * @return true, jeśli udało się pobrać danie
     */
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

    /**
     * Wykonuje ruch w stronę celu (tarX, tarY) o jedną jednostkę na tick.
     */
    private void move() {
        if (this.x < this.tarX) this.x++;
        else if (this.x > this.tarX) this.x--;

        if (this.y < this.tarY) this.y++;
        else if (this.y > this.tarY) this.y--;
    }
}
