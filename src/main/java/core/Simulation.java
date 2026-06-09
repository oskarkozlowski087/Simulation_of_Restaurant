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
        this.tables.add(new Table(2, 9));
        this.tables.add(new Table(5, 9));
        this.tables.add(new Table(8, 9));

        for (Table table : tables) {
            board.getCell(table.getX(), table.getY()).setTable(table);
        }

        this.stoves.add(new Stove(2, 2));
        this.stoves.add(new Stove(4, 2));
        this.stoves.add(new Stove(6, 2));
        for (Stove stove : stoves) {
            board.getCell(stove.getX(), stove.getY()).setStove(true);
        }

        this.buffer = findBuffer();

        Cook cook1 = new Cook(3, 2, stoves.get(0), buffer);
        cooks.add(cook1);
        board.registerAgent(cook1, 3, 2);
        Cook cook2 = new Cook(5, 2, stoves.get(1), buffer);
        cooks.add(cook2);
        board.registerAgent(cook2, 5, 2);

        Waiter waiter1 = new Waiter(2, 8, buffer, clients);
        waiters.add(waiter1);
        board.registerAgent(waiter1, 2, 8);
        Waiter waiter2 = new Waiter(8, 8, buffer, clients);
        waiters.add(waiter2);
        board.registerAgent(waiter2, 8, 8);

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
            Client client = new Client(5, 11, 10, this.tables);

            clients.add(client);
            board.registerAgent(client, 5, 11);
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

        //kucharze
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

        //klienci
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client c = it.next();

            int oldX = c.getX();
            int oldY = c.getY();

            c.scanBoard();

            int newX = c.getX();
            int newY = c.getY();

            // aktualizacja planszy
            if (oldX != newX || oldY != newY) {
                board.getCell(oldX, oldY).setOccupant(null);
                board.getCell(newX, newY).setOccupant(c);
            }

            // usuwanie klienta
            if (c.hasLeft()) {
                board.getCell(c.getX(), c.getY()).setOccupant(null);
                it.remove(); // wyrzucamy go z listy
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
