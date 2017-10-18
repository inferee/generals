package players;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import system.Move;
import system.Spectator;
import system.Viewed;

/**
 * The human player interface for generals.io.
 * 
 * @author Axel Li
 */
public class SpectatorGraphics implements Spectator {
	protected final int iSize;
	protected final int jSize;
	protected final int kingdoms;
	protected Color[] colors = new Color[] { new Color(100, 100, 100), Color.GRAY, Color.RED, Color.BLUE, Color.YELLOW,
			Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.WHITE };
	protected JFrame frame;
	protected BoardGraphics board;
	protected StatGraphics stats;

	/**
	 * Creates a new spectator interface.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param kingdoms
	 *            - the number of kingdoms
	 */
	public SpectatorGraphics(int i, int j, int kingdoms) {
		this.iSize = i;
		this.jSize = j;
		this.kingdoms = kingdoms;
		colors = Arrays.copyOf(colors, kingdoms + 2);
		initializeGraphics();
	}

	/**
	 * Creates a new spectator interface, used for the super method in
	 * PlayerGraphics.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param kingdoms
	 *            - the number of kingdoms
	 * @param queue
	 *            - the queue for moves
	 * @param playerNumber
	 *            - the player number
	 */
	protected SpectatorGraphics(int i, int j, int kingdoms, int playerNumber) {
		this.iSize = i;
		this.jSize = j;
		this.kingdoms = kingdoms;
		this.board = new BoardGraphics(iSize, jSize, (int) (992.0 / iSize), playerNumber, colors,
				new ArrayDeque<Move>(), false);
		initializeGraphics();
	}

	/**
	 * Initializes all graphics.
	 */
	private void initializeGraphics() {
		if (board == null) {
			board = new BoardGraphics(iSize, jSize, (int) (992.0 / iSize), 0, colors, null, true);
		}
		stats = new StatGraphics(kingdoms, colors, 80);
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		container.add(board);
		container.add(stats);
		frame = new JFrame("generals.io");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(container);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void update(Viewed[][] b, int[][] armyState) {
		board.update(b);
		stats.update(armyState);
	}
}