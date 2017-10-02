package system;

/**
 * A move within the game board.
 * 
 * @author Axel Li
 */
public class Move {
	public final int startI;
	public final int startJ;
	public final int endI;
	public final int endJ;
	public final boolean all;

	/**
	 * Creates a Move object.
	 * 
	 * @param startI
	 *            - the starting I position
	 * @param startJ
	 *            - the starting J position
	 * @param endI
	 *            - the ending I position
	 * @param endJ
	 *            - the ending J position
	 * @param all
	 *            - <code>true</code> if moving all of the troops,
	 *            <code>false</code> if moving half of the troops
	 */
	public Move(int startI, int startJ, int endI, int endJ, boolean all) {
		this.startI = startI;
		this.startJ = startJ;
		this.endI = endI;
		this.endJ = endJ;
		this.all = all;
	}
}
