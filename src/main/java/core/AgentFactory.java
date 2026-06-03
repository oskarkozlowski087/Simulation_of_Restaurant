package core;

import agents.Cook;
import agents.MovingAgent;
import environment.CellType;

// klasa odpowiedzialna za tworzenie wszystkich agentow
public class AgentFactory {
    public static MovingAgent createAgent(String type, int startX, int startY) {
        return new Cook(0,0);
    }
}
