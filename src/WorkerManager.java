import java.util.ArrayList;
import java.util.List;

import bc.*;

public class WorkerManager implements UnitManagersInterface{
	
	public GameController gc;
	public List<WorkerBot> workers;
	private List<Unit> blueprintList = new ArrayList<Unit>();
	private int distanceToHelpBuild=9;
	
	private boolean debug=true;

	public WorkerManager(GameController gc, List<WorkerBot> workerList, List<Unit> blueprintList) {
		this.gc=gc;
		this.workers = workerList;
	}


	@Override
	public void issueOrderMoveAnyUnit(MapLocation targetLocation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void issueOrderMoveSpecificUnit(MapLocation targetLocation, Unit unit) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void issueOrderMoveAllUnits(MapLocation targetLocation) {
		for(WorkerBot bot: workers){
			bot.orderStack.add(new Order(OrderType.MOVE, targetLocation));
		}		
	}


	@Override
	/**
	 * each turn all workers will move to their targets UNLESS they 
	 * have something else nearby to do, like build a nearby structure etc.
	 */
	public void eachTurnMoveAllUnits() {
		for(WorkerBot worker:workers){
			//first check if there is a structure nearby to help build
			for(Unit blueprint:blueprintList){
				long distanceToBlueprint=gc.unit(worker.unitID).location().mapLocation()
						.distanceSquaredTo(blueprint.location().mapLocation());
				
				if(distanceToBlueprint<=distanceToHelpBuild){
					//if close enough to come help issue a build order
					worker.orderStack.push(new Order(OrderType.BUILD, blueprint.location().mapLocation()));
				}//end if distance less than
			}//end for each blueprint
			
			//checks if we have a targetLocation that we haven't arrived at,
			//and if the bot can actually move since we might have
			//moved closer to a nearby blueprint to be helpful.
			if(worker.orderStack.peek().getLocation()!=null && !worker.atTargetLocation && gc.isMoveReady(worker.unitID))
				worker.navigate(gc.unit(worker.unitID));
				worker.activate();
		}//end for each worker
		
	}
	
	
	/**
	 * use this one when you don't care where a structure gets built.  Has an O(N^2)
	 * loop where it checks all workers and all directions until it is successful.
	 */
	public void issueOrderBlueprintStructure(UnitType type){
		int unitID; //id of the worker 
		if(debug) System.out.println("issueOrderBuildStructure()");
		//if we have a worker on the field
		if(workers.size()>0){
			for(WorkerBot worker:workers){
				unitID=worker.unitID;
				//try each direction
				for(Direction dir:Direction.values()){
					if(gc.canBlueprint(unitID, type, dir)){
						if (type.equals(UnitType.Rocket)){
							worker.orderStack.push(new Order(OrderType.BLUEPRINT_ROCKET, gc.unit(unitID).location().mapLocation().add(dir)));
						}else{
							worker.orderStack.push(new Order(OrderType.BLUEPRINT_FACTORY, gc.unit(unitID).location().mapLocation().add(dir)));

						}
					}
				}//end for each direction
			}//end for each worker		
		}//end if enough resources
	}//end method
	
	
	/**
	 * use this one to build a structure at a specific location
	 * @param type
	 * @param location
	 */
	public void issueOrderBlueprintStructureAtLocation(UnitType type, MapLocation location){
		
	}

}
