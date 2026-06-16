package core;

import agents.Client;
import agents.Cook;
import agents.Waiter;
import environment.Buffer;
import environment.Cell;
import environment.Stove;
import environment.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Główna klasa zarządzająca przebiegiem symulacji restauracji.
 * Odpowiada za inicjalizację świata, tworzenie agentów (klientów, kelnerów, kucharzy)
 * oraz wykonywanie kolejnych kroków symulacji (ticków).
 */
public class Simulation {

    private Buffer buffer;
    private Board board;
    private List<Table> tables;
    private List<Stove> stoves;

    private List<Waiter> waiters;
    private List<Cook> cooks;
    private List<Client> clients;

    private boolean isRunning;
    private boolean isInitialized;
    private int tick;
    private int nextClientTick;
    private int spawnMin;
    private int spawnMax;
    private Random random;
    private List<String> logMessages;

    private int tableCount;
    private int cookCount;
    private int waiterCount;
    private SimulationStats stats;

    /**
     * Tworzy symulację z domyślną konfiguracją: 6 stolików, 2 kucharzy, 2 kelnerów.
     */
    public Simulation() {
        this(6, 2, 2);
    }

    /**
     * Tworzy symulację z określoną konfiguracją restauracji.
     *
     * @param tableCount  liczba stolików
     * @param cookCount   liczba kucharzy
     * @param waiterCount liczba kelnerów
     */
    public Simulation(int tableCount, int cookCount, int waiterCount) {
        this.tables = new ArrayList<>();
        this.stoves = new ArrayList<>();
        this.waiters = new ArrayList<>();
        this.cooks = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.logMessages = new ArrayList<>();
        this.isRunning = false;
        this.isInitialized = false;
        this.tick = 0;
        this.spawnMin = 3;
        this.spawnMax = 10;
        this.random = new Random();
        this.nextClientTick = random.nextInt(spawnMax - spawnMin + 1) + spawnMin;
        this.tableCount = tableCount;
        this.cookCount = cookCount;
        this.waiterCount = waiterCount;
        this.stats = new SimulationStats(tableCount, cookCount, waiterCount);
    }

    /**
     * Inicjalizuje świat symulacji: tworzy planszę, stoliki, kuchnię, bufet,
     * kuchenki, a następnie rozmieszcza agentów (kucharzy i kelnerów).
     */
    public void init() {
        log("--- OTWIERAMY RESTAURACJĘ ---");
        this.board = new Board(10, 12);

        int[][] tablePos = {{2,6}, {5,6}, {8,6}, {2,9}, {5,9}, {8,9}};
        int limit = Math.min(tableCount, tablePos.length);
        for (int i = 0; i < limit; i++) {
            Table t = new Table(tablePos[i][0], tablePos[i][1]);
            tables.add(t);
            board.getCell(t.getX(), t.getY()).setTable(t);
        }

        int[][] stovePos = {{2,2}, {4,2}, {6,2}};
        int stoveLimit = Math.min(cookCount, 3);
        for (int i = 0; i < stoveLimit; i++) {
            Stove s = new Stove(stovePos[i][0], stovePos[i][1]);
            stoves.add(s);
            board.getCell(s.getX(), s.getY()).setStove(true);
        }

        this.buffer = findBuffer();

        int[][] cookPos = {{3,2}, {5,2}, {7,2}};
        int cookLimit = Math.min(cookCount, cookPos.length);
        for (int i = 0; i < cookLimit; i++) {
            Cook c = new Cook(cookPos[i][0], cookPos[i][1], stoves.get(i), buffer, stats);
            cooks.add(c);
            board.registerAgent(c, cookPos[i][0], cookPos[i][1]);
        }

        int[][] waiterPos = {{2,8}, {8,8}, {5,8}};
        int waiterLimit = Math.min(waiterCount, waiterPos.length);
        for (int i = 0; i < waiterLimit; i++) {
            Waiter w = new Waiter(waiterPos[i][0], waiterPos[i][1], buffer, clients, stats);
            waiters.add(w);
            board.registerAgent(w, waiterPos[i][0], waiterPos[i][1]);
        }

        this.isInitialized = true;
        log("Restauracja gotowa do otwarcia!");
    }

    /**
     * Uruchamia główną pętlę symulacji. Wykonuje ticki, dopóki symulacja jest aktywna.
     */
    public void run() {
        if (!isInitialized) init();
        isRunning = true;
        while (isRunning) {
            tick();
        }
    }

