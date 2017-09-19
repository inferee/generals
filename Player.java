package system;

import java.util.Queue;

public interface Player {
	public void setQueue(Queue<Move> queue);
	
	public void update(Viewed[][] board, int[][] armyState);

	public void reset();
}
