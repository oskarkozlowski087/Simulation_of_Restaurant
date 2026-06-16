package agents;

import core.SimulationStats;
import models.Order;
import environment.Buffer;
import environment.Stove;
import models.OrderStatus;

/**
 * Reprezentuje kucharza w symulacji restauracji.
 * Kucharz pobiera zamówienia z bufetu, przygotowuje je na przypisanej kuchence,
 * a następnie odkłada gotowe dania z powrotem do bufetu.
 */
public class Cook extends MovingAgent {

    private Stove assignedStove;
    private Buffer buffer;
    private SimulationStats stats;
    private int cookingTimer = 0;

    /**
     * Tworzy nowego kucharza na podanej pozycji z przypisaną kuchenką i buforem.
     *
     * @param x             współrzędna X początkowa
     * @param y             współrzędna Y początkowa
     * @param assignedStove kuchenka przypisana do tego kucharza
     * @param buffer        bufet (lada) do pobierania i odkładania zamówień
     * @param stats         obiekt statystyk symulacji
     */
    public Cook(int x, int y, Stove assignedStove, Buffer buffer, SimulationStats stats) {
        super(x, y);
        this.assignedStove = assignedStove;
        this.buffer = buffer;
        this.stats = stats;
    }

    /**
     * Główna logika decyzyjna kucharza wywoływana w każdym ticku.
     * Priorytety:
     * 1. Puste ręce - idzie do bufetu po nowe zamówienie.
     * 2. Ma zamówienie - idzie do kuchenki i przygotowuje posiłek.
     * 3. Danie gotowe - odnosi je do bufetu.
     */
    @Override   
    public void scanBoard() {

        if (this.order == null) {
            if (this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
                takeNewOrder();
            } else {
                this.tarX = this.buffer.getX();
                this.tarY = this.buffer.getY();
                move();
            }
        }
        else if (this.order.getStatus() == OrderStatus.W_BUFORZE || this.order.getStatus() == OrderStatus.W_PRZYGOTOWANIU) {
            if (this.x == this.assignedStove.getX() && this.y == this.assignedStove.getY()) {
                prepareMeal();
            } else {
                this.tarX = this.assignedStove.getX();
                this.tarY = this.assignedStove.getY();
                move();
            }
        }
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

    /**
     * Pobiera nowe zamówienie z bufetu.
     */
    public void takeNewOrder() {
        Order presentOrder = this.buffer.takeOrder();

        if (presentOrder == null) {
            return;
        }

        this.order = presentOrder;
        System.out.println("Kucharz wziął zamówienie z buffera.");
        this.isOccupied = true;
    }

    /**
     * Przygotowuje posiłek na kuchence.
     * Jeśli kuchenka jest wolna, umieszcza na niej zamówienie.
     * Z każdym tickiem zwiększa licznik gotowania, a po osiągnięciu
     * wymaganego czasu zdejmuje gotowe danie z kuchenki.
     */
    public void prepareMeal() {
        if (!this.assignedStove.isOccupied()) {
            this.assignedStove.insertOrder(this.order);
        }

        this.cookingTimer++;
        int requiredTime = this.order.getTimeOrder();

        if (this.cookingTimer >= requiredTime) {
            this.order = this.assignedStove.takeOutOrder();
            this.order.setStatus(OrderStatus.GOTOWE);
            this.cookingTimer = 0;
            System.out.println("Posiłek gotowy! Zdejmuję z palnika.");
        } else {
            System.out.println("Posiłek w trakcie przygotowania... (" + this.cookingTimer + "/" + requiredTime + ")");
        }
    }

    /**
     * Odkłada gotowe danie do bufetu i aktualizuje statystyki.
     */
    public void dropMeal() {
        if (this.order != null && this.order.getStatus() == OrderStatus.GOTOWE) {
            this.buffer.addReadyMeal(this.order);
            if (stats != null) stats.onCookSession();
            System.out.println("Gotowy posiłek czeka na kelnera.");

            this.order = null;
            this.isOccupied = false;
        }
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