    /**
     * Wykonuje pojedynczy krok symulacji (tick).
     * Zwiększa licznik czasu, sprawdza warunek zakończenia, tworzy nowych klientów
     * oraz aktualizuje pozycje wszystkich agentów (kelnerów, kucharzy, klientów).
     */
    public void tick() {
        if (!isRunning || !isInitialized) return;

        tick++;

        if (tick >= 100) {
            log("Koniec zmiany! Zamykamy.");
            stats.setUnfinished(clients.size());
            stats.setTotalTicks(tick);
            if (buffer != null) stats.updateMaxQueue(buffer.getMaxPending());
            isRunning = false;
            return;
        }

        if (tick >= nextClientTick) {
            Client client = new Client(5, 11, 8 + random.nextInt(8), this.tables, this);

            clients.add(client);
            board.registerAgent(client, 5, 11);
            stats.onClientSpawned();
            log("Nowy klient wszedł do restauracji i rozgląda się za miejscem.");

            nextClientTick = tick + random.nextInt(spawnMax - spawnMin + 1) + spawnMin;
        }

        for (Waiter w : waiters) {
            int oldX = w.getX();
            int oldY = w.getY();

            w.scanBoard();

            int newX = w.getX();
            int newY = w.getY();

            if (oldX != newX || oldY != newY) {
                board.getCell(oldX, oldY).setOccupant(null);
                board.getCell(newX, newY).setOccupant(w);
            }
        }

        for (Cook c : cooks) {
            int oldX = c.getX();
            int oldY = c.getY();

            c.scanBoard();

            int newX = c.getX();
            int newY = c.getY();

            if (oldX != newX || oldY != newY) {
                board.getCell(oldX, oldY).setOccupant(null);
                board.getCell(newX, newY).setOccupant(c);
            }
        }

        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client c = it.next();

            int oldX = c.getX();
            int oldY = c.getY();

            c.scanBoard();

            int newX = c.getX();
            int newY = c.getY();

            if (oldX != newX || oldY != newY) {
                board.getCell(oldX, oldY).setOccupant(null);
                board.getCell(newX, newY).setOccupant(c);
            }

            if (c.hasLeft()) {
                board.getCell(c.getX(), c.getY()).setOccupant(null);
                it.remove();
            }
        }
    }

    /**
     * Rejestruje powód opuszczenia restauracji przez klienta i aktualizuje statystyki.
     *
     * @param reason powód opuszczenia restauracji (tekstowa przyczyna)
     */
    public void onClientLeft(String reason) {
        if (reason.contains("Najedzony") || reason.contains("szczęśliwy")) {
            stats.onClientSatisfied();
        } else if (reason.contains("stolik")) {
            stats.onClientNoTable();
        } else if (reason.contains("podszedł") || reason.contains("cierpliwość")) {
            stats.onClientNoWaiter();
        }
    }

    /** Zatrzymuje symulację. */
    public void stop() {
        isRunning = false;
    }

    /** Uruchamia symulację (ustawia flagę isRunning na true). */
    public void startSimulation() {
        isRunning = true;
    }

    /** Sprawdza, czy symulacja jest uruchomiona. */
    public boolean isRunning() {
        return isRunning;
    }

    /** Sprawdza, czy symulacja została zainicjalizowana. */
    public boolean isInitialized() {
        return isInitialized;
    }

    /** Zwraca obiekt planszy symulacji. */
    public Board getBoard() {
        return board;
    }

    /** Zwraca aktualny numer ticka symulacji. */
    public int getTick() {
        return tick;
    }

    /** Zwraca listę wszystkich klientów w restauracji. */
    public List<Client> getClients() {
        return clients;
    }

    /** Zwraca listę wszystkich kelnerów. */
    public List<Waiter> getWaiters() {
        return waiters;
    }

    /** Zwraca listę wszystkich kucharzy. */
    public List<Cook> getCooks() {
        return cooks;
    }

    /** Zwraca listę wszystkich kuchenek. */
    public List<Stove> getStoves() {
        return stoves;
    }

    /** Zwraca listę wszystkich stolików. */
    public List<Table> getTables() {
        return tables;
    }

    /** Zwraca obiekt bufora (lady) do przekazywania zamówień. */
    public Buffer getBuffer() {
        return buffer;
    }

    /** Zwraca listę komunikatów loga symulacji. */
    public List<String> getLogMessages() {
        return logMessages;
    }

    /** Zwraca obiekt statystyk symulacji. */
    public SimulationStats getStats() {
        return stats;
    }

    /**
     * Dodaje wiadomość do loga symulacji i wypisuje ją na konsolę.
     * Automatycznie poprzedza wiadomość aktualnym numerem ticka.
     * Przechowuje maksymalnie 200 ostatnich wiadomości.
     *
     * @param message treść wiadomości
     */
    public void log(String message) {
        System.out.println(message);
        logMessages.add("[" + tick + "] " + message);
        if (logMessages.size() > 200) {
            logMessages.remove(0);
        }
    }

    /**
     * Wyszukuje na planszy komórkę zawierającą bufet (Buffer).
     *
     * @return znaleziony obiekt Buffer lub null, jeśli nie istnieje
     */
    private Buffer findBuffer() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                Cell cell = board.getCell(x, y);
                if (cell.getBuffer() != null) {
                    return cell.getBuffer();
                }
            }
        }
        return null;
    }
}
