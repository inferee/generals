package system;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import graphics.PlayerGraphics;

/**
 * The game board for generals.io.
 * 
 * @author Axel Li
 */
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

	/**
	 * Creates a new game board.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param mountains
	 *            - the number of mountains
	 * @param cities
	 *            - the number of cities
	 * @param kingdoms
	 *            - the number of kingdoms
	 * @param minMaze
	 *            - the minimum maze distance between kingdoms
	 * @param minMan
	 *            - the minimum manhattan distance between kingdoms
	 */
	public GameBoard(int i, int j, int mountains, int cities, int kingdoms, int minMaze, int minMan) {
		this.kingdoms = kingdoms;
		this.iSize = i;
		this.jSize = j;
		board = new Space[i][j];
		g = new GameGenerator(i, j, mountains, cities, kingdoms, minMaze, minMan);
		initializePlayers();
		reset();
	}

	/**
	 * Creates a custom game board.
	 * 
	 * @param board
	 *            -the custom board
	 * @param players
	 *            - the custom players
	 * @param mountains
	 *            - number of mountains (for the game board generator)
	 * @param cities
	 *            - number of cities (for the game board generator)
	 * @param kingdoms
	 *            - number of kingdoms (for the game board generator)
	 * @param minMaze
	 *            - minimum maze distance between kings (for the game board
	 *            generator)
	 * @param minMan
	 *            - minimum manhattan distance between kings (for the game board
	 *            generator)
	 */
	public GameBoard(Space[][] board, Player[] players, int mountains, int cities, int kingdoms, int minMaze,
			int minMan) {
		this.kingdoms = players.length;
		this.iSize = board.length;
		this.jSize = board[0].length;
		this.players = players;
		g = new GameGenerator(iSize, jSize, mountains, cities, kingdoms, minMaze, minMan);
		reset(board);
	}

	/**
	 * Resets the game and sets the board to the input board.
	 *
	 * @param board
	 *            - the new board to be set
	 */
	public void reset(Space[][] board) {
		this.board = board;
		dead = new boolean[kingdoms];
		count = 0;
		updateArmyState();
		updatePlayers();
	}

	/**
	 * Resets the game and randomly generates the board.
	 */
	public void reset() {
		randomize();
		dead = new boolean[kingdoms];
		count = 0;
		updateArmyState();
		updatePlayers();
	}

	/**
	 * Cycles the game for one round.
	 */
	public void cycle() {
		count++;
		reinforceAll();
		step();
		updateArmyState();
		updatePlayers();
	}

	/**
	 * Returns the number of cycles.
	 * 
	 * @return the number of cycles that have occurred.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Returns the status of the players.
	 * 
	 * @return the status of each player
	 */
	public boolean[] getStatus() {
		return dead;
	}

	/**
	 * Randomly generates the game board.
	 */
	private void randomize() {
		boolean successful = false;
		while (!successful) {
			successful = g.create(100, false);
		}
		int[][] grid = g.getGrid();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == 0) {
					board[i][j] = new Territory(0, 0);
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

	/**
	 * Initializes all players.
	 */
	private void initializePlayers() {
		moves = new ArrayDeque[kingdoms];
		players = new Player[kingdoms];
		for (int i = 0; i < moves.length; i++) {
			moves[i] = new ArrayDeque<>();
			players[i] = new PlayerGraphics(iSize, jSize, kingdoms, i + 1, moves[i]);
		}
	}

	/**
	 * Makes each player's next move in a random order.
	 */
	private void step() {
		int[] sequence = g.getSequence();
		for (int x = 0; x < kingdoms; x++) {
			int i = sequence[x];
			if (!moves[i].isEmpty()) {
				Move m = moves[i].poll();
				System.out.println("(" + m.startI + "," + m.startJ + ") to (" + m.endI + "," + m.endJ + ")");
				if (!makeMove(m, i) || board[m.endI][m.endJ] instanceof Occupiable
						&& ((Occupiable) board[m.endI][m.endJ]).getType() != i + 1) {
					players[i].failedMove(m);
					moves[i].clear();
				}
			}
		}
	}

	/**
	 * Attempts to make a move.
	 * 
	 * @param m
	 *            - the move being made
	 * @param playerIndex
	 *            - the index of the player making the move
	 * @return <code> true </code> if successful
	 */
	private boolean makeMove(Move m, int playerIndex) {
		if (isValid(m) && board[m.startI][m.startJ] instanceof Occupiable
				&& ((Occupiable) board[m.startI][m.startJ]).getType() == playerIndex + 1
				&& board[m.endI][m.endJ] instanceof Occupiable) {
			Occupiable from = ((Occupiable) board[m.startI][m.startJ]);
			Occupiable to = ((Occupiable) board[m.endI][m.endJ]);
			boolean king = to instanceof City && ((City) to).isKing();
			int previous = to.getType();
			if (m.all) {
				to.attacked(from.getType(), from.move());
			} else {
				to.attacked(from.getType(), from.split());
			}
			if (to.getType() != previous && king) {
				dead[previous - 1] = true;
				convert(previous, from.getType());
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks whether a move is in bounds and is exactly one step.
	 * 
	 * @param m
	 *            - the move to be checked
	 * @return <code> true </code> if the move is valid based on above parameters
	 */
	private boolean isValid(Move m) {
		return m.startI >= 0 && m.startI < iSize && m.startJ >= 0 && m.startJ < jSize && m.endI >= 0 && m.endI < iSize
				&& m.endJ >= 0 && m.endJ < jSize && Math.abs(m.startI - m.endI) + Math.abs(m.startJ - m.endJ) == 1;
	}

	/**
	 * Converts land belonging to a player when the king is captured.
	 * 
	 * @param before
	 *            - the previous index
	 * @param after
	 *            - the new index
	 */
	private void convert(int before, int after) {
		for (Space[] r : board) {
			for (Space s : r) {
				if (s instanceof Occupiable && ((Occupiable) s).getType() == before) {
					((Occupiable) s).updateType(after);
				}
			}
		}
	}

	/**
	 * Updates each of the players with their own view of the board.
	 */
	private void updatePlayers() {
		for (int i = 0; i < players.length; i++) {
			if (!dead[i]) {
				players[i].update(playerView(i + 1), armyState);
			} else {
				players[i].update(playerView(0), armyState);
			}
		}
	}

	/**
	 * Generates the viewed grid for a player.
	 * 
	 * @param p
	 *            - the player number
	 * @return the view for the player
	 */
	private Viewed[][] playerView(int p) {
		Viewed[][] result = new Viewed[iSize][jSize];
		if (p != 0) {
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
		} else {
			for (int i = 0; i < iSize; i++) {
				for (int j = 0; j < jSize; j++) {
					if (board[i][j] instanceof Occupiable) {
						result[i][j] = new Viewed(true, false, ((Occupiable) board[i][j]).getType(),
								((Occupiable) board[i][j]).getTroops(), board[i][j] instanceof City,
								(board[i][j] instanceof City && ((City) board[i][j]).isKing()));
					} else {
						result[i][j] = new Viewed(true, true, 0, 0, false, false);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Reinforces all valid troops in a cycle.
	 */
	private void reinforceAll() {
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

	/**
	 * Recounts the state of each player and updates armyState.
	 */
	private void updateArmyState() {
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