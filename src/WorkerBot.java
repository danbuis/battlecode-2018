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
	public void blueprintStructure(UnitType type, Direction direction){
		gc.blueprint(unitID, type, direction);
	}

	/**
	 * causes the worker to do the action specified in its orders.
	 */
	public void activate() {
		Order currentOrder = orderStack.peek();
		Direction dirToOrderLocation = gc.unit(unitID).location().mapLocation().directionTo(currentOrder.getLocation());
		
		if(currentOrder.getType()==OrderType.BUILD){
			buildStructure(currentOrder);		
		}else if(currentOrder.getType()==OrderType.BLUEPRINT_FACTORY){
			blueprintStructure(UnitType.Factory, dirToOrderLocation);
		}else if(currentOrder.getType()==OrderType.BLUEPRINT_ROCKET){
			blueprintStructure(UnitType.Rocket, dirToOrderLocation);
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
