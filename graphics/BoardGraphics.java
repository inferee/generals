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

public class BoardGraphics extends JPanel implements ActionListener, KeyListener {
	private final int i;
	private final int j;
	private final int boardButtonSize;
	private final int playerNumber;
	private final Color[] colors;
	private final Icon[] icons = new Icon[] { null, null, null, null };
	private ToggleButton[][] boardButtons;
	private Viewed[][] board;
	private ToggleButton selected;
	private ArrayDeque<Move> queue;
	private boolean all;

	public BoardGraphics(int i, int j, int boardButtonSize, int playerNumber, Color[] colors, ArrayDeque<Move> queue) {
		this.i = i;
		this.j = j;
		this.boardButtonSize = boardButtonSize;
		this.playerNumber = playerNumber;
		this.colors = colors;
		this.queue = queue;
		all = true;
		initializeButtons();
		initializePanel();
	}

	/*
	 * TODO: BUGS The board allowing a move into a mountain that is known. SOLVED
	 * (gameboard not returning correct viewed board) Moves not being put into
	 * queues when holding down or quickly pressing movement keys. SOLVED
	 * (queue.push instead of queue.offer, didn't notice because of arraydeque) E
	 * and Q not returning the selected to the previous spot SOLVED (didn't reset selected square)
	 * 
	 * 
	 */

	public void update(Viewed[][] board) {
		this.board = board;
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				if (board[x][y].isKnown()) {
					boardButtons[x][y].setBackground(colors[board[x][y].getType() + 1]);
				} else {
					boardButtons[x][y].setBackground(colors[0]);
				}
				if (board[x][y].isMountain()) {
					boardButtons[x][y].setIcon(icons[1]);
				} else if (board[x][y].isCity()) {
					boardButtons[x][y].setIcon(icons[2]);
				} else if (board[x][y].isKing()) {
					boardButtons[x][y].setIcon(icons[3]);
				} else {
					boardButtons[x][y].setIcon(icons[0]);
				}
				if (!board[x][y].isKnown() || board[x][y].isMountain() || board[x][y].getTroops() == 0) {
					boardButtons[x][y].setText("");
				} else {
					boardButtons[x][y].setText(Integer.toString(board[x][y].getTroops()));
				}
			}
		}
	}

	public void failedMove(Move failing) {
		if (selected != null) {
			selected.setSelected(false);
		}
		selected = boardButtons[failing.startI][failing.startJ];
		selected.setSelected(true);
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
			if (playerNumber == board[b.i][b.j].getType()) {
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

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			if (selected != null && selected.i > 0) {
				if (!(board[selected.i - 1][selected.j].isKnown() && board[selected.i - 1][selected.j].isMountain())) {
					selected.setSelected(false);
					System.out.println(queue.size());
					queue.offer(new Move(selected.i, selected.j, selected.i - 1, selected.j, all));
					System.out.println(queue.size());
					selected = boardButtons[selected.i - 1][selected.j];
					selected.setSelected(true);
				}
			}
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			if (selected != null && selected.i < i - 1) {
				if (!(board[selected.i + 1][selected.j].isKnown() && board[selected.i + 1][selected.j].isMountain())) {
					selected.setSelected(false);
					queue.offer(new Move(selected.i, selected.j, selected.i + 1, selected.j, all));
					selected = boardButtons[selected.i + 1][selected.j];
					selected.setSelected(true);
				}
			}
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			if (selected != null && selected.j > 0) {
				if (!(board[selected.i][selected.j - 1].isKnown() && board[selected.i][selected.j - 1].isMountain())) {
					selected.setSelected(false);
					queue.offer(new Move(selected.i, selected.j, selected.i, selected.j - 1, all));
					selected = boardButtons[selected.i][selected.j - 1];
					selected.setSelected(true);
				}
			}
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			if (selected != null && selected.j < j - 1) {
				if (!(board[selected.i][selected.j + 1].isKnown() && board[selected.i][selected.j + 1].isMountain())) {
					selected.setSelected(false);
					queue.offer(new Move(selected.i, selected.j, selected.i, selected.j + 1, all));
					selected = boardButtons[selected.i][selected.j + 1];
					selected.setSelected(true);
				}
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
		default:
		}
	}

	private void initializeButtons() {
		boardButtons = new ToggleButton[i][j];
		Dimension d = new Dimension(boardButtonSize, boardButtonSize);
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				boardButtons[x][y] = new ToggleButton(x, y);
				boardButtons[x][y].setPreferredSize(d);
				boardButtons[x][y].addActionListener(this);
				boardButtons[x][y].addKeyListener(this);
			}
		}
	}

	private void initializePanel() {
		setLayout(new GridLayout(i, j));
		for (ToggleButton[] r : boardButtons) {
			for (ToggleButton button : r) {
				add(button);
			}
		}
		setPreferredSize(new Dimension(j * boardButtonSize, i * boardButtonSize));
		setVisible(true);
	}

	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		GameBoard g = new GameBoard(20, 20, 40, 10, 2, 7, 5);
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