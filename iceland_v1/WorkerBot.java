import bc.*;


public class WorkerBot extends BasicBot {
	
	private boolean debug=true;

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
				if(debug) System.out.println("Unit "+this.unitID+" placing a blueprint, order complete.");
				this.orderStack.pop();
			}
		}
		
		
		
		
	}

	/**
	 * causes the worker to do the action specified in its orders.
	 */
	public void activate() {
		if(debug) System.out.println("performing action with unit "+this.unitID);
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
				if(debug) System.out.println("Unit "+this.unitID+" building a structure at "+currentOrder.getLocation());
			}
		}else{ //if it doesn't exist or is complete or is on the other team get rid of the order
			if(debug) System.out.println("Unit "+this.unitID+" build order complete");
			orderStack.pop();
		}
	}

}
