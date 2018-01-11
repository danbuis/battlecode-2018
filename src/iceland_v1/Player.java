package iceland_v1;
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
        
        initPlayer(gc);

        while (true) {
            //System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                NavigationManager test = navigationManagers.get(unit.id());
                test.navigate();
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
            temp.setTargetLocation(new MapLocation(Planet.Earth, 5,5));
            
            //                      key           value
            navigationManagers.put(unit.id(), new NavigationManager(gc, unit));
            System.out.println("added unit to NavigationManager map");
        }
        
        
    	
    }
}