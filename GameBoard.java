package system;

import generatorProofOfConcept.GameGenerator;

import java.util.LinkedList;
import java.util.Queue;

public class GameBoard {
	private final int kingdoms;
	private final int iSize;
	private final int jSize;
	private GameGenerator g;
	private Player[] players;
	private Queue<Move>[] moves;
	private Space[][] board;
	private boolean[] dead;
	private int[][] armyState;
	private int count;

	public GameBoard(int i, int j, int mountains, int cities, int kingdoms, int minMaze, int minMan) {
		this.kingdoms = kingdoms;
		this.iSize = i;
		this.jSize = j;
		players = new Player[kingdoms];
		board = new Space[iSize][jSize];
		g = new GameGenerator(iSize, jSize, mountains, cities, kingdoms, minMaze, minMan);
		reset();
	}

	public void reset() {
		randomize();
		updateQueues();
		updateArmyState();
		updatePlayers();
		count = 0;
		dead = new boolean[kingdoms];
	}

	public void cycle() {
		count++;
		reinforceAll();
		step();
		updateArmyState();
		updatePlayers();
	}

	public int getCount() {
		return count;
	}

	public boolean[] getStatus() {
		return dead;
	}

	private void randomize() {
		boolean successful = false;
		while (!successful) {
			successful = g.create(100, false);
		}
		int[][] grid = g.getGrid();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == 0) {
					board[i][j] = new Territory();
				} else if (grid[i][j] == -1) {
					board[i][j] = new Space(true);
				} else if (grid[i][j] == -2) {
					board[i][j] = new City(0, false);
				} else {
					board[i][j] = new City(grid[i][j], true);
				}
			}
		}
	}

	private void updateQueues() {
		for (int i = 0; i < players.length; i++) {
			moves[i] = new LinkedList<>();
			players[i].setQueue(moves[i]);
		}
	}

	private void step() {
		for (int i = 0; i < kingdoms; i++) {
			if (!moves[i].isEmpty()) {
				Move m = moves[i].poll();
				if (!makeMove(m)) {
					moves[i].clear();
				}
			}
		}
	}

	private boolean makeMove(Move m) {
		int newI = m.i;
		int newJ = m.j;
		if (m.ij) {
			if (m.pos) {
				newI++;
			} else {
				newI--;
			}
		} else {
			if (m.pos) {
				newJ++;
			} else {
				newJ--;
			}
		}
		if (isValid(m) && board[m.i][m.j] instanceof Occupiable && board[newI][newJ] instanceof Occupiable) {
			Occupiable from = ((Occupiable) board[m.i][m.j]);
			Occupiable to = ((Occupiable) board[newI][newJ]);
			int previous = to.getType();
			if (m.all) {
				to.attacked(from.getType(), from.move());
			} else {
				to.attacked(from.getType(), from.split());
			}
			if (to.getType() != previous && to instanceof City && ((City) to).isKing()) {
				dead[previous - 1] = true;
				convert(previous, from.getType());
			}
			return true;
		} else {
			return false;
		}
	}

	private void convert(int before, int after) {
		for (Space[] r : board) {
			for (Space s : r) {
				if (s instanceof Territory && ((Territory) s).getType() == before) {
					((Territory) s).updateType(after);
				}
			}
		}
	}

	private boolean isValid(Move m) {// Checks if a move is in the board
		if (m.ij) {
			if (m.pos) {
				return m.i >= 0 && m.i < iSize - 1 && m.j >= 0 && m.j < jSize;
			} else {
				return m.i > 0 && m.i < iSize && m.j >= 0 && m.j < jSize;
			}
		} else {
			if (m.pos) {
				return m.i >= 0 && m.i < iSize && m.j >= 0 && m.j < jSize - 1;
			} else {
				return m.i >= 0 && m.i < iSize && m.j > 0 && m.j < jSize;
			}
		}
	}

	private void updatePlayers() {// Updates each of the players
		for (int i = 0; i < players.length; i++) {
			players[i].update(playerView(i + 1), armyState);
		}
	}

	private Viewed[][] playerView(int p) {// Returns each player's view
		Viewed[][] result = new Viewed[iSize][jSize];
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (board[i][j] instanceof Occupiable && ((Occupiable) board[i][j]).getType() == p) {
					for (int iShift = -1; iShift <= 1; iShift++) {
						for (int jShift = -1; jShift <= 1; jShift++) {
							if (i + iShift >= 0 && i + iShift < iSize && j + jShift >= 0 && j + jShift < jSize
									&& result[i + iShift][j + jShift] == null) {
								Space current = board[i + iShift][j + jShift];
								if (current instanceof Occupiable) {
									if (current instanceof City) {
										City c = (City) current;
										if (c.isKing()) {
											result[i + iShift][j + jShift] = new Viewed(true, true, c.getType(),
													c.getTroops(), true, true);
										} else {
											result[i + iShift][j + jShift] = new Viewed(true, true, c.getType(),
													c.getTroops(), true, false);
										}
									} else {
										Territory c = (Territory) current;
										result[i + iShift][j + jShift] = new Viewed(true, true, c.getType(),
												c.getTroops(), false, false);
									}
								} else {
									result[i + iShift][j + jShift] = new Viewed(true, false, 0, 0, false, false);
								}
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (result[i][j] == null) {
					result[i][j] = new Viewed(false, board[i][j].getMountain(), 0, 0, false, false);
				}
			}
		}
		return result;
	}

	private void reinforceAll() {// Increases army size
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (board[i][j] instanceof Occupiable) {
					if (count % 20 == 0 || (board[i][j] instanceof City && count % 2 == 0)) {
						((Occupiable) board[i][j]).reinforce();
					}
				}
			}
		}
	}

	private void updateArmyState() {// Updates army size and land
		int[][] newState = new int[kingdoms][2];
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				if (board[i][j] instanceof Occupiable) {
					int type = ((Occupiable) board[i][j]).getType();
					if (type != 0) {
						newState[type - 1][0]++;
						newState[type - 1][1] += ((Occupiable) board[i][j]).getTroops();
					}
				}
			}
		}
		armyState = newState;
	}
}
