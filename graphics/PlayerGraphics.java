package graphics;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import system.Move;
import system.Player;
import system.Viewed;

/**
 * The human player interface for generals.io.
 * 
 * @author Axel Li
 */
public class PlayerGraphics extends SpectatorGraphics implements Player {
	protected final int playerNumber;
	protected ArrayDeque<Move> queue;

	/**
	 * Creates a new player interface.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param kingdoms
	 *            - the number of kingdoms
	 * @param playerNumber
	 *            - the player number of this player
	 * @param queue
	 *            - the queue for Move objects
	 */
	public PlayerGraphics(int i, int j, int kingdoms, int playerNumber, ArrayDeque<Move> queue) {
		super(i, j, kingdoms, queue, playerNumber);
		this.playerNumber = playerNumber;
		this.queue = queue;
	}

	public void failedMove(Move failing) {
		board.failedMove(failing);
	}

	public void reset(int winner) {

	}
}