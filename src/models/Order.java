package models;

public class Order {
    private int idOrder;
    private int timeOrder;
    private OrderStatus status;

    private static int idCounter = 1;

    public Order(int timeOrder){
        this.idOrder = idCounter;
        idCounter++;
        this.timeOrder = timeOrder;
        this.status = OrderStatus.ZLOZONE;
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
}
