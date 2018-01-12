// import the API.
// See xxx for the javadocs.
import java.util.HashMap;
import java.util.Map;

import bc.*;

public class Player {
	/*Map of NavigationManagers for units.  NavigationManagers are accessed via their 
	 * ID.  Opted for a map instead of an array list since it won't
	 * accept repeat values, and we won't have to deal with updating
	 * units as they die.
	 */
	public static Map<Integer, NavigationManager> navigationManagers = new HashMap<Integer, NavigationManager>();
	
    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();
        
        //initializing player
        initPlayer(gc);

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                NavigationManager test = navigationManagers.get(unit.id());
                if(gc.isMoveReady(unit.id())){
	                Direction dir = test.navigate(unit);
	                if(dir!=null){
		                System.out.println("about to move unit "+unit.id()+" "+dir.name());
		                System.out.println("currentLocation "+unit.location().mapLocation());
		                if(dir.equals(Direction.Southwest)){
		                	if (gc.canMove(unit.id(), Direction.Southwest)) {
		                        gc.moveRobot(unit.id(), Direction.Southwest);
		                        System.out.println("moving south west");
		                	}
		                }else if(dir.equals(Direction.Southeast)){
		                	if (gc.canMove(unit.id(), Direction.Southeast)) {
		                        gc.moveRobot(unit.id(), Direction.Southeast);
		                        System.out.println("moving south east");
		                	}
		                }else if(dir.equals(Direction.South)){
		                	if (gc.canMove(unit.id(), Direction.South)) {
		                        gc.moveRobot(unit.id(), Direction.South);
		                        System.out.println("moving south");
		                	}
		                }
		                else if(dir.equals(Direction.East)){
		                	if (gc.canMove(unit.id(), Direction.East)) {
		                        gc.moveRobot(unit.id(), Direction.East);
		                        System.out.println("moving east");
		                	}
		                }
		                else if(dir.equals(Direction.West)){
		                	if (gc.canMove(unit.id(), Direction.West)) {
		                        gc.moveRobot(unit.id(), Direction.West);
		                        System.out.println("moving west");
		                	}
		                }else if(dir.equals(Direction.Northeast)){
		                	if (gc.canMove(unit.id(), Direction.Northeast)) {
		                        gc.moveRobot(unit.id(), Direction.Northeast);
		                        System.out.println("moving north east");
		                	}
		                }else if(dir.equals(Direction.Northwest)){
		                	if (gc.canMove(unit.id(), Direction.Northwest)) {
		                        gc.moveRobot(unit.id(), Direction.Northwest);
		                        System.out.println("moving north west");
		                	}
		                }else if(dir.equals(Direction.North)){
		                	if (gc.canMove(unit.id(), Direction.North)) {
		                        gc.moveRobot(unit.id(), Direction.North);
		                        System.out.println("moving north");
		                	}
		                }
	                }
                }
                System.out.println("current location "+unit.location().mapLocation());
                
                //THE BELOW WORKS, SO I'M GOING TO TRY TO HAVE THE NAVIGATION MANAGER
                //RETURN A DIRECTION AND HAVE THIS CLASS MOVE THE UNIT.
             /*// Most methods on gc take unit IDs, instead of the unit objects themselves.
                System.out.println("attempting to move unit "+unit.id()+" southwest");
                System.out.println("current position "+unit.location().mapLocation());
                if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Southwest)) {
                    gc.moveRobot(unit.id(), Direction.Southwest);
                }*/
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }//end main
    
    private static void initPlayer(GameController gc){
    	System.out.println("Initializing");
    	
    	//initialize starting workers navigation managers
    	VecUnit units = gc.myUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            NavigationManager temp = new NavigationManager(gc, unit);
            MapLocation targetLoc= new MapLocation(Planet.Earth, 10,5);
            temp.setTargetLocation(targetLoc);
            
            //                      key           value
            navigationManagers.put(unit.id(), new NavigationManager(gc, unit));
            System.out.println("added unit to NavigationManager map");
        }
        
        
    	
    }
}