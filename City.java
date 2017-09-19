package system;

public class City extends Space implements Occupiable {
	private boolean king;
	private int type;
	private int troops;

	public City(int type, boolean king) {
		super(true);
		this.type = type;
		this.king = king;
		this.troops = 1;
	}

	public void reinforce() {
		troops++;
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

	public boolean isKing() {
		return king;
	}
}
