import bc.*;

public interface UnitManagersInterface {
	public void issueOrderMoveAnyUnit(MapLocation targetLocation);
	public void issueOrderMoveSpecificUnit(MapLocation targetLocation, Unit unit);
	public void issueOrderMoveAllUnits(MapLocation targetLocation);
	public void eachTurnMoveAllUnits();
	
}
