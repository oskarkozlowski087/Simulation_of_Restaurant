package agents;

import environment.Table;
import environment.Buffer;
import models.Order;
import models.OrderStatus;
import java.util.List;

public class Waiter extends MovingAgent {

    private Buffer buffer;

    //aby kelner mogl widziec wszystkich klientow
    private List<Client> allClients;

    //"kieszeń" na aktualny cel. ciagle sie zmienia
    private Client currentTargetClient = null;
    private boolean isWaitingForFood = false;

    // Konstruktor
    public Waiter(int x, int y, Buffer buffer, List<Client> allClients) {
        super(x, y);
        this.buffer = buffer;
        this.allClients = allClients;
    }

    @Override
    public void scanBoard() {

        // gdy kelner ma puste ręce nie czeka na jedzenie i nie ma wybranego celu
        if (this.order == null && !isWaitingForFood && this.currentTargetClient == null) {
            findMostImpatientClientToServe();
        }


        if (this.order == null) {

            // Idziemy po zamówienie do klienta
            if (!isWaitingForFood && this.currentTargetClient != null) {
                if (this.x == this.currentTargetClient.getX() && this.y == this.currentTargetClient.getY()) {
                    pickUpOrder();
                } else {
                    this.tarX = this.currentTargetClient.getX();
                    this.tarY = this.currentTargetClient.getY();
                    move();
                }
            }
            //czekamy przy buforze na jedzenie
            else if (isWaitingForFood) {
                if (this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
                    pickOrderFromBuffer();
                } else {
                    this.tarX = this.buffer.getX();
                    this.tarY = this.buffer.getY();
                    move();
                }
            }
        }
        else {
            if (this.order.getStatus() == OrderStatus.ZLOZONE) {
                if (this.x == this.buffer.getX() && this.y == this.buffer.getY()) {
                    dropOrderAtBuffer();
                } else {
                    this.tarX = this.buffer.getX();
                    this.tarY = this.buffer.getY();
                    move();
                }
            }
            //odnoszenie gotowego jedzenia do tego samego klienta
            else if (this.order.getStatus() == OrderStatus.GOTOWE) {
                if (this.x == this.currentTargetClient.getX() && this.y == this.currentTargetClient.getY()) {
                    deliverOrder();
                } else {
                    this.tarX = this.currentTargetClient.getX();
                    this.tarY = this.currentTargetClient.getY();
                    move();
                }
            }
        }
    }
    //szukanie najmniej cierpliwego klienta
    private void findMostImpatientClientToServe() {
        int lowestPatience = 999999; // Ustawiamy duza liczbę
        Client mostImpatientClient = null;

        // kelner patrzy na każdego klienta
        for (Client client : allClients) {

            //sprawdzamy czy klient chce zamówić i nie ma już kelnera
            if (client.wantsToOrder()) {

                //sprawdzamy
                if (client.getPatience() < lowestPatience) {
                    lowestPatience = client.getPatience();
                    mostImpatientClient = client;
                }
            }
        }

        //zapisujemy tego  klienta
        if (mostImpatientClient != null) {
            this.currentTargetClient = mostImpatientClient;
            mostImpatientClient.setAssignedWaiter(this);
        }
    }

    public void pickUpOrder() {
        Order clientOrder = this.currentTargetClient.takeOrder();

        if (clientOrder != null) {
            this.order = clientOrder;
            this.isOccupied = true;
            System.out.println("Kelner odebrał zamówienie od stolika.");
        }
    }

    public void deliverOrder() {
        this.currentTargetClient.reciveMeal();
        System.out.println("Danie zostało podane!");

        // Zwalniamy ręce kelnera
        this.order = null;
        this.isOccupied = false;

        //zwalniamy target kelnera ponieważ zrealizowal zamowienie.
        this.currentTargetClient = null;
    }

    public void dropOrderAtBuffer() {
        this.order.setStatus(OrderStatus.W_BUFORZE);
        this.buffer.addOrder(this.order);
        System.out.println("Kelner zostawił zamówienie na ladzie dla kucharza.");

        this.order = null; // kelner ma puste ręce
        this.isWaitingForFood = true; // przełącza się w tryb czekania na jedzenie
    }

    public void pickOrderFromBuffer() {
        Order readyMeal = this.buffer.takeReadyMeal();

        // kelner weźmie danie tylko jezeli coś tam leży.
        if (readyMeal != null) {
            this.order = readyMeal;
            this.isWaitingForFood = false; // koniec czekania
            System.out.println("Kelner odebrał gotowe danie z lady.");
        }
    }


    private void move() {
        if (this.x < this.tarX) this.x++;
        else if (this.x > this.tarX) this.x--;

        if (this.y < this.tarY) this.y++;
        else if (this.y > this.tarY) this.y--;
    }
}