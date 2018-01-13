import bc.GameController;

public class RangerBot extends BasicBot {
	
	GameController gc;
	public enum Command{Protect,Attack,Move,Wait};
	public Command currentTask;
	public int targetID=-1;
	public int targetX =-1;
	public int targetY =-1;
	

	public RangerBot(GameController gc, int unitID) {
		super(gc, unitID);
		// TODO Auto-generated constructor stub
		this.gc = gc;
	}
	
	public void activate() {
		switch(currentTask){
		case Attack:
			attack();
			break;
		case Move:
			move();
			break;
		case Protect:
			protect();
			break;
		}
	}
	
	public void attack() {
		
	}
	
	public void protect() {
		
	}
	
	public void move() {
		
	}
	
	
	

}
