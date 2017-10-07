package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import system.Move;
import system.Viewed;

/**
 * The board graphics for the human player interface.
 * 
 * @author Axel Li
 */
public class BoardGraphics extends JPanel {
	private static final long serialVersionUID = 1L;
	private final int iSize;
	private final int jSize;
	private final int boardButtonSize;
	private final int playerNumber;
	private final Color[] colors;
	private final Icon[] icons = new Icon[] { null, createImageIcon("mountain.png", "mountain"),
			createImageIcon("city.png", "city"), createImageIcon("king.png", "king") };
	private ToggleButton[][] boardButtons;
	private JLabel[][] boardText;
	private Viewed[][] board;
	private ArrayDeque<Move> queue;
	private Detector detect;
	private boolean spectator;

	/**
	 * Creates board graphics for the player interface.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param boardButtonSize
	 *            - the size of the buttons
	 * @param playerNumber
	 *            - the player number of this player
	 * @param colors
	 *            - the color scheme for the players
	 * @param queue
	 *            - the queue for Move
	 */
	public BoardGraphics(int i, int j, int boardButtonSize, int playerNumber, Color[] colors, ArrayDeque<Move> queue,
			boolean spectator) {
		this.iSize = i;
		this.jSize = j;
		this.boardButtonSize = boardButtonSize;
		this.playerNumber = playerNumber;
		this.colors = colors;
		this.queue = queue;
		this.spectator = spectator;
		initializeButtons();
		initializePanel();
		update(initialView());
	}

	/**
	 * Updates the board.
	 * 
	 * @param board
	 *            - the current viewable board
	 */
	public void update(Viewed[][] board) {
		this.board = board;
		for (int x = 0; x < iSize; x++) {
			for (int y = 0; y < jSize; y++) {
				if (board[x][y].known) {
					boardButtons[x][y].setBackground(colors[board[x][y].type + 1]);
				} else {
					boardButtons[x][y].setBackground(colors[0]);
				}
				if (!board[x][y].known || board[x][y].mountain || board[x][y].troops == 0) {
					boardText[x][y].setText("");
				} else {
					boardText[x][y].setText(Integer.toString(board[x][y].troops));
				}
				if (board[x][y].mountain) {
					boardButtons[x][y].setIcon(icons[1]);
				} else if (board[x][y].king) {
					boardButtons[x][y].setIcon(icons[3]);
				} else if (board[x][y].city) {
					boardButtons[x][y].setIcon(icons[2]);
				} else {
					boardButtons[x][y].setIcon(icons[0]);
				}
			}
		}
	}

	/**
	 * Resets the selected button.
	 * 
	 * @param failing
	 *            - the unsuccessful move
	 */
	public void failedMove(Move failing) {
		detect.failed(failing);
	}

	/**
	 * Pulls an ImageIcon from a file.
	 * 
	 * @param path
	 *            - the file name
	 * @param description
	 *            - the icon description
	 * @return the icon if found, <code>null</code> if the path is invalid
	 */
	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Initializes the button grid.
	 */
	private void initializeButtons() {
		boardButtons = new ToggleButton[iSize][jSize];
		boardText = new JLabel[iSize][jSize];
		Dimension d = new Dimension(boardButtonSize, boardButtonSize);
		for (int x = 0; x < iSize; x++) {
			for (int y = 0; y < jSize; y++) {
				boardText[x][y] = new JLabel(icons[0], JLabel.CENTER);
				boardText[x][y].setAlignmentX(JLabel.CENTER_ALIGNMENT);
				boardButtons[x][y] = new ToggleButton(x, y);
				boardButtons[x][y].add(boardText[x][y]);
				boardButtons[x][y].setPreferredSize(d);
			}
		}
		if (!spectator) {
			detect = new Detector(queue);
			for (int x = 0; x < iSize; x++) {
				for (int y = 0; y < jSize; y++) {
					boardButtons[x][y].addActionListener(detect);
					boardButtons[x][y].addKeyListener(detect);
				}
			}
		}
	}

	/**
	 * Initializes the panel.
	 */
	private void initializePanel() {
		setLayout(new GridLayout(iSize, jSize));
		for (ToggleButton[] r : boardButtons) {
			for (ToggleButton button : r) {
				add(button);
			}
		}
		setPreferredSize(new Dimension(jSize * boardButtonSize, iSize * boardButtonSize));
		setVisible(true);
	}

