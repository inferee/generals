package system;

/**
 * A city in the game.
 * 
 * @author Axel Li
 */
public class City extends Space implements Occupiable {
	private boolean king;
	private int type;
	private int troops;

	/**
	 * Creates a new City object.
	 * 
	 * @param type
	 *            - the type of the troops
	 * @param troops
	 *            - the number of troops
	 * @param king
	 *            - <code> true </code> if the city is a king
	 */
	public City(int type, int troops, boolean king) {
		super(true);
		this.type = type;
		this.troops = troops;
		this.king = king;
	}

	public void reinforce() {
		if (type != 0) {
			troops++;
		}
	}

	public int move() {
		if (troops <= 1) {
			return 0;
		} else {
			int x = troops - 1;
			troops = 1;
			return x;
		}
	}

	public int split() {
		if (troops <= 1) {
			return 0;
		} else {
			int x = troops / 2;
			troops -= x;
			return x;
		}
	}

	public void attacked(int type, int troops) {
		if (this.type == type) {
			this.troops += troops;
		} else {
			if (troops > this.troops) {
				this.type = type;
				this.troops = troops - this.troops;
				king = false;
			} else {
				this.troops -= troops;
			}
		}
	}

	public void updateType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public int getTroops() {
		return troops;
	}

	/**
	 * Returns whether the city is a king.
	 * 
	 * @return whether the city is a king
	 */
	public boolean isKing() {
		return king;
	}
}
