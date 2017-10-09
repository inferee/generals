package system;

import java.util.ArrayDeque;

import graphics.PlayerGraphics;
import graphics.SpectatorGraphics;

public class GameStart {

	public static void main(String[] args) throws InterruptedException {
		// Space[][] board = new Space[18][18];
		// for (int i = 0; i < 18; i++) {
		// for (int j = 0; j < 18; j++) {
		// if (i > 4 && i < 13 && j > 4 && j < 13) {
		// if (i < 9) {
		// board[i][j] = new Territory(1, 1);
		// } else {
		// board[i][j] = new Territory(2, 1);
		// }
		// } else {
		// board[i][j] = new Space(true);
		// }
		// }
		// }
		// board[5][9] = new City(1, 30, true);
		// board[12][8] = new City(2, 30, true);
		// ArrayDeque<Move>[] queues = new ArrayDeque[] { new ArrayDeque<Move>(), new
		// ArrayDeque<Move>() };
		// Player[] players = new Player[] { new PlayerGraphics(18, 18, 2, 1,
		// queues[0]),
		// new PlayerGraphics(18, 18, 2, 2, queues[1]) };
		// GameBoard g = new GameBoard(board, players, queues, new Spectator[] { new
		// SpectatorGraphics(18, 18, 2) }, 0.1,
		// 0.1, 2, 9, 7);
		// GameBoard g = new GameBoard(18, 18, 0.1, 0.04, 2, 18, 9);
		GameBoard g = new GameBoard(18, 18, 0.1, 0.04, 8, 8, 6);
		long before = System.nanoTime();
		long time = 0;
		long tick = 0;
		long minTick = 0;
		int x = 200;
		while (x-- > 0) {// quickly cycles 200 times, used for debugging
			g.cycle();
			time = (long) (tick - (System.nanoTime() - before) / 1e6);
			Thread.sleep(Math.max(time, minTick));
			before = System.nanoTime();
		}
		tick = 500;
		minTick = 10;
		while (true) {
			g.cycle();
			time = (long) (tick - (System.nanoTime() - before) / 1e6);
			Thread.sleep(Math.max(time, minTick));
			before = System.nanoTime();
		}
	}
	/*
	 * Information
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
	 * TODO: BUGS AND CHANGES
	 * 
	 * The board allowing a move into a mountain that is known. FIXED (gameboard not
	 * returning correct viewed board, corrected code)
	 * 
	 * Moves not being put into queues when holding down or quickly pressing
	 * movement keys. FIXED (queue.push instead of queue.offer which wasn't noticed
	 * because of arraydeque, replaced with queue.offer)
	 * 
	 * E and Q not returning the selected to the previous spot. FIXED (didn't reset
	 * selected square, added proper code)
	 * 
	 * Enemy territories not being converted when the king is captured. FIXED (king
	 * was setting isKing to false before it was called, changed order)
	 * 
	 * Selected spot not being unselected when the territory or king is captured.
	 * 
	 * Selected spot becoming an invalid spot when keys are pressed quickly. FIXED
	 * (???, code rework)
	 * 
	 * Icons conflicting with the text. FIXED (???, code rework)
	 * 
	 * Cities starting with 40 troops rather than a random number between 40 and 45
	 * inclusive. FIXED (?Math.random() vs r.nextInt*()?, moved code to
	 * gameGenerator and changed from Math.random() to r.nextInt())
	 * 
	 * Spectators seeing kings as cities on the board. FIXED (icon assignment order,
	 * changed order)
	 * 
	 * Repetitive code for spectators and players. FIXED (N/A, used inheritance and
	 * reworked code)
	 */
}
