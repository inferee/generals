package system;

/**
 * A territory in the game.
 * 
 * @author Axel Li
 */
public class Territory extends Space implements Occupiable {
	private int type;
	private int troops;

	/**
	 * Creates a new Territory object.
	 */
	public Territory(int type, int troops) {
		super(false);
		this.type = type;
		this.troops = troops;
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
}
