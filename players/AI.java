package players;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import system.Move;
import system.Player;
import system.Viewed;

/**
 * A hard coded AI that plays generals.io.
 * 
 * @author Axel Li
 */
public class AI implements Player {
	private final int playerNumber;
	private final int iSize;
	private final int jSize;
	private ArrayDeque<Move> queue;
	private Viewed[][] history;
	private int[][] recent;
	private Tracker[][] track;
	private int[][] armyState;
	private int[][] shiftOrder;
	private int iPos;
	private int jPos;
	private int randomTick;
	private int iRand;
	private int jRand;

	// private SpectatorGraphics s;
	/**
	 * Creates a new AI with the parameters.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param kingdoms
	 *            - the number of kingdoms
	 * @param playerNumber
	 *            - the player number of the AI
	 */
	public AI(int i, int j, int kingdoms, int playerNumber) {
		this.playerNumber = playerNumber;
		iSize = i;
		jSize = j;
		queue = new ArrayDeque<>();
		history = new Viewed[i][j];
		recent = new int[i][j];
		track = new Tracker[i][j];
		armyState = new int[playerNumber][2];
		shiftOrder = new int[4][2];
		shiftOrder[0] = new int[] { 1, 0 };
		shiftOrder[1] = new int[] { -1, 0 };
		shiftOrder[2] = new int[] { 0, 1 };
		shiftOrder[3] = new int[] { 0, -1 };
		iRand = 0;
		jRand = 0;
		// s = new SpectatorGraphics(i, j, kingdoms);
		initMatrix();
	}

