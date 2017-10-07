package system;

/**
 * A player in the game.
 * 
 * @author Axel Li
 */
public interface Player extends Spectator {
	/**
	 * Notifies the player that a move has failed
	 * 
	 * @param failing
	 *            - the failing move
	 */
	public void failedMove(Move failing);

	/**
	 * Resets the player
	 * 
	 * @param winner
	 *            - the player number of the victor
	 */
	public void reset(int winner);
}
