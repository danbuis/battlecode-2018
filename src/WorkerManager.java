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
			bot.setTargetLocation(targetLocation);
		}		
	}


	@Override
	/**
	 * each turn all workers will move to their targets UNLESS they 
	 * have something else nearby to do, like build a nearby structure etc.
	 */
	public void eachTurnMoveAllUnits() {
		for(WorkerBot bot:workers){
			//first check if there is a structure nearby to help build
			for(Unit blueprint:blueprintList){
				long distanceToBlueprint=gc.unit(bot.unitID).location().mapLocation()
						.distanceSquaredTo(blueprint.location().mapLocation());
				if(distanceToBlueprint<=distanceToHelpBuild){//if close enough to come help
					
					if(debug) System.out.println("Close enough to help build");
					//we're adjacent!
					if(gc.canBuild(bot.unitID, blueprint.id())){
						gc.build(bot.unitID, blueprint.id());
					}else{
						//move toward blueprint
						MapLocation currentLocation = gc.unit(bot.unitID).location().mapLocation();
						MapLocation blueprintLocation = gc.unit(blueprint.id()).location().mapLocation();
						bot.moveInDirection(currentLocation.directionTo(blueprintLocation),gc.unit(bot.unitID));
						//try build again now that we're closer...
						if(gc.canBuild(bot.unitID, blueprint.id())){
							gc.build(bot.unitID, blueprint.id());
						}//end inside if can build
					}//end else
				}//end if distance less than
			}//end for each blueprint
			
			//checks if we have a targetLocation, and if the bot can actually move
			//since we might have moved closer to a nearby blueprint to be helpful.
			if(bot.getTargetLocation()!=null && gc.isMoveReady(bot.unitID))
				bot.navigate(gc.unit(bot.unitID));
		}
		
	}
	
	
	/**
	 * use this one when you don't care where a factory gets built.  Has an O(N^2)
	 * loop where it checks all workers and all directions until it is successful.
	 */
	public boolean issueOrderBuildFactory(){
		int unitID;
		if(debug) System.out.println("issueOrderBuildFactory()");
		//if we have enough karbonite and a worker on the field
		if(gc.karbonite()>=100 && workers.size()>0){
			if(debug) System.out.println("enough resources");
			for(WorkerBot worker:workers){
				unitID=worker.unitID;
				//try each direction
				for(Direction dir:Direction.values()){
					if(gc.canBlueprint(unitID, UnitType.Factory, dir)){
						gc.blueprint(unitID, UnitType.Factory, dir);
						return true;
					}
				}//end for each direction
			}//end for each worker
			
		}//end if enough resources
		return false;
	}

}
