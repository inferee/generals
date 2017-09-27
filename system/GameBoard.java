package system;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

import graphics.PlayerGraphics;

public class GameBoard {
	private final int kingdoms;
	private final int iSize;
	private final int jSize;
	private GameGenerator g;
	private Player[] players;
	private ArrayDeque<Move>[] moves;
	private Space[][] board;
	private boolean[] dead;
	private int[][] armyState;
	private int count;

	public GameBoard(int i, int j, int mountains, int cities, int kingdoms, int minMaze, int minMan) {
		this.kingdoms = kingdoms;
		this.iSize = i;
		this.jSize = j;
		board = new Space[iSize][jSize];
		g = new GameGenerator(iSize, jSize, mountains, cities, kingdoms, minMaze, minMan);
		reset();
	}

	public void reset() {
		randomize();
		initializePlayers();
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
					board[i][j] = new City(0, 40 + (int) Math.random() * 6, false);
				} else {
					board[i][j] = new City(grid[i][j], 1, true);
				}
			}
		}
	}

	private void initializePlayers() {
		moves = new ArrayDeque[kingdoms];
		players = new Player[kingdoms];
		for (int i = 0; i < moves.length; i++) {
			moves[i] = new ArrayDeque<>();
			players[i] = new PlayerGraphics(iSize, jSize, kingdoms, i + 1, moves[i]);
		}
	}

	private void step() {
		for (int i = 0; i < kingdoms; i++) {
			if (!moves[i].isEmpty()) {
				Move m = moves[i].poll();
				System.out.println("(" + m.startI + "," + m.startJ + ") to (" + m.endI + "," + m.endJ + ")");
				if (m != null && !makeMove(m, i)) {
					players[i].failedMove(m);
					moves[i].clear();
				} else if (board[m.endI][m.endJ] instanceof Occupiable
						&& ((Occupiable) board[m.endI][m.endJ]).getType() != i + 1) {
					players[i].failedMove(m);
				}
			}
		}
	}

	private boolean makeMove(Move m, int playerIndex) {
		if (isValid(m) && board[m.startI][m.startJ] instanceof Occupiable
				&& ((Occupiable) board[m.startI][m.startJ]).getType() == playerIndex + 1
				&& board[m.endI][m.endJ] instanceof Occupiable) {
			Occupiable from = ((Occupiable) board[m.startI][m.startJ]);
			Occupiable to = ((Occupiable) board[m.endI][m.endJ]);
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
		return m.startI >= 0 && m.startI < iSize && m.startJ >= 0 && m.startJ < jSize && m.endI >= 0 && m.endI < iSize
				&& m.endJ >= 0 && m.endJ < jSize && Math.abs(m.startI - m.endI) + Math.abs(m.startJ - m.endJ) == 1;

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
											result[i + iShift][j + jShift] = new Viewed(true, false, c.getType(),
													c.getTroops(), false, true);
										} else {
											result[i + iShift][j + jShift] = new Viewed(true, false, c.getType(),
													c.getTroops(), true, false);
										}
									} else {
										Territory c = (Territory) current;
										result[i + iShift][j + jShift] = new Viewed(true, false, c.getType(),
												c.getTroops(), false, false);
									}
								} else {
									result[i + iShift][j + jShift] = new Viewed(true, true, 0, 0, false, false);
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
