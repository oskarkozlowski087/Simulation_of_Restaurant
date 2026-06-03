package core;

import agents.Client;
import agents.Cook;
import agents.Waiter;
import environment.Buffer;
import environment.Stove;
import environment.Table;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    // 1. INWENTARZ RESTAURACJI

    private Buffer buffer;

    private List<Table> tables;
    private List<Stove> stoves;

    // Personel i goście
    private List<Waiter> waiters;
    private List<Cook> cooks;
    private List<Client> clients;

    private boolean isRunning; // Przełącznik, czy symulacja trwa

    public Simulation() {
        //odpalamy puste listy
        this.tables = new ArrayList<>();
        this.stoves = new ArrayList<>();
        this.waiters = new ArrayList<>();
        this.cooks = new ArrayList<>();
        this.clients = new ArrayList<>();

        // Budujemy jedną, wspolny butter (POGLĄDOWO)!!!!!!!!!!!! na środku sali
        //this.buffer = new Buffer(10, 10);

        // Rozstawiamy meble (na razie testowo, na sztywno kilka sztuk) POGLĄDOWO!!!!!!!!!!
        this.tables.add(new Table(2, 2));
        this.tables.add(new Table(2, 4));

        //this.stoves.add(new Stove(18, 2));
        //this.stoves.add(new Stove(18, 4));

        // Zaznaczamy, że restauracja jest gotowa do startu
        this.isRunning = true;
    }
    // 3. GŁÓWNY SILNIK GRY -zegar

    public void run() {
        System.out.println("--- OTWIERAMY RESTAURACJĘ ---");
        int tick = 0; // licznik czasu

        // Ta pętla kręci się tak długo aż isRunning = false
        while (isRunning) {

            // TUTAJ W PRZYSZŁOŚCI BĘDZIE SIĘ DZIAŁA MAGIA:
            // 1. Kucharze smażą (np. pętla po liście cooks)
            // 2. Kelnerzy chodzą (np. pętla po liście waiters)
            // 3. Klienci jedzą (np. pętla po liście clients)


            tick++; //wirtualny zegar (+1)

            //aby symulacja nie zawiesiła komputera
            // Zamknie się automatycznie po 100 obiegach.
            if (tick >= 100) {
                System.out.println("Koniec zmiany! Zamykamy.");
                this.isRunning = false;
            }
        }
    }
}
