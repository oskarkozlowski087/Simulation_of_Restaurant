package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimulationStats {
    private int totalClients;
    private int satisfied;
    private int noTable;
    private int noWaiter;
    private int unfinished;

    private int totalOrdersPlaced;
    private int totalMealsDelivered;
    private int totalWaiterTrips;
    private int totalCookSessions;
    private int maxQueueLength;

    private int tableCount;
    private int cookCount;
    private int waiterCount;
    private int totalTicks;

    public SimulationStats(int tables, int cooks, int waiters) {
        this.tableCount = tables;
        this.cookCount = cooks;
        this.waiterCount = waiters;
    }

    public synchronized void onClientSpawned() { totalClients++; }
    public synchronized void onClientSatisfied() { satisfied++; }
    public synchronized void onClientNoTable() { noTable++; }
    public synchronized void onClientNoWaiter() { noWaiter++; }
    public synchronized void onOrderPlaced() { totalOrdersPlaced++; }
    public synchronized void onMealDelivered() { totalMealsDelivered++; }
    public synchronized void onWaiterTrip() { totalWaiterTrips++; }
    public synchronized void onCookSession() { totalCookSessions++; }
    public synchronized void updateMaxQueue(int size) {
        if (size > maxQueueLength) maxQueueLength = size;
    }
    public synchronized void setUnfinished(int count) { this.unfinished = count; }
    public synchronized void setTotalTicks(int ticks) { this.totalTicks = ticks; }

    public int getSatisfied() { return satisfied; }
    public int getNoTable() { return noTable; }
    public int getNoWaiter() { return noWaiter; }
    public int getUnfinished() { return unfinished; }
    public int getTotalClients() { return totalClients; }
    public int getTotalOrdersPlaced() { return totalOrdersPlaced; }
    public int getTotalMealsDelivered() { return totalMealsDelivered; }
    public int getTotalWaiterTrips() { return totalWaiterTrips; }
    public int getTotalCookSessions() { return totalCookSessions; }
    public int getMaxQueueLength() { return maxQueueLength; }

    public String toReport() {
        int served = satisfied + noTable + noWaiter;
        double satPct = totalClients > 0 ? (satisfied * 100.0 / totalClients) : 0;
        double noTablePct = totalClients > 0 ? (noTable * 100.0 / totalClients) : 0;
        double noWaiterPct = totalClients > 0 ? (noWaiter * 100.0 / totalClients) : 0;

        return """
            === RAPORT SYMULACJI RESTAURACJI ===
            Data: %s
            Konfiguracja: %d stolików, %d kucharzy, %d kelnerów
            Czas trwania: %d ticków

            --- Klienci ---
            Zadowoleni:      %d  (%4.1f%%)
            Brak stolika:    %d  (%4.1f%%)
            Brak kelnera:    %d  (%4.1f%%)
            Niedokończeni:   %d
            Razem:           %d

            --- Wydajność ---
            Zamówienia złożone:    %d
            Dania wydane:          %d
            Wyjścia kelnera:       %d
            Sesje kucharzy:        %d
            Max długość kolejki:   %d
            """
            .formatted(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                tableCount, cookCount, waiterCount,
                totalTicks,
                satisfied, satPct, noTable, noTablePct, noWaiter, noWaiterPct,
                unfinished, totalClients,
                totalOrdersPlaced, totalMealsDelivered, totalWaiterTrips, totalCookSessions, maxQueueLength
            );
    }
}
