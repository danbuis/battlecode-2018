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
		if(debug){
			System.out.println("top of bluePrintStructure()");
			System.out.println("locationToBlueprint: "+locationToBlueprint);
			System.out.println("currentLocation: "+currentLocation);
			System.out.println("available resources: "+gc.karbonite());
		}
		//is location adjacent
		if(currentLocation.isAdjacentTo(locationToBlueprint)){
			if(debug)System.out.println("Locations are adjacent");
			Direction dir = currentLocation.directionTo(locationToBlueprint);
			if (debug) System.out.println("attempting to blueprint a "+type.name());
			if (debug) System.out.println("in Direction "+dir);
			if(gc.canBlueprint(unitID, type, dir)){
				gc.blueprint(unitID, type, dir);
				//successfully blueprinted!
				if(debug) System.out.println("Unit "+this.unitID+" placing a blueprint, order complete.");
				//pop blueprint and push a build
				this.orderStack.pop();
				this.orderStack.push(new Order(OrderType.BUILD, locationToBlueprint));
			}
		}
		
		
		
		
	}

	/**
	 * causes the worker to do the action specified in its orders.
	 */
	public void activate() {
		if(!orderStack.isEmpty()){
			Order currentOrder = orderStack.peek();
			if(debug) System.out.println("performing action with unit "+this.unitID+" "+currentOrder.toString() );
			
			OrderType type = currentOrder.getType();
			switch (type){
			case BUILD: 
				buildStructure(currentOrder);
				break;
			case BLUEPRINT_FACTORY:
				blueprintStructure(UnitType.Factory);
				break;
			case BLUEPRINT_ROCKET:
				blueprintStructure(UnitType.Rocket);
				break;
			default:
				//invalid order
				orderStack.pop();
			}
		}
		
	}
	
	private void buildStructure(Order currentOrder){
		
		Unit unitAtLocation = gc.senseUnitAtLocation(currentOrder.getLocation());
		if(debug){
			System.out.println("Unit "+unitID+" building a "+unitAtLocation.unitType().name()+" at "+unitAtLocation.location().mapLocation());
			System.out.println("Structure being built at "+unitAtLocation.health()+" health");
		}
		//makes sure there is something there, that it is on our team, and that it needs building
		if(unitAtLocation!=null && unitAtLocation.team().equals(gc.unit(unitID).team())
		   && unitAtLocation.structureIsBuilt()==0){
			
			if(gc.canBuild(unitID, unitAtLocation.id())){
				gc.build(unitID, unitAtLocation.id());
				if(debug) System.out.println("Unit "+this.unitID+" building a structure at "+currentOrder.getLocation());
			}
		}else{ //if it doesn't exist or is complete or is on the other team, so get rid of the order
			if(debug) System.out.println("Unit "+this.unitID+" build order complete");
			this.orderStack.pop();
		}
	}

}
