import bc.*;
import java.util.Random;

public class RangerBot extends BasicBot {

	GameController gc;
	public int targetID = -1;

	public RangerBot(GameController gc, int unitID) {
		super(gc, unitID);
		// TODO Auto-generated constructor stub
		this.gc = gc;
	}

	// order action switch
	public void performOrder() {
		if (orderStack.size() != 0) {
			switch (orderStack.peek().getType()) {
			case MOVE:
				attackMove();
				break;
			case SNIPE:
				snipe(orderStack.peek().getLocation());
				break;

			default: // if the order is invalid, idle a turn and clean up the stack;
				idle();
				break;
			}
			idle();
		}
	}

	// attempts to attack. if it couldn't attack, move and try again.
	public void attackMove() {

		attackNearbyUnits();
		// did it already shoot?
		if (thisUnit.attackCooldown() > 10) {
			navigate(thisUnit);
		} else {
			navigate(thisUnit);
			attackNearbyUnits();
		}
	}

	// Ranger ability. Attempts to snipe and then clears the order
	public void snipe(MapLocation loc) {
		if (gc.canBeginSnipe(unitID, loc) && gc.isBeginSnipeReady(unitID)) {
			gc.beginSnipe(unitID, loc);
		}
		orderStack.pop();
	}

	// No valid orders received. Wobble around and shoot visible enemies
	public void idle() {
		moveInDirection(Direction.values()[(new Random().nextInt(8)) % 8], thisUnit);
		attackNearbyUnits();
		if (orderStack.size() != 0) {
			orderStack.pop();
		}
	}

	// general logic to find the closest units and attack them
	public void attackNearbyUnits() {
		VecUnit nearbyUnits = gc.senseNearbyUnits(thisUnit.location().mapLocation(), thisUnit.visionRange());
		if (nearbyUnits.size() != 0) {
			int closestUnit = findClosestEnemyUnit(nearbyUnits); // find the closest enemy unit
			
			// if the enemy donut holes the ranger (Dash Rendar), attempt to move the ranger away before
			// shooting
			long closestDist = gc.unit(closestUnit).location().mapLocation()
					.distanceSquaredTo(thisUnit.location().mapLocation());
			if (closestDist < thisUnit.rangerCannotAttackRange()) { // if too close
				int directionAway = (thisUnit.location().mapLocation()
						.directionTo(gc.unit(closestUnit).location().mapLocation()).ordinal() + 4) % 8;
				
				// attempt to move straight back
				moveInDirection(Direction.values()[directionAway], thisUnit);
				// diagonal back
				moveInDirection(Direction.values()[directionAway + 1], thisUnit);
				moveInDirection(Direction.values()[directionAway - 1], thisUnit);

			}
			closestDist = gc.unit(closestUnit).location().mapLocation()
					.distanceSquaredTo(thisUnit.location().mapLocation());
			//if the closest unit is still too close: shoot anything
			if(closestDist < thisUnit.rangerCannotAttackRange()) {
				attemptAttack(findAnEnemyUnitBeyondDonutHole(nearbyUnits));
			}
			attemptAttack(closestUnit);
		}
	}

	// logic for attacking
	public void attemptAttack(int targ) {
		if (targ != -1) {
			if (gc.canAttack(unitID, targ)) {
				gc.attack(unitID, targ);
			}
		}
	}
	
	/*
	 * In the event that a unit is inside the ranger's donut hole, logic for attacking something else that is also not donutholed.
	 */
	public int findAnEnemyUnitBeyondDonutHole(VecUnit arr) {

		for (int i = 0; i< arr.size(); i++) {
			if(arr.get(i).location().mapLocation().distanceSquaredTo(thisUnit.location().mapLocation())<thisUnit.rangerCannotAttackRange()){
				return arr.get(i).id();
			}
		}
		return -1;
	}

	// takes an array of nearby units and find the closest enemy
	public int findClosestEnemyUnit(VecUnit arr) {
		int returnThis = -1;
		long closestDistance = 1000000; // arbitrarily large number
		Unit tempUnit = null;
		for (int i = 0; i < arr.size(); i++) {
			tempUnit = arr.get(i);
			if (tempUnit.team() != thisUnit.team()) {
				long tempDist = thisUnit.location().mapLocation().distanceSquaredTo(tempUnit.location().mapLocation());
				if (tempDist < closestDistance) {
					closestDistance = tempDist;
					returnThis = tempUnit.id();
				}
			}
		}
		return returnThis;
	}

}
