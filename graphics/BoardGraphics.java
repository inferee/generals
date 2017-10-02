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
import javax.swing.JPanel;

import system.GameBoard;
import system.Move;
import system.Viewed;

/**
 * The board graphics for the human player interface.
 * 
 * @author Axel Li
 */
public class BoardGraphics extends JPanel implements ActionListener, KeyListener {
	private final int iSize;
	private final int jSize;
	private final int boardButtonSize;
	private final int playerNumber;
	private final Color[] colors;
	private final Icon[] icons = new Icon[] { null, null, null, null };
	private ToggleButton[][] boardButtons;
	private Viewed[][] board;
	private ToggleButton selected;
	private ToggleButton previous;
	private ArrayDeque<Move> queue;
	private boolean all;

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
	public BoardGraphics(int i, int j, int boardButtonSize, int playerNumber, Color[] colors, ArrayDeque<Move> queue) {
		this.iSize = i;
		this.jSize = j;
		this.boardButtonSize = boardButtonSize;
		this.playerNumber = playerNumber;
		this.colors = colors;
		this.queue = queue;
		all = true;
		initializeButtons();
		initializePanel();
	}

	/*
	 * TODO: BUGS
	 * 
	 * The board allowing a move into a mountain that is known. SOLVED (gameboard
	 * not returning correct viewed board, corrected code)
	 * 
	 * Moves not being put into queues when holding down or quickly pressing
	 * movement keys. SOLVED (queue.push instead of queue.offer which wasn't noticed
	 * because of arraydeque, replaced with queue.offer)
	 * 
	 * E and Q not returning the selected to the previous spot. SOLVED (didn't reset
	 * selected square, added proper code)
	 * 
	 * Enemy territories not being converted when the king is captured. SOLVED (king
	 * was setting isKing to false before it was called, changed order)
	 * 
	 * Selected spot not being unselected when the territory or king is captured.
	 * 
	 * Selected spot becoming an invalid spot when keys are pressed quickly. SOLVED
	 * (???, code rework)
	 */
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
					boardButtons[x][y].setText("");
				} else {
					boardButtons[x][y].setText(Integer.toString(board[x][y].troops));
				}
				if (board[x][y].mountain) {
					boardButtons[x][y].setIcon(icons[1]);
					boardButtons[x][y].setText("X");
				} else if (board[x][y].city) {
					boardButtons[x][y].setIcon(icons[2]);
				} else if (board[x][y].king) {
					boardButtons[x][y].setIcon(icons[3]);
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
		if (board[failing.startI][failing.startJ].type == playerNumber) {
			previous = boardButtons[failing.startI][failing.startJ];
		}
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
	 * Attempts to make a move
	 * 
	 * @param iChange
	 *            - the change in i position
	 * @param jChange
	 *            - the change in j position
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
	 * Moves the selected tile without moving the troops
	 * 
	 * @param iChange
	 *            - the change in i position
	 * @param jChange
	 *            - the change in j position
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

	/**
	 * Initializes the button grid.
	 */
	private void initializeButtons() {
		boardButtons = new ToggleButton[iSize][jSize];
		Dimension d = new Dimension(boardButtonSize, boardButtonSize);
		for (int x = 0; x < iSize; x++) {
			for (int y = 0; y < jSize; y++) {
				boardButtons[x][y] = new ToggleButton(x, y);
				boardButtons[x][y].setPreferredSize(d);
				boardButtons[x][y].addActionListener(this);
				boardButtons[x][y].addKeyListener(this);
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

	public static void main(String[] args) throws InterruptedException {
		GameBoard g = new GameBoard(18, 18, 40, 10, 2, 7, 5);
		long before = System.nanoTime();
		long time = 0;
		long tick = 10;
		int x = 200;
		while (x-- > 0) {
			g.cycle();
			time = (long) (tick - (System.nanoTime() - before) / 1e6);
			Thread.sleep(Math.max(time, 10));
			before = System.nanoTime();
		}
		tick = 500;
		while (true) {
			g.cycle();
			time = (long) (tick - (System.nanoTime() - before) / 1e6);
			Thread.sleep(Math.max(time, 10));
			before = System.nanoTime();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}