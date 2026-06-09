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

// klasa odpowiedzialna za tworzenie wszystkich agentow
public class AgentFactory {

    public static MovingAgent createAgent(String type, int startX, int startY) {
        int Patience = 15;
        if (type == null) return null;

        switch (type) {
            case "COOK":
                return new Cook(startX, startY, null, null);
            case "WAITER":
                return new Waiter(startX, startY, null, null);
            case "CLIENT":
                return new Client(startX, startY, Patience, new ArrayList<>());
            default:
                throw new IllegalArgumentException("Nieobsługiwany typ agenta");
        }
    }
}