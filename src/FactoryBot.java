import bc.GameController;
import bc.UnitType;

public class FactoryBot extends BasicBot{

	public FactoryBot(GameController gc, int unitID) {
		super(gc, unitID);
		// TODO Auto-generated constructor stub
	}

	public void produceUnit(UnitType robot_type) {
		gc.produceRobot(unitID, robot_type);
		
		
	}

}
