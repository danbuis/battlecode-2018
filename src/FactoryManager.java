import java.util.List;
import java.util.Stack;

import bc.*;

public class FactoryManager {
	
	GameController gc;
	List<WorkerBot> workerList;
	List<RangerBot>rangerList;
	List<MageBot> mageList;
	List<HealerBot>healerList;
	List<KnightBot>knightList;
	List<FactoryBot>factoryList;
	
	Stack<BasicBot> buildRequests = new Stack<BasicBot>();

	public FactoryManager(GameController gc, List<WorkerBot> workerList, List<RangerBot> rangerList,
			List<MageBot> mageList, List<HealerBot> healerList, List<KnightBot> knightList,
			List<FactoryBot> factoryList) {
		this.gc=gc;
		this.rangerList=rangerList;
		this.mageList=mageList;
		this.healerList=healerList;
		this.knightList=knightList;
		this.factoryList=factoryList;
		this.workerList=workerList;
	}

	public void eachTurnBuildUnits() {

		if(!factoryList.isEmpty()){

			for(FactoryBot factory : factoryList){
				System.out.println("Factory "+factory.unitID+" has "+gc.unit(factory.unitID).structureGarrison().size()+" units in its garrison");
				if(gc.canProduceRobot(factory.unitID, UnitType.Ranger)){
					System.out.println("Producing unit at factory");
					factory.produceUnit(UnitType.Ranger);
				}
				
				//if we have a unit in garrison, get it out...
				if(gc.unit(factory.unitID).structureGarrison().size()>0){
					System.out.println("inside structure garrison check, location "+gc.unit(factory.unitID).location().mapLocation());
					int unit = factory.unitID;
					for(Direction dir:Direction.values()){
						System.out.println("checking "+dir+" :"+gc.canUnload(unit, dir));
						if(gc.canUnload(unit, dir)){
							System.out.println("moving unit from Factory garrison");
							gc.unload(unit, dir);
						}
					}//end for each direction
				}//end is garrison larger than 0
			}//end for each factory
		}//end is factory list empty
		
	}

}
