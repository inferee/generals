package system;

/**
 * The player view of a space.
 * 
 * @author Axel Li
 */
public class Viewed {
	public final boolean known;
	public final boolean mountain;
	public final int type;
	public final int troops;
	public final boolean city;
	public final boolean king;

	/**
	 * Creates a new Viewed object.
	 * 
	 * @param known
	 *            - <code> true</code> if the land is known
	 * @param mountain
	 *            - <code> true</code> if the land appears as a mountain
	 * @param type
	 *            - the type of the troops on the land
	 * @param troops
	 *            - the number of troops on the land
	 * @param city
	 *            - <code> true</code> if the land is a city
	 * @param king
	 *            - <code> true</code> if the land is a king
	 */
	public Viewed(boolean known, boolean mountain, int type, int troops, boolean city, boolean king) {
		this.known = known;
		this.mountain = mountain;
		this.type = type;
		this.troops = troops;
		this.city = city;
		this.king = king;
	}
}
