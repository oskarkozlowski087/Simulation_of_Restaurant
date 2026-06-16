package core;

import agents.Cook;
import agents.Client;
import agents.Waiter;
import agents.MovingAgent;
import environment.Buffer;
import environment.CellType;
import environment.Stove;
import environment.Table;

import java.util.ArrayList;
import java.util.Random;

/**
 * Fabryka odpowiedzialna za tworzenie instancji agentów w symulacji.
 * Umożliwia tworzenie kucharzy, kelnerów i klientów na podstawie typu.
 */
public class AgentFactory {

    /**
     * Tworzy nowego agenta na podstawie podanego typu i współrzędnych startowych.
     *
     * @param type   typ agenta ("COOK", "WAITER", "CLIENT")
     * @param startX współrzędna X startowa
     * @param startY współrzędna Y startowa
     * @return nowo utworzony agent lub null, jeśli typ jest null
     * @throws IllegalArgumentException jeśli typ agenta nie jest obsługiwany
     */
    public static MovingAgent createAgent(String type, int startX, int startY) {
        int Patience = 10 + new Random().nextInt(8);
        if (type == null) return null;

        switch (type) {
            case "COOK":
                return new Cook(startX, startY, null, null, null);
            case "WAITER":
                return new Waiter(startX, startY, null, null, null);
            case "CLIENT":
                return new Client(startX, startY, Patience, new ArrayList<>(), null);
            default:
                throw new IllegalArgumentException("Nieobsługiwany typ agenta");
        }
    }
}