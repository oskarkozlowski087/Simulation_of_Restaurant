package models;

import agents.Client;

public class Order {
    private int idOrder;
    private int timeOrder;
    private OrderStatus status;
    private Client client;

    private static int idCounter = 1;

    public Order(int timeOrder, Client client){
        this.idOrder = idCounter;
        idCounter++;
        this.timeOrder = timeOrder;
        this.status = OrderStatus.ZLOZONE;
        this.client = client;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public int getTimeOrder() {
        return timeOrder;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }
}