	@Override
	public void update(Viewed[][] board, int[][] armyState) {
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (board[i][j].known) {
					history[i][j] = new Viewed(true, board[i][j].mountain, board[i][j].type, board[i][j].troops,
							board[i][j].city, board[i][j].king);
					recent[i][j] = 0;
				} else if (board[i][j].mountain && !history[i][j].city && !history[i][j].king
						&& !history[i][j].mountain) {
					history[i][j] = new Viewed(false, true, 0, 0, false, false);
					recent[i][j]++;
				} else if (!board[i][j].known && history[i][j].type == playerNumber) {
					history[i][j] = new Viewed(false, false, 0, 0, false, false);
				} else {
					recent[i][j]++;
				}
			}
		}
		if (!(board[iPos][jPos].known && !board[iPos][jPos].mountain && board[iPos][jPos].type == playerNumber
				&& board[iPos][jPos].troops > 1)) {
			resetPosition();
		}
		int[] next = determineMove();
		if (armyState[playerNumber - 1][0] > 0) {
			System.out.println("Player: " + playerNumber + " From: (" + iPos + "," + jPos + ")" + " To: (" + next[0]
					+ "," + next[1] + ")" + " Goal: (" + iRand + "," + jRand + ")");
		}
		queue.offer(new Move(iPos, jPos, next[0], next[1], true));
		iPos = next[0];
		jPos = next[1];
		clearTrack();
		// s.update(history, armyState);
	}

	/**
	 * Determines the next move to make.
	 * 
	 * @return the i and j values of the ending location in a <code>int[]</code>
	 *         array of size 2
	 */
	private int[] determineMove() {
		int iVal = -1;
		int jVal = -1;
		int minDist = Integer.MAX_VALUE;
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (history[i][j].type != playerNumber && history[i][j].king) {
					int dist = Math.abs(iPos - i) + Math.abs(jPos - j);
					if (dist < minDist) {
						minDist = dist;
						iVal = i;
						jVal = j;
					}
				}
			}
		}
		if (iVal < 0 || jVal < 0) {// TODO: find target location when no kings are known
			if (--randomTick <= 0 || (history[iRand][jRand].type == playerNumber && recent[iRand][jRand] < 2)
					|| (history[iRand][jRand].known && history[iRand][jRand].mountain && recent[iRand][jRand] < 2)) {
				iRand = (int) (Math.random() * iSize);
				jRand = (int) (Math.random() * jSize);
				randomTick = 40;
			}
			iVal = iRand;
			jVal = jRand;
		}
		Tracker result = search(iVal, jVal);
		if (result == null || result.previous == null) {
			return new int[] { iPos, jPos };
		}
		while (result.previous.previous != null) {
			result = result.previous;
		}
		return new int[] { result.i, result.j };
	}

	/**
	 * Calculates the optimal path based on what is known.
	 * 
	 * @param i
	 *            - the i value of the target location
	 * @param j
	 *            - the j value of the target location
	 * @return a <code>Tracker</code> object that functions as a linked list storing
	 *         a series of previous locations
	 */
	private Tracker search(int i, int j) {
		PriorityQueue<Tracker> pq = new PriorityQueue<>(new Comparator<Tracker>() {
			public int compare(Tracker t1, Tracker t2) {
				return t1.cost > t2.cost ? 1 : -1;
			}
		});
		HashSet<Tracker> set = new HashSet<>();
		track[iPos][jPos].update(null, 0, 1, history[iPos][jPos].troops, 0);
		pq.offer(track[iPos][jPos]);
		while (!pq.isEmpty()) {
			Tracker t = pq.poll();
			if (t == track[i][j]) {
				return t;
			} else if (!history[t.i][t.j].mountain) {
				set.add(track[t.i][t.j]);
				shuffleShiftOrder();
				for (int[] shift : shiftOrder) {
					int iNew = t.i + shift[0];
					int jNew = t.j + shift[1];
					if (iNew >= 0 && iNew < iSize && jNew >= 0 && jNew < jSize) {
						int lost = t.lost;
						int gained = t.gained;
						int steps = t.steps + 1;
						if (history[iNew][jNew].type == playerNumber && history[iNew][jNew].troops > 0) {
							gained += history[iNew][jNew].troops - 1;
						} else {
							lost += history[iNew][jNew].troops + 1;
						}
						double cost = Math.sqrt(steps) * lost / gained;
						if (!set.contains(track[iNew][jNew]) && !pq.contains(track[iNew][jNew])) {
							track[iNew][jNew].update(t, steps, lost, gained, cost);
							pq.offer(track[iNew][jNew]);
						} else if (pq.contains(track[iNew][jNew]) && cost < track[iNew][jNew].cost) {
							pq.remove(track[iNew][jNew]);
							track[iNew][jNew].update(t, steps, lost, gained, cost);
							pq.offer(track[iNew][jNew]);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Shuffles the array which determines which moves are considered first.
	 */
	private void shuffleShiftOrder() {
		for (int i = 0; i < 4; i++) {
			int x = (int) (Math.random() * (4 - i));
			int[] tmp = shiftOrder[x];
			shiftOrder[x] = shiftOrder[i];
			shiftOrder[i] = tmp;
		}
	}

	/**
	 * Sets the selected square to the space with the most troops.
	 */
	private void resetPosition() {
		int max = 0;
		int iMax = 0;
		int jMax = 0;
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (history[i][j].type == playerNumber && history[i][j].troops > max) {
					max = history[i][j].troops;
					iMax = i;
					jMax = j;
				}
			}
		}
		iPos = iMax;
		jPos = jMax;
	}

	/**
	 * Sets up the <code>Tracker</code> and <code>Viewed</code> objects in the
	 * matrices
	 */
	private void initMatrix() {
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				track[i][j] = new Tracker(i, j);
				history[i][j] = new Viewed(false, false, 0, 0, false, false);
			}
		}
	}

	/**
	 * Resets the matrix of <code>Tracker</code> objects
	 */
	private void clearTrack() {
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				track[i][j].update(null, 0, 0, 0, 0);
			}
		}
	}

	@Override
	public ArrayDeque<Move> getQueue() {
		return queue;
	}

	@Override
	public void failedMove(Move failing) {
		iPos = failing.startI;
		jPos = failing.startJ;
	}

	@Override
	public void reset(int winner) {
		initMatrix();
	}

	/**
	 * A helper class to keep track of the moves in a sequence.
	 * 
	 * @author Axel Li
	 */
	private class Tracker {
		public final int i;
		public final int j;
		public Tracker previous;
		public int steps;
		public int lost;
		public int gained;
		public double cost;

		/**
		 * Creates a new <code>Tracker</code> with its location.
		 * 
		 * @param i
		 *            - the i location
		 * @param j
		 *            - the j location
		 */
		public Tracker(int i, int j) {
			this.i = i;
			this.j = j;
		}

		/**
		 * Changes the values stored.
		 * 
		 * @param previous
		 *            - the previous <code>Tracker</code>
		 * @param steps
		 *            - the number of steps
		 * @param lost
		 *            - the number of troops lost
		 * @param gained
		 *            - the number of troops gained
		 * @param cost
		 *            - the cost of traveling to the location
		 */
		public void update(Tracker previous, int steps, int lost, int gained, double cost) {
			this.previous = previous;
			this.steps = steps;
			this.lost = lost;
			this.gained = gained;
			this.cost = cost;
		}
	}
}
