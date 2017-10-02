package system;

/**
 * A player in the game.
 * 
 * @author Axel Li
 */
public interface Player {
	/**
	 * Updates the player about the game state.
	 * 
	 * @param board
	 *            - the current board
	 * @param armyState
	 *            - the state of each player's army and land
	 */
	public void update(Viewed[][] board, int[][] armyState);

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
