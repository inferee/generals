package system;

import java.util.ArrayList;
import java.util.List;

import graphics.PlayerGraphics;
import graphics.SpectatorGraphics;

/**
 * A game starter class for generals.
 * 
 * @author Axel Li
 */
public class GameStart {
	private static final int minI = 15;
	private static final int maxI = 25;
	private static final int minJ = 15;
	private static final int maxJ = 25;
	private static final double minMountain = 0;
	private static final double maxMountain = 0.2;
	private static final double minCity = 0;
	private static final double maxCity = 0.07;
	private static final int minTick = 100;
	private static final int maxTick = 2000;
	private static final int minWaitTime = 10;
	private static final int maxPlayers = 8;
	private static final int maxSpectators = 8;
	private int iSize = 18;
	private int jSize = 18;
	private double mountain = 0.12;
	private double city = 0.04;
	private int manhattan = -1;
	private int maze = -1;
	private int tick = 500;
	private List<Player> players;
	private List<Spectator> spectators;

	/**
	 * Creates a new game starter.
	 */
	public GameStart() {
		players = new ArrayList<>();
		spectators = new ArrayList<>();
	}

	/**
	 * Starts the game and returns true when done.
	 * 
	 * @return <code>true</code>
	 * @throws InterruptedException
	 */
	public boolean startGame() throws InterruptedException {
		GameBoard g = new GameBoard(players.toArray(new Player[players.size()]),
				spectators.toArray(new Spectator[spectators.size()]), iSize, jSize, mountain, city, manhattan, maze);
		int x = 200;
		while (x-- > 0) {// TODO: for debugging purposes only, remove when finished
			g.cycle();
		}
		while (g.gameEnd() == 0) {
			long before = System.nanoTime();
			g.cycle();
			long time = (long) (tick - (System.nanoTime() - before) / 1e6);
			Thread.sleep(Math.max(time, minWaitTime));
		}
		return true;
	}

