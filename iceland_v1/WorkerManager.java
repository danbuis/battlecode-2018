import java.util.List;

import bc.*;

public class WorkerManager implements UnitManagersInterface{
	
	GameController gc;
	List<WorkerBot> workers;
	

	public WorkerManager(GameController gc, List<WorkerBot> workerList) {
		this.gc=gc;
		this.workers = workerList;
	}


	@Override
	public void issueOrderMoveAnyUnit(MapLocation targetLocation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void issueOrderMoveSpecificUnit(MapLocation targetLocation, Unit unit) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void issueOrderMoveAllUnits(MapLocation targetLocation) {
		for(WorkerBot bot: workers){
			bot.setTargetLocation(targetLocation);
		}		
	}


	@Override
	public void eachTurnMoveAllUnits() {
		for(WorkerBot bot:workers){
			if(bot.getTargetLocation()!=null)
				bot.navigate(gc.unit(bot.unitID));
		}
		
	}

}
