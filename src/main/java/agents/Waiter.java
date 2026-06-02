package agents;//import environment.Table
//import environment.Buffer
//import models.Order

public class Waiter extends MovingAgent {
    public Waiter(int x, int y){
        super(x,y);
    }
    public void pickUpOrder(){ //environment.Table table
        this.isOccupied = true;
        System.out.println("Kelner odebrał zamówienie od stolika");
    }
    public void dropOrderAtBuffer(){    //environment.Buffer buffer
        System.out.println("Kelner zostawił zamówienie na bufferze dla kucharza");
        this.isOccupied = false;
    }
    public void pickOrderFromBuffer(){   //environment.Buffer buffer
        System.out.println("Kelnerz odebrał zamówienie z buffera.");
        this.isOccupied = true;
    }
    public void deliverOrder(){ //environment.Table table
        System.out.println("Danie zostało podane!");
        this.isOccupied = false;
    }
}
