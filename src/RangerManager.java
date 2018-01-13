import java.util.List;

import bc.*;

public class RangerManager implements UnitManagersInterface {

	public List<RangerBot> rangers;

	public RangerManager(List<RangerBot> rangers) {
		this.rangers = rangers;
	}

	// passthrough method to add a new order to the end of the order queue
	public void issueNewOrder(OrderType type, MapLocation loc, RangerBot bot) {
		bot.orderStack.add(bot.orderStack.size(), new Order(type, loc));
	}

	// passthrough method to add a new order to the front of the order queue
	public void issueReplacementOrder(OrderType type, MapLocation loc, RangerBot bot) {
		bot.orderStack.push(new Order(type, loc));
	}

	// tell every ranger to snipe a location right now
	public void issueSnipe(MapLocation targetLocation) {
		for (int i = 0; i < rangers.size(); i++) {
			issueReplacementOrder(OrderType.SNIPE, targetLocation, rangers.get(i));
		}
	}

	// tell the closest bot to go to a location, preference on bots that aren't
	// doing anything.
	@Override
	public void issueOrderMoveAnyUnit(MapLocation targetLocation) {

		// idle bots pass
		long closestDist = 100000; // arbitrarily large number
		RangerBot closestBot = null;
		for (int i = 0; i < rangers.size(); i++) {
			RangerBot tempBot = rangers.get(i);
			if (tempBot.orderStack.size() == 0) {
				long tempDist = tempBot.thisUnit.location().mapLocation().distanceSquaredTo(targetLocation);
				if (tempDist < closestDist) {
					closestDist = tempDist;
					closestBot = tempBot;
				}
			}
		}
		// did we find an idle bot?
		if (closestBot != null) {
			issueNewOrder(OrderType.MOVE, targetLocation, closestBot);
			return;
		} else {
			// non-idle bots pass
			for (int i = 0; i < rangers.size(); i++) {
				RangerBot tempBot = rangers.get(i);

				long tempDist = tempBot.thisUnit.location().mapLocation().distanceSquaredTo(targetLocation);
				if (tempDist < closestDist) {
					closestDist = tempDist;
					closestBot = tempBot;
				}

			}
		}
	}

	// tell all idle bots to go to a location
	public void issueOrderMoveIdleUnits(MapLocation targetLocation) {
		RangerBot tempBot = null;
		for (int i = 0; i < rangers.size(); i++) {
			tempBot = rangers.get(i);
			if (tempBot.orderStack.size() == 0) {
				issueNewOrder(OrderType.MOVE, targetLocation, tempBot);
			}
		}
	}

	// Tell a specific Ranger to go to a location
	@Override
	public void issueOrderMoveSpecificUnit(MapLocation targetLocation, BasicBot unit) {
		// TODO Auto-generated method stub
		if (unit.thisUnit.unitType() == UnitType.Ranger) { // make sure this unit is in my jurisdiction
			issueNewOrder(OrderType.MOVE, targetLocation, (RangerBot) unit);
		}
	}

	// tell every bot to go to a location
	@Override
	public void issueOrderMoveAllUnits(MapLocation targetLocation) {
		for (int i = 0; i < rangers.size(); i++) {
			issueNewOrder(OrderType.MOVE, targetLocation, rangers.get(i));
		}

	}

	@Override
	public void eachTurnMoveAllUnits() {
		// TODO Auto-generated method stub
		for (int i = 0; i < rangers.size(); i++) {
			rangers.get(i).performOrder();
		}

	}

}