	/**
	 * Sets the size
	 * 
	 * @param i
	 *            - the i size
	 * @param j
	 *            - the j size
	 * @return <code>true</code> if successful
	 */
	public boolean setSize(int i, int j) {
		if (i >= minI && i <= maxI && j >= minJ && j <= maxJ) {
			iSize = i;
			jSize = j;
			updateMinDistance();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the size.
	 * 
	 * @return an array with iSize and jSize
	 */
	public int[] getSize() {
		return new int[] { iSize, jSize };
	}

	/**
	 * Sets the proportion of mountains and cities.
	 * 
	 * @param mountain
	 *            - the proportion of mountains
	 * @param city
	 *            - the proportion of cities
	 * @return <code>true</code> if successful
	 */
	public boolean setProportion(double mountain, double city) {
		if (mountain >= minMountain && mountain <= maxMountain && city >= minCity && city <= maxCity) {
			this.mountain = mountain;
			this.city = city;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the proportion of mountains and cities.
	 * 
	 * @return an array with the mountain and city proportions
	 */
	public double[] getProportion() {
		return new double[] { mountain, city };
	}

	/**
	 * Updates the minimum distances based on the other current values.
	 */
	private void updateMinDistance() {
		int val = (int) Math.sqrt(8.0 / 5 * iSize * jSize / players.size());
		maze = val;
		manhattan = val - 10 + players.size();
	}

	/**
	 * Sets the minimum maze and manhattan distance.
	 * 
	 * @param maze
	 *            - the minimum maze distance
	 * @param manhattan
	 *            - the minimum manhattan distance
	 * @return <code>true</code> if successful
	 */
	public boolean setMinDistance(int maze, int manhattan) {
		int val = (int) Math.sqrt(8.0 / 5 * iSize * jSize / players.size());
		if (maze >= 0 && maze <= val && manhattan >= 0 && manhattan <= val - 2) {
			this.maze = maze;
			this.manhattan = manhattan;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the minimum maze and manhattan distances.
	 * 
	 * @return an array with the minimum maze and manhattan distances
	 */
	public int[] getMinDistance() {
		return new int[] { maze, manhattan };
	}

	/**
	 * Sets the tick rate of the game.
	 * 
	 * @param tick
	 *            - the tick speed
	 * @return <code>true</code> if successful
	 */
	public boolean setTick(int tick) {
		if (tick >= minTick && tick <= maxTick) {
			this.tick = tick;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the tick speed.
	 * 
	 * @return the tick speed
	 */
	public int getTick() {
		return tick;
	}

	/**
	 * Gets the number of players.
	 * 
	 * @return the number of players currently in the list
	 */
	public int numPlayers() {
		return players.size();
	}

	/**
	 * Adds a player to the game.
	 * 
	 * @param p
	 *            - the new player
	 * @return the player number of the player or 0 if either the player limit had
	 *         been reached or if the player has already been added
	 */
	public int addPlayer(Player p) {
		if (!players.contains(p) && players.size() < maxPlayers) {
			players.add(p);
			updateMinDistance();
			return players.size();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the player number of a player.
	 * 
	 * @param p
	 *            - the player whose index is being searched
	 * @return the player number or 0 if the player has not been added
	 */
	public int getPlayerNumber(Player p) {
		return players.indexOf(p) + 1;
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param p
	 *            - the player to be removed
	 */
	public void removePlayer(Player p) {
		int i = players.indexOf(p);
		while (i != -1) {
			players.remove(i);
			i = players.indexOf(p);
		}
	}

	/**
	 * Gets the number of spectators.
	 * 
	 * @return the number of spectators currently in the list
	 */
	public int numSpectators() {
		return spectators.size();
	}

	/**
	 * Adds a spectator to the game.
	 * 
	 * @param s
	 *            - the new spectator
	 * @return the number of spectators watching the game or 0 if either the
	 *         spectator limit had been reached or if the spectator was already
	 *         added
	 */
	public int addSpectator(Spectator s) {
		if (!spectators.contains(s) && spectators.size() < maxSpectators) {
			spectators.add(s);
			return spectators.size();
		} else {
			return 0;
		}
	}

	/**
	 * Removes a spectator from the game.
	 * 
	 * @param s
	 *            - the spectator to be removed
	 */
	public void removeSpectator(Spectator s) {
		int i = spectators.indexOf(s);
		while (i != -1) {
			spectators.remove(i);
			i = spectators.indexOf(s);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		/*
		 * test code for a custom game from before game start class was created
		 * 
		 * Space[][] board = new Space[18][18]; for (int i = 0; i < 18; i++) { for (int
		 * j = 0; j < 18; j++) { if (i > 4 && i < 13 && j > 4 && j < 13) { if (i < 9) {
		 * board[i][j] = new Territory(1, 1); } else { board[i][j] = new Territory(2,
		 * 1); } } else { board[i][j] = new Space(true); } } } board[5][9] = new City(1,
		 * 30, true); board[12][8] = new City(2, 30, true); ArrayDeque<Move>[] queues =
		 * new ArrayDeque[] { new ArrayDeque<Move>(), new ArrayDeque<Move>() }; Player[]
		 * players = new Player[] { new PlayerGraphics(18, 18, 2, 1, queues[0]), new
		 * PlayerGraphics(18, 18, 2, 2, queues[1]) }; GameBoard g = new GameBoard(board,
		 * players, queues, new Spectator[] { new SpectatorGraphics(18, 18, 2) }, 0.1,
		 * 0.1, 9, 7);
		 */

		/*
		 * test code for a standard game from before game start class was created
		 * 
		 * GameBoard g = new GameBoard(18, 18, 0.12, 0.04, 2, 18, 9); GameBoard g = new
		 * GameBoard(18, 18, 0.12, 0.04, 8, 8, 6); int x = 200; while (x-- > 0) {//
		 * TODO: for debugging purposes only, remove when finished g.cycle(); } while
		 * (g.gameEnd() == 0) { long before = System.nanoTime(); g.cycle(); long time =
		 * (long) (500 - (System.nanoTime() - before) / 1e6);
		 * Thread.sleep(Math.max(time, minWaitTime)); }
		 */

		int i = 15;
		int j = 15;
		int kingdoms = 8;
		GameStart start = new GameStart();
		System.out.println(start.setSize(i, j));
		for (int x = 1; x <= kingdoms; x++) {
			start.addPlayer(new PlayerGraphics(i, j, kingdoms, x));
			System.out.println(start.getSize()[0] + " " + start.getSize()[1] + " " + start.getProportion()[0] + " "
					+ start.getProportion()[1] + " " + start.getMinDistance()[0] + " " + start.getMinDistance()[1]);
		}
		start.addSpectator(new SpectatorGraphics(i, j, kingdoms));
		start.startGame();
	}

	/*
	 * Useful Information
	 * 
	 * For having three digits visible, set button size to at least 55 (18x18 fits)
	 * 
	 * For having four digits visible, set button size to at least 62 (16x16 fits)
	 * 
	 * Currently button size is determined by 992.0/iSize (in PlayerGraphics)
	 * 
	 * GameBoard constructor, for reference, is (i,j,proportion mountains,
	 * proportion cities, kingdoms, min maze distance, min manhattan distance)
	 * 
	 * For Space[][] to Viewed[][]: ................................................
	 * Mountain - (known,true,0,0,false,false) .....................................
	 * Territory - (known,false,type,troops,false,false) ...........................
	 * Known City - (true,false,type,troops,true,false) ............................
	 * Unknown City - (false,true,0,0,false,false) .................................
	 * King - (true,false,type,troops,false,true) ..................................
	 * Unknown King - (false,true,0,0,false,false) .................................
	 * 
	 * Possible Fitness: ...........................................................
	 * Game Ranking (1st place, 2nd place, so on) ..................................
	 * Territory and Army ..........................................................
	 * Number of Explored Mountains ................................................
	 * Number of Explored Spaces ...................................................
	 * Number of Cities/Kings Captured .............................................
	 * Time Alive ..................................................................
	 */

	/*
	 * TODO: BUGS AND CHANGES (in chronological order, with earliest first)
	 * 
	 * The board allowing a move into a mountain that is known. DONE (gameboard not
	 * returning correct viewed board, corrected code)
	 * 
	 * Moves not being put into queues when holding down or quickly pressing
	 * movement keys. DONE (queue.push instead of queue.offer which wasn't noticed
	 * because of arraydeque, replaced with queue.offer)
	 * 
	 * E and Q not returning the selected to the previous spot. DONE (didn't reset
	 * selected square, added proper code)
	 * 
	 * Enemy territories not being converted when the king is captured. DONE (king
	 * was setting isKing to false before it was called, changed order)
	 * 
	 * Selected spot not being unselected when the territory or king is captured.
	 * DONE (?order?, code rework)
	 * 
	 * Selected spot becoming an invalid spot when keys are pressed quickly. DONE
	 * (???, code rework)
	 * 
	 * Icons conflicting with the text. DONE (???, code rework)
	 * 
	 * Cities starting with 40 troops rather than a random number between 40 and 45
	 * inclusive. FDONEIXED (?Math.random() vs r.nextInt*()?, moved code to
	 * gameGenerator and changed from Math.random() to r.nextInt())
	 * 
	 * Spectators seeing kings as cities on the board. DONE (icon assignment order,
	 * changed order)
	 * 
	 * Easier game initialization. DONE
	 * 
	 * Repetitive code for spectators and players. DONE (N/A, used inheritance and
	 * reworked code)
	 * 
	 * Changed code to have players initialize queues; the game board gets them with
	 * the getQueue() method defined in Player. DONE
	 * 
	 * New game starting options in the GameStart class allowing for changing
	 * settings, adding players and spectators, starting the game, etc. DONE
	 * 
	 * The minimum maze and manhattan distance being swapped. DONE (Order in
	 * constructor inputs, reordering of said inputs)
	 * 
	 * More/better code documentation. IN_PROGRESS
	 * 
	 * Creating custom game boards in the GameStart class or elsewhere. IN_PROGRESS
	 */
}
