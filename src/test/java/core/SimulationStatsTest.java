package core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationStatsTest {

    @Test
    void countersIncrementCorrectly() {
        SimulationStats stats = new SimulationStats(6, 2, 2);
        assertEquals(0, stats.getTotalClients());
        assertEquals(0, stats.getSatisfied());

        stats.onClientSpawned();
        stats.onClientSatisfied();
        stats.onOrderPlaced();
        stats.onMealDelivered();

        assertEquals(1, stats.getTotalClients());
        assertEquals(1, stats.getSatisfied());
        assertEquals(1, stats.getTotalOrdersPlaced());
        assertEquals(1, stats.getTotalMealsDelivered());
    }

    @Test
    void reportContainsConfigAndCounts() {
        SimulationStats stats = new SimulationStats(4, 1, 1);
        stats.setTotalTicks(50);
        stats.onClientSpawned();
        stats.onClientSatisfied();

        String report = stats.toReport();

        assertAll("Report content",
            () -> assertTrue(report.contains("4 stolik")),
            () -> assertTrue(report.contains("1 kucharz")),
            () -> assertTrue(report.contains("1 kelner")),
            () -> assertTrue(report.contains("50 tick"))
        );
    }

    @Test
    void updateMaxQueueOnlyIncreases() {
        SimulationStats stats = new SimulationStats(6, 2, 2);

        stats.updateMaxQueue(3);
        assertEquals(3, stats.getMaxQueueLength());

        stats.updateMaxQueue(1);
        assertEquals(3, stats.getMaxQueueLength());

        stats.updateMaxQueue(5);
        assertEquals(5, stats.getMaxQueueLength());
    }
}
