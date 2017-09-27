package system;

import java.util.ArrayDeque;

public interface Player {
	public void setQueue(ArrayDeque<Move> queue);

	public void update(Viewed[][] board, int[][] armyState);

	public void failedMove(Move failing);

	public void reset();
}
