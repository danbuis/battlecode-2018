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
	private static FactoryManager factoryManager;
	private static ResearchManager researchManager = new ResearchManager();
	private static RangerManager rangerManager;
	
	private static int targetWorkerPopulation=0;


	
	
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
        
        //initialize unit managers
        workerManager = new WorkerManager(gc, workerList, blueprintList, targetWorkerPopulation);
        factoryManager = new FactoryManager(gc, workerList, rangerList, mageList, healerList, knightList, factoryList);
        rangerManager = new RangerManager(rangerList);
        


        while (true) {
            System.out.println("Current round: "+gc.round());
            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            System.out.println("Number of units: "+units.size());
            
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
                
                //gotta check if the basicBotMaps has these units yet
                if(!basicBotMaps.containsKey(unit.id())){
                	//looks like it doesn't, better add it!
                	addUnitToBotMap(unit, gc);
                }
                               
                //sort units by type into the now empty lists
                UnitType type = unit.unitType();
                switch(type){
                case Factory:
                	if(unit.structureIsBuilt()==0){ //0=false
                		System.out.println("adding a blueprint to list");
                		blueprintList.add(unit);
                	}else
                		factoryList.add((FactoryBot) basicBotMaps.get(unit.id()));
                	break;
                case Healer:
                	healerList.add((HealerBot) basicBotMaps.get(unit.id()));
                	break;
                case Knight:
                	knightList.add((KnightBot) basicBotMaps.get(unit.id()));
                	break;
                case Mage:
                	mageList.add((MageBot) basicBotMaps.get(unit.id()));
                	break;
                case Ranger:
                	rangerList.add((RangerBot) basicBotMaps.get(unit.id()));
                	break;
                case Rocket:
                	if(unit.structureIsBuilt()==0){ //0=false
                		blueprintList.add(unit);
                	}else rocketList.add((RocketBot) basicBotMaps.get(unit.id()));
                	break;
                case Worker:
                	System.out.println("Adding unit to workerList: "+unit.id()+" of type "+unit.unitType());
                	workerList.add((WorkerBot) basicBotMaps.get(unit.id()));
                }    
            } //end of counting and classifying units
            
            //example of sending an order to a manager, who will handle the implementation
            if(gc.round()==1){
            	workerManager.issueOrderBlueprintStructure(UnitType.Factory);
            }
            
            if(gc.round()==50){
            	workerManager.issueOrderBlueprintStructureAtLocation(UnitType.Factory, new MapLocation(Planet.Earth, 10,5));
            }
            
            if(gc.round()==650){
            	workerManager.issueOrderAllBlueprintStructureAtLocation(UnitType.Factory, new MapLocation(Planet.Earth, 10,18));
            }

            iterateEachTurn();
         
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }//end main
    
    private static void iterateEachTurn() {
    	
    	   System.out.println("calling workerManager move all units");
           workerManager.eachTurnMoveAllUnits();
           
           System.out.println("calling rangerManager move all units");
           rangerManager.eachTurnMoveAllUnits();
           
           System.out.println("calling factoryManager build units");
           factoryManager.eachTurnBuildUnits();
		
	}

	private static void addUnitToBotMap(Unit unit, GameController gc) {
    	switch(unit.unitType()){
        case Factory:
        	 FactoryBot factBot = new FactoryBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), factBot);
        	break;
        case Healer:
        	 HealerBot healBot = new HealerBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), healBot);
        	break;
        case Knight:
        	 KnightBot knightBot = new KnightBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), knightBot);
        	break;
        case Mage:
        	 MageBot mageBot = new MageBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), mageBot);
        	break;
        case Ranger:
        	 RangerBot rangeBot = new RangerBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), rangeBot);
        	break;
        case Rocket:
        	 RocketBot rockBot = new RocketBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), rockBot);
        	break;
        case Worker:
        	 WorkerBot workBot = new WorkerBot(gc, unit.id());
        	 basicBotMaps.put(unit.id(), workBot);
        	 break;
    	}
		
	}

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
        
      //initialize target worker populations
        if(gc.planet()==Planet.Earth){
        	targetWorkerPopulation=6;
        }else targetWorkerPopulation=4;
        
        workerManager.resourceList=initResources(gc);
    	
    }

	private static List<MapLocation> initResources(GameController gc) {
		List<MapLocation> returnList = new ArrayList<MapLocation>();
		
		PlanetMap map = gc.startingMap(gc.planet());
		long height = map.getHeight();
		long width = map.getWidth();
		MapLocation test;
		
		for(int x=0; x==width; x++){
			for (int y=0; y==height; y++){
				test = new MapLocation(gc.planet(), x ,y);
				if(map.initialKarboniteAt(test)>0){
					returnList.add(test);
				}
			}//end y loop
		}//end x loop
		
		return returnList;
	}
}