package agents;

import models.Order;

public abstract class MovingAgent {
    protected int x;
    protected int y;
    protected int tarX;
    protected int tarY;
    protected boolean isOccupied;

    protected Order order;

    public MovingAgent(int x, int y){
        this.x = x;
        this.y = y;
        this.isOccupied = false;
    }

    public boolean isOccupied(){
        return isOccupied;
    }

    public void scanBoard(){

    }
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getTarX() {
        return tarX;
    }

    public int getTarY() {
        return tarY;
    }


}
