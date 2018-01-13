import bc.MapLocation;

/**
 * A simple data structure to hold orders for bots.  Contains the type of order
 * and the location associated with it.  Individual bots are responsible for deciding
 * what to do with an Order.
 * @author DanBuis
 *
 */
public class Order {
	
	private OrderType type;
	private MapLocation location;
	
	public Order(OrderType type, MapLocation location){
		this.setType(type);
		this.setLocation(location);
	}

	public MapLocation getLocation() {
		return location;
	}

	private void setLocation(MapLocation location) {
		this.location = location;
	}

	public OrderType getType() {
		return type;
	}

	private void setType(OrderType type) {
		this.type = type;
	}
	
	public String toString() {
		return (" Order type: " + type.toString() + "at location: " + location.toString());
	}

}
