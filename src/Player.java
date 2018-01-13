// import the API.
// See xxx for the javadocs.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bc.*;

public class Player {
	/*Map of NavigationManagers for units.  NavigationManagers are accessed via their 
	 * ID.  Opted for a map instead of an array list since it won't
	 * accept repeat values, and we won't have to deal with updating
	 * units as they die.
	 */
	public static Map<Integer, BasicBot> basicBotMaps = new HashMap<Integer, BasicBot>();
	
	private static List<WorkerBot> workerList=new ArrayList<WorkerBot>();
	private static List<RangerBot> rangerList=new ArrayList<RangerBot>();
	private static List<MageBot> mageList=new ArrayList<MageBot>();
	private static List<HealerBot> healerList=new ArrayList<HealerBot>();
	private static List<KnightBot> knightList=new ArrayList<KnightBot>();
	private static List<RocketBot> rocketList=new ArrayList<RocketBot>();
	private static List<FactoryBot> factoryList=new ArrayList<FactoryBot>();
	private static List<Unit> blueprintList=new ArrayList<Unit>();

	private static WorkerManager workerManager;
	
	
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
        
        //initialize unit managers
        workerManager = new WorkerManager(gc, workerList, blueprintList);
        
        //initializing player
        initPlayer(gc);

        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            
            //clear all lists
            factoryList.clear();
            healerList.clear();
            knightList.clear();
            mageList.clear();
            rangerList.clear();
            rocketList.clear();
            workerList.clear();
            blueprintList.clear();
            
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                
                /* REMOVE LATER
                if(gc.round()==1){
                	basicBotMaps.get(unit.id()).setTargetLocation(new MapLocation(Planet.Earth, 10,5));
                }
                
                if(gc.round()==300){
                	basicBotMaps.get(unit.id()).setTargetLocation(new MapLocation(Planet.Earth, 0,0));
                }*/
                
                //sort units by type into the now empty lists
                UnitType type = unit.unitType();
                switch(type){
                case Factory:
                	if(unit.structureIsBuilt()==0){ //0=false
                		blueprintList.add(unit);
                	}else
                		factoryList.add((FactoryBot) basicBotMaps.get(unit.id()));
                case Healer:
                	healerList.add((HealerBot) basicBotMaps.get(unit.id()));
                case Knight:
                	knightList.add((KnightBot) basicBotMaps.get(unit.id()));
                case Mage:
                	mageList.add((MageBot) basicBotMaps.get(unit.id()));
                case Ranger:
                	rangerList.add((RangerBot) basicBotMaps.get(unit.id()));
                case Rocket:
                	if(unit.structureIsBuilt()==0){ //0=false
                		blueprintList.add(unit);
                	}else
                	rocketList.add((RocketBot) basicBotMaps.get(unit.id()));
                case Worker:
                	workerList.add((WorkerBot) basicBotMaps.get(unit.id()));
                }    
            } //end of counting and classifying units
            
            //example of sending an order to a manager, who will handle the implementation
            if(gc.round()==1){
            	System.out.println("Player order given");
            	workerManager.issueOrderMoveAllUnits(new MapLocation(Planet.Earth, 10,5));
            }
            
            if(gc.round()==250){
            	System.out.println("Player order given");
            	workerManager.issueOrderMoveAllUnits(new MapLocation(Planet.Earth, 0,0));
            }
            
            System.out.println("calling workerManager move all units");
            workerManager.eachTurnMoveAllUnits();
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }//end main
    
    private static void initPlayer(GameController gc){
    	System.out.println("Initializing");
    	
    	//initialize starting workers extended bot objects
    	VecUnit units = gc.myUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            WorkerBot startingWorker = new WorkerBot(gc, unit.id());
            
            //                  key        value
            basicBotMaps.put(unit.id(), startingWorker);
            System.out.println("added unit to NavigationManager map");
        }
        
        
    	
    }
}