	/**
	 * Provides a initial empty board to prevent a null pointer exception.
	 * 
	 * @return a initialized empty board
	 */
	private Viewed[][] initialView() {
		Viewed[][] r = new Viewed[iSize][jSize];
		for (int i = 0; i < iSize; i++) {
			for (int j = 0; j < jSize; j++) {
				r[i][j] = new Viewed(false, false, 0, 0, false, false);
			}
		}
		return r;
	}

	/**
	 * A custom listener for detecting events in the board.
	 * 
	 * @author Axel Li
	 */
	private class Detector implements ActionListener, KeyListener {

		private ToggleButton selected;
		private ToggleButton previous;
		private ArrayDeque<Move> queue;
		private boolean all;

		/**
		 * Creates a detector for the board
		 * 
		 * @param queue
		 *            - the queue for moves
		 */
		public Detector(ArrayDeque<Move> queue) {
			this.queue = queue;
			all = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ToggleButton b = (ToggleButton) e.getSource();
			if (b == selected) {
				if (all) {
					all = false;
					selected.setSelected(true);
				} else {
					selected.setSelected(false);
					selected = null;
				}
			} else {
				if (playerNumber == board[b.i][b.j].type) {
					if (selected != null) {
						selected.setSelected(false);
					}
					selected = b;
					all = true;
				} else {
					b.setSelected(false);
				}
			}
		}

		/**
		 * Updates the detector about a failed move.
		 * 
		 * @param failing
		 *            - the failed move
		 */
		private void failed(Move failing) {
			if (board[failing.startI][failing.startJ].type == playerNumber) {
				previous = boardButtons[failing.startI][failing.startJ];
			}
		}

		/**
		 * Attempts to make a move from the selected button.
		 * 
		 * @param iChange
		 *            - the i change
		 * @param jChange
		 *            - the j change
		 */
		private void attemptMove(int iChange, int jChange) {
			if (selected != null && Math.abs(iChange) + Math.abs(jChange) == 1) {
				int newI = selected.i + iChange;
				int newJ = selected.j + jChange;
				int oldI = selected.i;
				int oldJ = selected.j;
				if (newI >= 0 && newI < iSize && newJ >= 0 && newJ < jSize
						&& !(board[newI][newJ].known && board[newI][newJ].mountain)) {
					selected.setSelected(false);
					queue.offer(new Move(oldI, oldJ, newI, newJ, all));
					selected = boardButtons[newI][newJ];
					selected.setSelected(true);
				}
			}
		}

		/**
		 * Moves the selected button without putting a move into the queue.
		 * 
		 * @param iChange
		 *            - the i change
		 * @param jChange
		 *            - the j change
		 */
		private void moveSelected(int iChange, int jChange) {
			if (selected != null && Math.abs(iChange) + Math.abs(jChange) == 1) {
				int newI = selected.i + iChange;
				int newJ = selected.j + jChange;
				if (newI >= 0 && newI < iSize && newJ >= 0 && newJ < jSize) {
					selected.setSelected(false);
					selected = boardButtons[newI][newJ];
					selected.setSelected(true);
				}
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				if (e.isShiftDown()) {
					moveSelected(-1, 0);
				} else {
					attemptMove(-1, 0);
				}
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				if (e.isShiftDown()) {
					moveSelected(1, 0);
				} else {
					attemptMove(1, 0);
				}
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				if (e.isShiftDown()) {
					moveSelected(0, -1);
				} else {
					attemptMove(0, -1);
				}
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				if (e.isShiftDown()) {
					moveSelected(0, 1);
				} else {
					attemptMove(0, 1);
				}
				break;
			case KeyEvent.VK_E:
				if (!queue.isEmpty()) {
					if (selected != null) {
						selected.setSelected(false);
					}
					Move x = queue.peekLast();
					selected = boardButtons[x.startI][x.startJ];
					selected.setSelected(true);
				}
				queue.pollLast();
				break;
			case KeyEvent.VK_Q:
				if (!queue.isEmpty()) {
					if (selected != null) {
						selected.setSelected(false);
					}
					Move y = queue.peekFirst();
					selected = boardButtons[y.startI][y.startJ];
					selected.setSelected(true);
				}
				queue.clear();
				break;
			case KeyEvent.VK_F:
				all = !all;
				break;
			case KeyEvent.VK_R:
				if (selected != null && previous != null && board[previous.i][previous.j].type == playerNumber) {
					selected.setSelected(false);
					selected = previous;
					previous.setSelected(true);
					previous = null;
				}
				break;
			default:
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}
	}
}