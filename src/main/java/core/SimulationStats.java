package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gromadzi i udostępnia statystyki dotyczące przebiegu symulacji restauracji.
 * Zlicza m.in. liczbę klientów, zrealizowanych zamówień oraz wydajność pracowników.
 */
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

    /**
     * Tworzy obiekt statystyk z określoną konfiguracją restauracji.
     *
     * @param tables  liczba stolików
     * @param cooks   liczba kucharzy
     * @param waiters liczba kelnerów
     */
    public SimulationStats(int tables, int cooks, int waiters) {
        this.tableCount = tables;
        this.cookCount = cooks;
        this.waiterCount = waiters;
    }

    /** Zwiększa licznik całkowitej liczby klientów. */
    public synchronized void onClientSpawned() { totalClients++; }
    /** Zwiększa licznik zadowolonych klientów. */
    public synchronized void onClientSatisfied() { satisfied++; }
    /** Zwiększa licznik klientów, którzy odeszli z powodu braku stolika. */
    public synchronized void onClientNoTable() { noTable++; }
    /** Zwiększa licznik klientów, którzy odeszli z powodu braku kelnera. */
    public synchronized void onClientNoWaiter() { noWaiter++; }
    /** Zwiększa licznik złożonych zamówień. */
    public synchronized void onOrderPlaced() { totalOrdersPlaced++; }
    /** Zwiększa licznik dostarczonych dań. */
    public synchronized void onMealDelivered() { totalMealsDelivered++; }
    /** Zwiększa licznik wyjść kelnera. */
    public synchronized void onWaiterTrip() { totalWaiterTrips++; }
    /** Zwiększa licznik sesji kucharzy. */
    public synchronized void onCookSession() { totalCookSessions++; }
    /**
     * Aktualizuje maksymalną długość kolejki zamówień.
     *
     * @param size bieżąca długość kolejki
     */
    public synchronized void updateMaxQueue(int size) {
        if (size > maxQueueLength) maxQueueLength = size;
    }
    /**
     * Ustawia liczbę klientów, którzy nie zostali obsłużeni do końca.
     *
     * @param count liczba niedokończonych klientów
     */
    public synchronized void setUnfinished(int count) { this.unfinished = count; }
    /**
     * Ustawia całkowitą liczbę ticków symulacji.
     *
     * @param ticks liczba ticków
     */
    public synchronized void setTotalTicks(int ticks) { this.totalTicks = ticks; }

    /** Zwraca liczbę zadowolonych klientów. */
    public int getSatisfied() { return satisfied; }
    /** Zwraca liczbę klientów, którzy odeszli z powodu braku stolika. */
    public int getNoTable() { return noTable; }
    /** Zwraca liczbę klientów, którzy odeszli z powodu braku kelnera. */
    public int getNoWaiter() { return noWaiter; }
    /** Zwraca liczbę niedokończonych klientów. */
    public int getUnfinished() { return unfinished; }
    /** Zwraca całkowitą liczbę klientów. */
    public int getTotalClients() { return totalClients; }
    /** Zwraca liczbę złożonych zamówień. */
    public int getTotalOrdersPlaced() { return totalOrdersPlaced; }
    /** Zwraca liczbę dostarczonych dań. */
    public int getTotalMealsDelivered() { return totalMealsDelivered; }
    /** Zwraca liczbę wyjść kelnera. */
    public int getTotalWaiterTrips() { return totalWaiterTrips; }
    /** Zwraca liczbę sesji kucharzy. */
    public int getTotalCookSessions() { return totalCookSessions; }
    /** Zwraca maksymalną długość kolejki zamówień. */
    public int getMaxQueueLength() { return maxQueueLength; }

    /**
     * Generuje raport tekstowy z wynikami symulacji.
     * Zawiera statystyki klientów, wydajności oraz konfigurację restauracji.
     *
     * @return sformatowany raport w postaci tekstu
     */
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
