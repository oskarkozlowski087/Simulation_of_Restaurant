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

    public Simulation() {
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
    }

    public void init() {
        log("--- OTWIERAMY RESTAURACJĘ ---");
        this.board = new Board(10, 12);

        this.tables.add(new Table(2, 6));
        this.tables.add(new Table(5, 6));
        this.tables.add(new Table(8, 6));

        for (Table table : tables) {
            board.getCell(table.getX(), table.getY()).setTable(table);
        }

        this.stoves.add(new Stove(3, 2));
        this.stoves.add(new Stove(6, 2));
        for (Stove stove : stoves) {
            board.getCell(stove.getX(), stove.getY()).setStove(true);
        }

        this.buffer = findBuffer();

        Cook cook = new Cook(4, 2, stoves.get(0), buffer);
        cooks.add(cook);
        board.registerAgent(cook, 4, 2);

        Waiter waiter = new Waiter(2, 8, buffer, clients);
        waiters.add(waiter);
        board.registerAgent(waiter, 2, 8);

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
            isRunning = false;
            return;
        }

        if (tick >= nextClientTick) {
            Table freeTable = findFreeTable();
            if (freeTable != null) {
                Client client = new Client(0, 0, 10);
                freeTable.setOccupied(true);
                client.takeTable(freeTable);
                client.generateOrder();
                clients.add(client);
                board.registerAgent(client, freeTable.getX(), freeTable.getY());
                log("Nowy klient przy stoliku (" + freeTable.getX() + "," + freeTable.getY() + ")");
            }
            nextClientTick = tick + random.nextInt(spawnMax - spawnMin + 1) + spawnMin;
        }

        for (Waiter w : waiters) w.scanBoard();
        for (Cook c : cooks) c.scanBoard();

        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client c = it.next();
            c.decrementTime();
            if (c.getAssignedTable() == null) {
                it.remove();
            }
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

    private Table findFreeTable() {
        for (Table table : tables) {
            if (!table.getIsOccupied()) {
                return table;
            }
        }
        return null;
    }
}
