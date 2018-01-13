import bc.*;

public class Channels {

	/**
	 * The channel class established communications channels for use by every other
	 * class else. in order to make the the channels publicly accessible by all
	 * other classes, a private local class is used to store and handle the methods.
	 */

	GameController gc; // obligatory

	/*
	 * For reference, the UnitType enums: 0Factory 1Healer 2Knight 3Mage 4Ranger
	 * 5Rocket 6Worker
	 */

	public Channels() {

	}

	// Earth Channels
	public ChannelInfo censusEarthFactory = new ChannelInfo(0, Planet.Earth);
	public ChannelInfo censusEarthHealer = new ChannelInfo(1, Planet.Earth);
	public ChannelInfo censusEarthKnight = new ChannelInfo(2, Planet.Earth);
	public ChannelInfo censusEarthMage = new ChannelInfo(3, Planet.Earth);
	public ChannelInfo censusEarthRanger = new ChannelInfo(4, Planet.Earth);
	public ChannelInfo censusEarthRocket = new ChannelInfo(5, Planet.Earth);
	public ChannelInfo censusEarthWorker = new ChannelInfo(6, Planet.Earth);

	// Mars Channels
	public ChannelInfo censusMarsFactory = new ChannelInfo(0, Planet.Mars);
	public ChannelInfo censusMarsHealer = new ChannelInfo(1, Planet.Mars);
	public ChannelInfo censusMarsKnight = new ChannelInfo(2, Planet.Mars);
	public ChannelInfo censusMarsMage = new ChannelInfo(3, Planet.Mars);
	public ChannelInfo censusMarsRanger = new ChannelInfo(4, Planet.Mars);
	public ChannelInfo censusMarsRocket = new ChannelInfo(5, Planet.Mars);
	public ChannelInfo censusMarsWorker = new ChannelInfo(6, Planet.Mars);

	
	
	/*
	 * The channel Info class contains the actual logic of the class,
	 * namely the getters and setters for the information in each individual channel
	 */
	protected class ChannelInfo {
		private Planet planet = Planet.Earth;		//the planet for the team array
		private int frequency = 0;					//the position in the team array
		
		
		//valued constructor
		public ChannelInfo(int frequency, Planet planet) {
			this.planet = planet;
			this.frequency = frequency;
		}

		//getter
		public long getMessage() {
			return gc.getTeamArray(planet).get(frequency);
		}
		
		//setter
		public void setMessage(int message) {
			gc.writeTeamArray(frequency, message);
		}

	}
}
