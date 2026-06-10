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

    public Simulation() {
        this(6, 2, 2);
    }

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
        this.spawnMax = 8;
        this.random = new Random();
        this.nextClientTick = random.nextInt(spawnMax - spawnMin + 1) + spawnMin;
        this.tableCount = tableCount;
        this.cookCount = cookCount;
        this.waiterCount = waiterCount;
        this.stats = new SimulationStats(tableCount, cookCount, waiterCount);
    }

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

    public void run() {
        if (!isInitialized) init();
        isRunning = true;
        while (isRunning) {
            tick();
        }
    }

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

    public void onClientLeft(String reason) {
        if (reason.contains("Najedzony") || reason.contains("szczęśliwy")) {
            stats.onClientSatisfied();
        } else if (reason.contains("stolik")) {
            stats.onClientNoTable();
        } else if (reason.contains("podszedł") || reason.contains("cierpliwość")) {
            stats.onClientNoWaiter();
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void startSimulation() {
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public Board getBoard() {
        return board;
    }

    public int getTick() {
        return tick;
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Waiter> getWaiters() {
        return waiters;
    }

    public List<Cook> getCooks() {
        return cooks;
    }

    public List<Stove> getStoves() {
        return stoves;
    }

    public List<Table> getTables() {
        return tables;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public List<String> getLogMessages() {
        return logMessages;
    }

    public SimulationStats getStats() {
        return stats;
    }

    public void log(String message) {
        System.out.println(message);
        logMessages.add("[" + tick + "] " + message);
        if (logMessages.size() > 200) {
            logMessages.remove(0);
        }
    }

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
