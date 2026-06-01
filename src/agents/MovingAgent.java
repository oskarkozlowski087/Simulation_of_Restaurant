package agents;

public abstract class MovingAgent {
    protected int x;
    protected int y;
    protected int tarX;
    protected int tarY;
    protected boolean isOccupied;

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


}
