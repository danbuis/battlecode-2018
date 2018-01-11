package Utility;

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
	//need access to game controller for basically everything
	private GameController gc;
	
	//need access to the unit that goes with this structure
	private Unit unit;
	
	//track what locations this robot has recently visited
	private List<MapLocation> pastLocations =new ArrayList<MapLocation>();
	
	//target MapLocation.  Parent gameController can set it whenever it needs to.
	private MapLocation targetLocation=null;
	
	// does what it says on the box.  
	private boolean atTargetLocation;
	
	//how close do you need to be to the target location.  Reset to 2 upon arrival at a destination. This allows it to be at or adjacent to the target
	private int accuracy = 2;
	
	//general purpose constructor
	public NavigationManager(GameController gc, Unit unit){
		this.gc = gc;
		this.unit=unit;
		atTargetLocation=false; //initialize variable
	}
	
	/**
	 * public facing entry method for navigation
	 */
	public void navigate(){
		System.out.println("Navigating with unit "+unit.id());
		if(!atTargetLocation && targetLocation!=null){
			navToPoint();
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
	 */
	private void navToPoint(){
		MapLocation currentLocation = unit.location().mapLocation();
		MapLocation locationToTest;
		long smallestDist=1000000000; //arbitrary large number
		long temp;
		Direction closestDirection=null;
		int unitID = unit.id();
		
		//first find the direction that moves us closer to the target
		for(Direction dir : Direction.values()){
			locationToTest=currentLocation.add(dir);
			//make sure it is on the map and is passable
			if(gc.canMove(unitID, dir)){
				//make sure the location hasn't already been visited
				if(!pastLocations.contains(locationToTest)){
					//at this point its a valid location to test, check its distance
					temp = locationToTest.distanceSquaredTo(targetLocation);
					if (temp<smallestDist){
						smallestDist=temp;
						closestDirection=dir;
					}
				}
			}
		}//end of for-each loop
		
		//movement and maintenance
		if(closestDirection!=null){
			navInDirection(true, closestDirection);
		}
		
		//have we arrived close enough?
		if(unit.location().mapLocation().distanceSquaredTo(targetLocation)<=accuracy){
			accuracy = 2;
			targetLocation = null;
			atTargetLocation=true;
		}
	}
	
	/**
	 * Code to navigate in a given direction. All navigation related code will funnel to here
	 * @param prechecked - was the direction verified before calling this method?
	 */
	private void navInDirection(boolean prechecked, Direction direction){
		boolean resultOfCheck = true;
		if(!prechecked){
			resultOfCheck = gc.canMove(unit.id(), direction);
		}
		
		if(resultOfCheck){
			//add current location
			pastLocations.add(unit.location().mapLocation());
			//move
			gc.moveRobot(unit.id(), direction);
			//get rid of oldest to maintain recent locations
			if (pastLocations.size()>9)
				pastLocations.remove(0);
			
		}
		
	}


	public MapLocation getTargetLocation() {
		return targetLocation;
	}


	public void setTargetLocation(MapLocation targetLocation) {
		this.targetLocation = targetLocation;
		atTargetLocation = false;
	}

}
