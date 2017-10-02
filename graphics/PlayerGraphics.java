package graphics;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import system.GameBoard;
import system.Move;
import system.Player;
import system.Viewed;

/**
 * The human player interface for generals.io.
 * 
 * @author Axel Li
 */
public class PlayerGraphics implements Player {
	private final int iSize;
	private final int jSize;
	private final int kingdoms;
	private final int playerNumber;
	private Color[] colors = new Color[] { Color.DARK_GRAY, Color.GRAY, Color.RED, Color.BLUE, Color.YELLOW,
			Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.WHITE };
	private JFrame frame;
	private BoardGraphics board;
	private StatGraphics stats;
	private ArrayDeque<Move> queue;

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
		this.iSize = i;
		this.jSize = j;
		this.kingdoms = kingdoms;
		this.playerNumber = playerNumber;
		colors = Arrays.copyOf(colors, kingdoms + 2);
		this.queue = queue;
		initializeGraphics();
	}

	/**
	 * Initializes all graphics.
	 */
	private void initializeGraphics() {
		board = new BoardGraphics(iSize, jSize, 55, playerNumber, colors, queue);
		stats = new StatGraphics(kingdoms, colors, 80);
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.add(board);
		container.add(stats);
		frame = new JFrame("generals.io");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.add(container);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void update(Viewed[][] b, int[][] armyState) {
		board.update(b);
		stats.update(armyState);
	}

	public void failedMove(Move failing) {
		board.failedMove(failing);
	}

	public void reset(int winner) {

	}
}