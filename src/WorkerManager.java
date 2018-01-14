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
	public void issueOrderMoveSpecificUnit(MapLocation targetLocation, BasicBot unit) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void issueOrderMoveAllUnits(MapLocation targetLocation) {
		for(WorkerBot bot: workers){
			if (debug) System.out.println("pushing new move order for "+bot.unitID);
			bot.orderStack.push(new Order(OrderType.MOVE, targetLocation));
		}		
	}


	@Override
	/**
	 * each turn all workers will move to their targets UNLESS they 
	 * have something else nearby to do, like build a nearby structure etc.
	 */
	public void eachTurnMoveAllUnits() {
		if (debug){
			System.out.println("top of eachTurnMoveAllUnits()");
			System.out.println("moving "+workers.size()+" units");
			System.out.println("number of blueprints on the field: "+blueprintList.size());
		}
		for(WorkerBot worker:workers){
			if (debug) System.out.println("checking worker "+worker.unitID);
			//first check if there is a structure nearby to help build
			for(Unit blueprint:blueprintList){
<<<<<<< HEAD
				if (debug) System.out.println("top of blueprintList loop");
=======
>>>>>>> parent of 7f123fe... Workers now assist in building nearby structures
				long distanceToBlueprint=gc.unit(worker.unitID).location().mapLocation()
						.distanceSquaredTo(blueprint.location().mapLocation());
				if (debug) System.out.println("that blueprint is "+distanceToBlueprint+" away");
				if((worker.orderStack.isEmpty() || worker.orderStack.peek().getType()!=OrderType.BUILD) && distanceToBlueprint<=distanceToHelpBuild){
					//if close enough to come help issue a build order
					if(debug) System.out.println("pushing a build order to unit "+worker.unitID);
					worker.orderStack.push(new Order(OrderType.BUILD, blueprint.location().mapLocation()));
				}//end if distance less than
			}//end for each blueprint
			
			//checks if we have a targetLocation that we haven't arrived at,
			//and if the bot can actually move since we might have
			//moved closer to a nearby blueprint to be helpful.
			if(debug){
				System.out.println(worker==null);
				System.out.println(worker.orderStack.size()+" orders");
			}
			if(worker.orderStack.size()!=0 && worker.orderStack.peek().getLocation()!=null && gc.isMoveReady(worker.unitID)){
				worker.navigate(gc.unit(worker.unitID));
				
				worker.activate();
				}
		}//end for each worker
	}
	
	
	/**
	 * use this one when you don't care where a structure gets built.  Has an O(N^2)
	 * loop where it checks all workers and all directions until it is successful.
	 */
	public void issueOrderBlueprintStructure(UnitType type){
		int unitID; //id of the worker 
		if(debug) System.out.println("issueOrderBlueprintStructure() "+type.name());
		//if we have a worker on the field
		if(workers.size()>0){
			for(WorkerBot worker:workers){
				unitID=worker.unitID;
				
				//try each direction
				for(Direction dir:Direction.values()){
					if(gc.canBlueprint(unitID, type, dir)){
						if (debug) System.out.println("pushing a blueprint order to unit "+unitID+" type: "+type.name());
						if (type.equals(UnitType.Rocket)){
							worker.orderStack.push(new Order(OrderType.BLUEPRINT_ROCKET, gc.unit(unitID).location().mapLocation().add(dir)));
							return;
						}else{
							worker.orderStack.push(new Order(OrderType.BLUEPRINT_FACTORY, gc.unit(unitID).location().mapLocation().add(dir)));
							return;
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
		WorkerBot closestWorker=null;
		long distance = 1000000000; //arbitrary large number
		if(workers.size()!=0){ //what if we don't have any workers?  throws exception down in the final if/else.
			//find closest
			for(WorkerBot worker: workers){
				long distanceToBlueprintTarget = gc.unit(worker.unitID).location().mapLocation().distanceSquaredTo(location);
				
				if (debug) System.out.println("Worker "+worker.unitID+" is "+distanceToBlueprintTarget+" units away");
				
				if(distanceToBlueprintTarget <= distance){
					if (debug) System.out.println("new closest worker");
					closestWorker = worker;
					if (debug) System.out.println("closestWorker null? "+closestWorker==null);
					distance = distanceToBlueprintTarget;
				}
			}
			if(debug){
				System.out.println("is closestWorker null? "+closestWorker==null);
				System.out.println("closest worker "+closestWorker);
				System.out.println(closestWorker.orderStack);
			}
			if (type.equals(UnitType.Rocket)){
				closestWorker.orderStack.push(new Order(OrderType.BLUEPRINT_ROCKET, location ));
			}else{
				closestWorker.orderStack.push(new Order(OrderType.BLUEPRINT_FACTORY, location ));
			}
		}
		
		
	}

}
