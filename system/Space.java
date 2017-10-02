package system;

/**
 * A piece of land in the game.
 * 
 * @author Axel Li
 */
public class Space {
	private boolean mountain;

	/**
	 * Creates a Space object
	 * 
	 * @param mountain
	 *            - whether the land appears to be a mountain when unknown
	 */
	public Space(boolean mountain) {
		this.mountain = mountain;
	}

	public boolean getMountain() {
		return mountain;
	}
}
