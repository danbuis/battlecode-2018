import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import bc.*;
/**\
 * Data structure for handling navigation of individual robots.  has a target destination, a boolean as to whether or not they have
 * arrived, and methods to get them there.
 * @author dbuis
 *
 */
public class BasicBot {
	private boolean debug = false;
	
	//need access to game controller for basically everything
	public GameController gc;
		
	//unit associated with this
	public int unitID;
	public Unit thisUnit = gc.unit(unitID);
	

	//track what locations this robot has recently visited
	private List<MapLocation> pastLocations =new ArrayList<MapLocation>();
	
	//target MapLocation.  Parent gameController can set it whenever it needs to.
	//private MapLocation targetLocation=new MapLocation(Planet.Earth, 10,5);
	//private MapLocation targetLocation=null;
	
	public Stack<Order> orderStack;
	
	// does what it says on the box.  
	public boolean atTargetLocation;
	
	//how close do you need to be to the target location.  Reset to 2 upon arrival at a destination. This allows it to be at or adjacent to the target
	private int howCloseToDestination = 1;
	
	//general purpose constructor
	public BasicBot(GameController gc, int unitID){
		this.unitID= unitID;
		this.gc = gc;
		atTargetLocation=false; //initialize variable
	}
	
	/**
	 * public facing entry method for navigation
	 * @return 
	 */
	public void navigate(Unit unit){
		if (debug) System.out.println("Navigating with unit "+unit.id());
		//grab current location
		MapLocation currentLoc = unit.location().mapLocation();
		if(debug){
			System.out.println("currently at "+currentLoc);
			System.out.println("at target:"+atTargetLocation+" and target is:"+orderStack.peek().getLocation());
		}
		//if we have a target and are not there yet, nav to that point
		if(!atTargetLocation && orderStack.peek().getLocation()!=null){
			if (debug) System.out.println("callingNavToPoint()");
			navToPoint(unit);
		}
	}
	
	
	/**
	 * core method of navigating to a point.  Iterates through all available directions to find the one closest to the target point.
	 * Checks first to see if the unit can in fact move there, followed by checking to see if its been there recently.  Chooses the best option
	 * available.
	 * 
	 * If a direction is found, it moves, and does some upkeep to past positions, and checks to see if it is at its destination.
	 * 
	 * @param accuracy - how close do you need to get? Uses distance squared 0 means spot on, 2 is within one tile (inc diagonals) 4 is within 2 tiles etc.
	 * @return 
	 */
	private void navToPoint(Unit unit){
		MapLocation currentLocation = unit.location().mapLocation();
		MapLocation locationToTest;
		MapLocation targetLocation = orderStack.peek().getLocation();
		long smallestDist=1000000000; //arbitrary large number
		long temp;
		Direction closestDirection=null;
		int i=1;
		
		if(gc.isMoveReady(unitID)){
			//first find the direction that moves us closer to the target
			for(Direction dir : Direction.values()){
				if (debug) System.out.println("this ("+dir.name()+") is the "+i+"th direction to check, should get to 9");
				locationToTest=currentLocation.add(dir);
				//make sure it is on the map and is passable
				if (debug){
					System.out.println("testing this location: "+locationToTest);
					System.out.println("valid move? "+gc.canMove(unitID, dir));
				}
				if(gc.canMove(unitID, dir)){
					if (debug)System.out.println("we can indeed move there...");
					//make sure the location hasn't already been visited
					if(!pastLocations.contains(locationToTest)){
						if (debug)System.out.println("not been there recently...");
						//at this point its a valid location to test, check its distance
						temp = locationToTest.distanceSquaredTo(targetLocation);
						if (debug)System.out.println("distance :"+temp);
						if (temp<smallestDist){
							if (debug)System.out.println("new closest!");
							//new closest point, update accordingly
							smallestDist=temp;
							closestDirection=dir;
						}
					}
				}
				i++;
			}//end of for-each loop
		}//end move ready if.
		
		//actual movement and maintenance of places recently visited
		if(closestDirection!=null){
			if (debug){
				System.out.println("found a closest direction, calling navInDirection()");
				System.out.println("heading "+closestDirection.name());
			}
			moveInDirection(closestDirection, unit);
			cleanUpAfterMove(unit);
		}else{
			//can't get any closer
			if (debug) System.out.println("can't get closer, erasing past locations");
			pastLocations.clear();
		}
		
		//have we arrived close enough?
		if(unit.location().mapLocation().distanceSquaredTo(targetLocation)<=howCloseToDestination){
			atTargetLocation=true;
			
			//if order was a MOVE order, it is complete, go ahead and pop it off the stack
			if(orderStack.peek().getType().equals(OrderType.MOVE)){
				orderStack.pop();
			}
			if (debug) System.out.println("Unit "+unit.id()+" arrived at destination.");
		}
		
		;
	}
	
	/**
	 * Code to maintain the pastLocations list
	 * @param prechecked - was the direction verified before calling this method?
	 */
	private void cleanUpAfterMove(Unit unit){
			//add current location
			pastLocations.add(unit.location().mapLocation());
			//get rid of oldest to maintain recent locations
			if (pastLocations.size()>9)
				pastLocations.remove(0);	
	}
	
	/**
	 * actually moves the unit in the right direction as determined earlier
	 * @param dir
	 * @param unit
	 */
	public void moveInDirection(Direction dir, Unit unit){
		   if(dir!=null){
			   if(debug){
				   System.out.println("about to move unit "+unitID+" "+dir.name());
				   System.out.println("currentLocation "+unit.location().mapLocation());
			   }
               if(dir.equals(Direction.Southwest)){
               	if (gc.canMove(unitID, Direction.Southwest)) {
                       gc.moveRobot(unitID, Direction.Southwest);
                       System.out.println("moving south west");
               	}
               }else if(dir.equals(Direction.Southeast)){
               	if (gc.canMove(unitID, Direction.Southeast)) {
                       gc.moveRobot(unitID, Direction.Southeast);
                       System.out.println("moving south east");
               	}
               }else if(dir.equals(Direction.South)){
               	if (gc.canMove(unitID, Direction.South)) {
                       gc.moveRobot(unitID, Direction.South);
                       System.out.println("moving south");
               	}
               }
               else if(dir.equals(Direction.East)){
               	if (gc.canMove(unitID, Direction.East)) {
                       gc.moveRobot(unitID, Direction.East);
                       System.out.println("moving east");
               	}
               }
               else if(dir.equals(Direction.West)){
               	if (gc.canMove(unitID, Direction.West)) {
                       gc.moveRobot(unitID, Direction.West);
                       System.out.println("moving west");
               	}
               }else if(dir.equals(Direction.Northeast)){
               	if (gc.canMove(unitID, Direction.Northeast)) {
                       gc.moveRobot(unitID, Direction.Northeast);
                       System.out.println("moving north east");
               	}
               }else if(dir.equals(Direction.Northwest)){
               	if (gc.canMove(unitID, Direction.Northwest)) {
                       gc.moveRobot(unitID, Direction.Northwest);
                       System.out.println("moving north west");
               	}
               }else if(dir.equals(Direction.North)){
               	if (gc.canMove(unitID, Direction.North)) {
                       gc.moveRobot(unitID, Direction.North);
                       System.out.println("moving north");
               	}
               }
           }
		
	}




}
