 package system;

public class Viewed {
	private boolean known;
	private boolean mountain;
	private int type;
	private int troops;
	private boolean city;
	private boolean king;

	public Viewed(boolean known, boolean mountain, int type, int troops, boolean city, boolean king) {
		this.known = known;
		this.mountain = mountain;
		this.type = type;
		this.troops = troops;
		this.city = city;
		this.king = king;
	}

	public boolean isKnown() {
		return known;
	}

	public boolean isMountain() {
		return mountain;
	}

	public int getType() {
		return type;
	}

	public int getTroops() {
		return troops;
	}

	public boolean isCity() {
		return city;
	}

	public boolean isKing() {
		return king;
	}
}
