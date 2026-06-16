package agents;

import core.Simulation;
import environment.Table;
import models.Order;
import java.util.List;
import java.util.Random;

/**
 * Reprezentuje klienta w symulacji restauracji.
 * Klient wchodzi do restauracji, szuka wolnego stolika, zamawia jedzenie,
 * czeka na kelnera, a po otrzymaniu dania je, a następnie wychodzi.
 * Klient posiada cierpliwość - jeśli spadnie do zera, opuszcza restaurację.
 */
public class Client extends MovingAgent {
    private int patience;
    private int eatingTime;
    private boolean isEating;
    private Table assignedTable;
    private List<Table> allTables;
    private boolean hasLeft = false;
    private Simulation simulation;
    private Random random = new Random();

    private Waiter assignedWaiter = null;

    /**
     * Tworzy nowego klienta na podanej pozycji z określoną cierpliwością.
     *
     * @param x              współrzędna X początkowa
     * @param y              współrzędna Y początkowa
     * @param startPatience  początkowa cierpliwość klienta
     * @param allTables      lista wszystkich stolików w restauracji
     * @param simulation     referencja do głównego obiektu symulacji
     */
    public Client(int x, int y, int startPatience, List<Table> allTables, Simulation simulation) {
        super(x, y);
        this.isEating = false;
        this.patience = startPatience;
        this.eatingTime = 6 + random.nextInt(7);
        this.assignedTable = null;
        this.allTables = allTables;
        this.simulation = simulation;
    }

    /**
     * Główna logika decyzyjna klienta wywoływana w każdym ticku.
     * Klient najpierw szuka stolika, następnie podchodzi do niego,
     * składa zamówienie, czeka na kelnera, je i w końcu wychodzi.
     */
    @Override
    public void scanBoard() {
        if (assignedTable == null) {
            findAndClaimFreeTable();

            if (assignedTable == null) {
                patience--;
                if (patience <= 0) {
                    leaveRestaurant("Zbyt mało wolnych stolików, uciekam!");
                }
            }
        }
        else if (this.x != assignedTable.getX() || this.y != assignedTable.getY()) {
            this.tarX = assignedTable.getX();
            this.tarY = assignedTable.getY();
            move();
        }
        else if (!isEating) {
            if (this.order == null && this.assignedWaiter == null) {
                generateOrder();
            }

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

    /**
     * Znajduje wolny stolik i zajmuje go.
     */
    private void findAndClaimFreeTable() {
        for (Table table : allTables) {
            if (!table.getIsOccupied()) {
                table.setOccupied(true);
                takeTable(table);
                return;
            }
        }
    }

    /**
     * Zajmuje wskazany stolik i ustawia go jako cel podróży.
     *
     * @param table stolik do zajęcia
     */
    public void takeTable(Table table) {
        this.isOccupied = true;
        this.assignedTable = table;
        System.out.println("Klient zajął stolik i idzie w jego stronę...");
    }

    /**
     * Generuje nowe zamówienie i rejestruje je w statystykach.
     *
     * @return wygenerowane zamówienie
     */
    public Order generateOrder() {
        System.out.println("Klient usiadł i wymyślił zamówienie.");
        this.order = new Order(3 + random.nextInt(3), this);
        if (simulation != null) simulation.getStats().onOrderPlaced();
        return this.order;
    }

    /**
     * Oznacza klienta jako jedzącego po otrzymaniu dania.
     */
    public void reciveMeal() {
        this.isEating = true;
        this.assignedWaiter = null;
        System.out.println("Klient otrzymał danie i zaczyna jeść!");
    }

    /**
     * Obsługuje wyjście klienta z restauracji z podanego powodu.
     * Zwalnia stolik, aktualizuje statystyki i oznacza klienta jako tego, który wyszedł.
     *
     * @param reason powód opuszczenia restauracji
     */
    public void leaveRestaurant(String reason) {
        System.out.println("Klient wychodzi z restauracji: " + reason);
        if (simulation != null) simulation.onClientLeft(reason);
        if (assignedTable != null) {
            assignedTable.setOccupied(false);
        }

        this.assignedTable = null;
        this.order = null;
        this.isOccupied = false;
        this.assignedWaiter = null;
        this.hasLeft = true;
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

    /** Zwraca stolik przypisany do klienta. */
    public Table getAssignedTable() { return assignedTable; }
    /** Zwraca kelnera obsługującego tego klienta. */
    public Waiter getAssignedWaiter() { return this.assignedWaiter; }
    /** Ustawia kelnera obsługującego tego klienta. */
    public void setAssignedWaiter(Waiter waiter) { this.assignedWaiter = waiter; }

    /**
     * Przekazuje zamówienie kelnerowi (zwraca je i usuwa z klienta).
     *
     * @return zamówienie klienta
     */
    public Order takeOrder() {
        Order o = this.order;
        this.order = null;
        return o;
    }

    /**
     * Sprawdza, czy klient chce złożyć zamówienie (siedzi przy stoliku, nie je,
     * nie ma przypisanego kelnera i ma wygenerowane zamówienie).
     *
     * @return true, jeśli klient chce zamówić
     */
    public boolean wantsToOrder() {
        boolean isAtTable = (this.assignedTable != null && this.x == this.assignedTable.getX() && this.y == this.assignedTable.getY());
        return (!this.isEating && this.assignedWaiter == null && isAtTable && this.order != null);
    }

    /** Zwraca poziom cierpliwości klienta. */
    public int getPatience() { return this.patience; }
    /** Sprawdza, czy klient opuścił już restaurację. */
    public boolean hasLeft() {
        return this.hasLeft;
    }
}