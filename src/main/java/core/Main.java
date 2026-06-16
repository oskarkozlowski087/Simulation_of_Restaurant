package core;

/**
 * Klasa uruchomieniowa symulatora restauracji.
 * Tworzy obiekt symulacji, inicjalizuje go i uruchamia główną pętlę.
 */
public class Main {
    /**
     * Główna metoda programu. Tworzy symulację z domyślną konfiguracją,
     * inicjalizuje świat i uruchamia symulację.
     *
     * @param args argumenty wiersza poleceń (nieużywane)
     */
    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.init();
        simulation.startSimulation();
        simulation.run();
    }
}
