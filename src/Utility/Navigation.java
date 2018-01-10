package Utility;

import Units.ExtendedUnit;
import bc.*;

public class Navigation {
	public void navToPoint (GameController gc, ExtendedUnit unit, MapLocation targetLocation, int accuracy){
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
			if(!unit.pastLocations.contains(locationToTest)){
				//at this point its a valid location to test, check its distance
				temp = locationToTest.distanceSquaredTo(targetLocation);
				if (temp<smallestDist){
					smallestDist=temp;
					closestDirection=dir;
				}
			}
		}
	}
		
		if(closestDirection!=null){
			//add current location
			unit.pastLocations.add(currentLocation);
			//move
			gc.moveRobot(unitID, closestDirection);
			//get rid of oldest
			if (unit.pastLocations.size()>9)
				unit.pastLocations.remove(0);
			
		}
	}
}
