import bc.*;
import java.util.ArrayList;

/**\
 * Library to handle research. The research queue consists of smaller modules that can be created with specfic goals in mind.
 * The goals will be able to be shuffled around and be interrupted.
 * @author tbuis
 *
 *
 */

public class ResearchManager {
	private GameController gc;		//obligatory
	protected ArrayList<ResearchBuildOrder> researchOrder = new ArrayList <ResearchBuildOrder>(); //the actual queue in use
	int researchOrderProgress = 0;//position in the queue
	
	public ResearchManager() {
		researchOrder.add(snipe);
		researchOrder.add(rocket);
		researchOrder.add(blink);
	}
	
	

	// some research order modules, partially as examples. (probably needs to be
	// moved elsewhere in the class)
	ResearchBuildOrder snipe = new ResearchBuildOrder(new int[] { 4, 4, 4 }, true);
	ResearchBuildOrder blink = new ResearchBuildOrder(new int[] { 3, 3, 3, 3 }, true);
	ResearchBuildOrder rocket = new ResearchBuildOrder(new int[] { 5 }, true);
	ResearchBuildOrder javelin = new ResearchBuildOrder(new int[] { 2, 2, 2 }, true);
	// ResearchBuildOrder roundedCombat = new ResearchBuildOrder (new int[]
	// {2,3,4},false);

	//really lazy implementation to the current snipe first strategy;
	
	// tracks the completed researches for each tree independently.
	// Because UnitType as an enum includes factories at 0, they must also be
	// included here
	protected int[] researchProgress = { 0, 0, 0, 0, 0, 0, 0 };

	// maximum researches for each tree.
	// makes sure we don't research excessively
	// and then explode for being wrong
	protected int[] maxResearches = { 0, 3, 3, 4, 3, 3, 4 };

	/*
	 * For reference, the UnitType enums: 0Factory 1Healer 2Knight 3Mage 4Ranger
	 * 5Rocket 6Worker
	 */

	// The unit type of the current research
	protected UnitType currentResearchType;

	/*
	 *The call per game turn to check if the current research task needs updating.
	 */
	public void updateResearchQueue() {
		ResearchInfo currentResearch = gc.researchInfo();
		if (currentResearch.roundsLeft() == 0) {
			researchProgress[currentResearchType.ordinal()]++;
			addNextResearch();
		}

	}

	
	/*
	 * The actual logic
	 */
	public void addNextResearch() {
		UnitType researchThis = null;
		while(researchThis==null && researchOrderProgress< researchOrder.size()) {
			UnitType tempResearch = researchOrder.get(researchOrderProgress).getNextResearch();
			if(tempResearch != null) {
				researchThis = tempResearch;
			} else {
				researchOrderProgress++;
			} //end if/else
		}//end while
		gc.queueResearch(researchThis);
		
	}

	
	
	
	
	public ArrayList<ResearchBuildOrder> getResearchOrder() {
		return researchOrder;
	}

	public void setResearchOrder(ArrayList<ResearchBuildOrder> researchOrder) {
		this.researchOrder = researchOrder;
	}

	public UnitType getCurrentResearchType() {
		return currentResearchType;
	}

	public void setCurrentResearchType(UnitType currentResearchType) {
		this.currentResearchType = currentResearchType;
	}





	private class ResearchBuildOrder {
		/*
		 * ResearchBuildOrder class stores prebuilt research orders for simplicity.
		 * Indcluding dependencies skips of levels of research that are already
		 * completed. If for example we only wanted worker1 to be done and it was
		 * already finished it would not add worker2 to the queue.
		 */
		private ArrayList<UnitType> queue; // workhorse of the class.
		private boolean includingDependencies = false;
		private int currentArr = 0; //current position in the research queue

		// valued constructor. Takes an array of of ints for visual compression and
		// creates a research queue based on the corresponding unit types
		public ResearchBuildOrder(int[] queue, boolean includingDependencies) {

			// transfer and conversion of the given unit type ints into real values
			if (queue.length != 0) {			//data validation;
				for (int i = 0; i < queue.length; i++) {
					this.queue.add(UnitType.values()[queue[i]]);
				}		//end for
			}		//end data validation if
			
			this.includingDependencies = includingDependencies;
		}
		
		//Gets the next research from the buildorder. There is probably some better way to manage the data validation segment
		public UnitType getNextResearch() {
			if(currentArr < queue.size()) {
				boolean valid = false;
				UnitType returnThis = null;
				
				//more data validation. Are we about to attempt impossible research?
				while(!valid && currentArr<queue.size()) { 							
					//find the next research in line
					returnThis = queue.get(currentArr);								
					//what position in the enum array is it
					int tempArrIndex = returnThis.ordinal();
					//if we haven't done too much research in that field, do it
					if(researchProgress[tempArrIndex]<maxResearches[tempArrIndex]) {	
						valid = true;
					}
					//increment our position in the queue even if the attempted research fails
					currentArr++;													
				}
				//failsafe against going too far into the queue
				if(!valid) {														
					returnThis = null;
				}
				return returnThis;
			}
			//if we had already exhausted this queue move on.
			else return null;														
		}
		

	}
}
