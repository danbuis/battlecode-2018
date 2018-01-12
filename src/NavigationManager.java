import java.util.ArrayList;
import java.util.List;

import bc.*;
/**\
 * Data structure for handling navigation of individual robots.  has a target destination, a boolean as to whether or not they have
 * arrived, and methods to get them there.
 * @author dbuis
 *
 */
public class NavigationManager {
	private boolean debug = false;
	
	//need access to game controller for basically everything
	private GameController gc;
	
	//track what locations this robot has recently visited
	private List<MapLocation> pastLocations =new ArrayList<MapLocation>();
	
	//target MapLocation.  Parent gameController can set it whenever it needs to.
	//private MapLocation targetLocation=new MapLocation(Planet.Earth, 10,5);
	private MapLocation targetLocation=null;
	
	// does what it says on the box.  
	private boolean atTargetLocation;
	
	//how close do you need to be to the target location.  Reset to 2 upon arrival at a destination. This allows it to be at or adjacent to the target
	private int accuracy = 2;
	
	//general purpose constructor
	public NavigationManager(GameController gc, Unit unit){
		this.gc = gc;
		atTargetLocation=false; //initialize variable
	}
	
	/**
	 * public facing entry method for navigation
	 * @return 
	 */
	public Direction navigate(Unit unit){
		if (debug) System.out.println("Navigating with unit "+unit.id());
		MapLocation currentLoc = unit.location().mapLocation();
		if(debug){
			System.out.println("currently at "+currentLoc);
			System.out.println("at target:"+atTargetLocation+" and target is:"+targetLocation);
		}
		if(!atTargetLocation && targetLocation!=null){
			if (debug) System.out.println("callingNavToPoint()");
			return navToPoint(unit);
		}
		else return null;
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
	private Direction navToPoint(Unit unit){
		MapLocation currentLocation = unit.location().mapLocation();
		MapLocation locationToTest;
		long smallestDist=1000000000; //arbitrary large number
		long temp;
		Direction closestDirection=null;
		int unitID = unit.id();
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
							smallestDist=temp;
							closestDirection=dir;
						}
					}
				}
				i++;
			}//end of for-each loop
		}//end move ready if.
		
		//movement and maintenance
		if(closestDirection!=null){
			if (debug){
				System.out.println("found a closest direction, calling navInDirection()");
				System.out.println("heading "+closestDirection.name());
			}
			cleanUpAfterMove(unit);
		}else{
			//can't get any closer
			if (debug) System.out.println("can't get closer, erasing past locations");
			pastLocations.clear();
		}
		
		//have we arrived close enough?
		if(unit.location().mapLocation().distanceSquaredTo(targetLocation)<=accuracy){
			accuracy = 2;
			targetLocation = null;
			atTargetLocation=true;
			if (debug) System.out.println("Unit "+unit.id()+" arrived at destination.");
		}
		
		return closestDirection;
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


	public MapLocation getTargetLocation() {
		return targetLocation;
	}


	public void setTargetLocation(MapLocation targetLocation) {
		this.targetLocation = targetLocation;
		atTargetLocation = false;
	}

}
