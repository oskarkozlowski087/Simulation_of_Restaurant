package core;

import agents.Cook;
import agents.Client;
import agents.Waiter;
import agents.MovingAgent;
import environment.CellType;

// klasa odpowiedzialna za tworzenie wszystkich agentow
public class AgentFactory {

    public static MovingAgent createAgent(String type, int startX, int startY) {
        int Patience = 15;
        if (type == null) return null;

        switch (type) {
            case "COOK":
                return new Cook(startX, startY); // W przyszłości można dodać ID, żeby ich rozróżniać
            case "WAITER":
                return new Waiter(startX, startY); //
            case "CLIENT":
                return new Client(startX, startY, Patience);
            default:
                throw new IllegalArgumentException("Nieobsługiwany typ agenta");
        }
    }
}