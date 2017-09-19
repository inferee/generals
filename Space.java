package system;

public class Space {
	private boolean mountain; // true if the tile appears as a mountain when undiscovered

	public Space(boolean mountain) {
		this.mountain = mountain;
	}

	public boolean getMountain() {
		return mountain;
	}
}
