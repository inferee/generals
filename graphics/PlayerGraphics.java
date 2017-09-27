package graphics;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import system.GameBoard;
import system.Move;
import system.Player;
import system.Viewed;

public class PlayerGraphics implements Player {
	private final int i;
	private final int j;
	private final int kingdoms;
	private final int playerNumber;
	private Color[] colors = new Color[] { Color.DARK_GRAY, Color.GRAY, Color.RED, Color.BLUE, Color.YELLOW,
			Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.WHITE };
	private JFrame frame;
	private BoardGraphics board;
	private StatGraphics stats;
	private ArrayDeque<Move> queue;

	public PlayerGraphics(int i, int j, int kingdoms, int playerNumber, ArrayDeque<Move> queue) {
		this.i = i;
		this.j = j;
		this.kingdoms = kingdoms;
		this.playerNumber = playerNumber;
		colors = Arrays.copyOf(colors, kingdoms + 2);
		this.queue = queue;
		initializeGraphics();
	}

	private void initializeGraphics() {
		board = new BoardGraphics(i, j, 55, playerNumber, colors, queue);
		stats = new StatGraphics(kingdoms, colors);
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

	@Override
	public void setQueue(ArrayDeque<Move> queue) {
		this.queue = queue;
	}

	@Override
	public void update(Viewed[][] b, int[][] armyState) {
		board.update(b);
		stats.update(armyState);
	}

	@Override
	public void failedMove(Move failing) {
		board.failedMove(failing);
	}

	@Override
	public void reset() {

	}
}