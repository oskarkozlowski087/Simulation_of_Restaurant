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
    private int tick;
    private int nextClientTick;
    private int spawnMin;
    private int spawnMax;
    private Random random;

    public Simulation() {
        this.tables = new ArrayList<>();
        this.stoves = new ArrayList<>();
        this.waiters = new ArrayList<>();
        this.cooks = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.isRunning = true;
        this.tick = 0;
        this.spawnMin = 3;
        this.spawnMax = 8;
        this.random = new Random();
        this.nextClientTick = random.nextInt(spawnMax - spawnMin + 1) + spawnMin;
    }

    public void run() {
        System.out.println("--- OTWIERAMY RESTAURACJĘ ---");
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

        while (isRunning) {
            tick++;

            if (tick >= 100) {
                System.out.println("Koniec zmiany! Zamykamy.");
                isRunning = false;
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
