import bc.*;

public class RangerBot extends BasicBot {

	GameController gc;
	public int targetID = -1;

	public RangerBot(GameController gc, int unitID) {
		super(gc, unitID);
		// TODO Auto-generated constructor stub
		this.gc = gc;
	}

	public void performOrder() {
		switch (orderStack.peek().getType()) {
		case MOVE:
			attackMove();
			break;
		case SNIPE:
			snipe(orderStack.peek().getLocation());
			break;
		default:
			idle();
			break;
		}
	}

	public void attackMove() {
		VecUnit nearbyUnits = gc.senseNearbyUnits(thisUnit.location().mapLocation(), thisUnit.visionRange());
		if (nearbyUnits.size() > 0) {
			int closestUnit = findClosestEnemyUnit(nearbyUnits);	//find the closest enemy unit			
			attemptAttack(closestUnit);
		}
		//did it already shoot?
		if(thisUnit.attackCooldown()>10) {
			
		} else {
			navigate(thisUnit);
			gc.senseNearbyUnits(thisUnit.location().mapLocation(), thisUnit.visionRange());
			if (nearbyUnits.size() > 0) {
				int closestUnit = findClosestEnemyUnit(nearbyUnits);	//find the closest enemy unit			
				attemptAttack(closestUnit);
			}
		}
	}

	public void snipe(MapLocation loc) {
		if (gc.canBeginSnipe(unitID, loc) && gc.isBeginSnipeReady(unitID)) {
			gc.beginSnipe(unitID, loc);
		}
		orderStack.pop();
	}

	public void idle() {

	}

	public void attemptAttack(int targ) {
		if (targ != -1) {
			if (gc.canAttack(unitID, targ)) {
				gc.attack(unitID, targ);
			}
		}
	}

	public int findClosestEnemyUnit(VecUnit arr) {
		int returnThis = -1;
		long closestDistance = 1000000;		//arbitrarily large number
		Unit tempUnit = null;
		for (int i = 0; i < arr.size(); i++) {
			tempUnit = arr.get(i);
			if(tempUnit.team() != thisUnit.team()) {
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
