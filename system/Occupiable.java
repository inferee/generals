package system;

/**
 * An occupiable space.
 * 
 * @author Axel Li
 */
public interface Occupiable {
	/**
	 * Reinforces the troops on the land.
	 */
	public void reinforce();

	/**
	 * Moves all possible troops away
	 * 
	 * @return the number of troops being moved
	 */
	public int move();

	/**
	 * Moves half of the troops away
	 * 
	 * @return the number of troops being moved
	 */
	public int split();

	/**
	 * Moves troops into the land
	 * 
	 * @param type
	 *            - the type of the troops
	 * @param troops
	 *            - the number of troops
	 */
	public void attacked(int type, int troops);

	/**
	 * Changes the type.
	 * 
	 * @param type
	 *            - the new type.
	 */
	public void updateType(int type);

	/**
	 * Returns the type of the troops.
	 * 
	 * @return the type of the troops
	 */
	public int getType();

	/**
	 * Returns the number of troops.
	 * 
	 * @return the number of troops
	 */
	public int getTroops();
}
