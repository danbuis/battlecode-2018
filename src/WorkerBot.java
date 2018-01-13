import bc.*;


public class WorkerBot extends BasicBot {

	public WorkerBot(GameController gc, int unitID) {
		super(gc, unitID);
	}
	
	/**
	 * general method to blueprint a structure.  validation is handled by the worker
	 * manager and delegated to this worker bot based on criteria determined by the 
	 * manager.
	 * @param type
	 * @param direction
	 */
	public void blueprintStructure(UnitType type){
		
		MapLocation locationToBlueprint = this.orderStack.peek().getLocation();
		MapLocation currentLocation = gc.unit(unitID).location().mapLocation();
		
		//is location adjacent
		if(currentLocation.isAdjacentTo(locationToBlueprint)){
			Direction dir = currentLocation.directionTo(locationToBlueprint);
			if(gc.canBlueprint(unitID, type, dir)){
				gc.blueprint(unitID, type, dir);
				//successfully blueprinted!
				this.orderStack.pop();
			}
		}
		
		
		
		
	}

	/**
	 * causes the worker to do the action specified in its orders.
	 */
	public void activate() {
		Order currentOrder = orderStack.peek();
		
		if(currentOrder.getType()==OrderType.BUILD){
			buildStructure(currentOrder);		
		}else if(currentOrder.getType()==OrderType.BLUEPRINT_FACTORY){
			blueprintStructure(UnitType.Factory);
		}else if(currentOrder.getType()==OrderType.BLUEPRINT_ROCKET){
			blueprintStructure(UnitType.Rocket);
		}
		
	}
	
	private void buildStructure(Order currentOrder){
		Unit unitAtLocation = gc.senseUnitAtLocation(currentOrder.getLocation());
		//makes sure there is something there, that it is on our team, and that it needs building
		if(unitAtLocation!=null && unitAtLocation.team().equals(gc.unit(unitID).team())
		   && unitAtLocation.structureIsBuilt()==1){
			
			if(gc.canBuild(unitID, unitAtLocation.id())){
				gc.build(unitID, unitAtLocation.id());
			}
		}else{ //if it doesn't exist or is complete or is on the other team get rid of the order
			orderStack.pop();
		}
	}

}
