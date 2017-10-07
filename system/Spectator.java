package system;

public interface Spectator {
	/**
	 * Updates the viewer about the game state.
	 * 
	 * @param board
	 *            - the current board
	 * @param armyState
	 *            - the state of each player's army and land
	 */
	public void update(Viewed[][] board, int[][] armyState);
}
