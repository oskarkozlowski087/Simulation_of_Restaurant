package models;

import agents.Client;

/**
 * Reprezentuje zamówienie złożone przez klienta w restauracji.
 * Każde zamówienie posiada unikalny identyfikator, czas przygotowania,
 * status oraz przypisanego klienta.
 */
public class Order {
    private int idOrder;
    private int timeOrder;
    private OrderStatus status;
    private Client client;

    private static int idCounter = 1;

    /**
     * Tworzy nowe zamówienie z określonym czasem przygotowania i przypisanym klientem.
     * Nadaje zamówieniu unikalny identyfikator oraz ustawia początkowy status na ZLOZONE.
     *
     * @param timeOrder czas potrzebny na przygotowanie zamówienia
     * @param client    klient składający zamówienie
     */
    public Order(int timeOrder, Client client){
        this.idOrder = idCounter;
        idCounter++;
        this.timeOrder = timeOrder;
        this.status = OrderStatus.ZLOZONE;
        this.client = client;
    }

    /**
     * Zwraca unikalny identyfikator zamówienia.
     *
     * @return identyfikator zamówienia
     */
    public int getIdOrder() {
        return idOrder;
    }

    /**
     * Zwraca czas potrzebny na przygotowanie zamówienia.
     *
     * @return czas przygotowania zamówienia
     */
    public int getTimeOrder() {
        return timeOrder;
    }

    /**
     * Zwraca aktualny status zamówienia.
     *
     * @return status zamówienia
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Ustawia nowy status zamówienia.
     *
     * @param status nowy status zamówienia
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Zwraca klienta, który złożył to zamówienie.
     *
     * @return klient powiązany z zamówieniem
     */
    public Client getClient() {
        return client;
    }
}
