package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class StatGraphics extends JPanel {
	private final int kingdoms;
	private final Color[] colors;
	private JButton[][] statButtons;
	private int[][] armyState;

	public StatGraphics(int kingdoms, Color[] colors) {
		this.kingdoms = kingdoms;
		this.colors = colors;
		initializeButtons();
		initializePanel();

	}

	public void update(int[][] armyState) {
		this.armyState = armyState;
		for (int i = 0; i < armyState.length; i++) {
			for (int j = 0; j < 2; j++) {
				statButtons[i][j].setText(Integer.toString(armyState[i][j]));
			}
		}
	}

	private void initializeButtons() {
		statButtons = new JButton[kingdoms][2];
		Dimension d = new Dimension(60, 0);
		for (int x = 0; x < kingdoms; x++) {
			for (int y = 0; y < 2; y++) {
				statButtons[x][y] = new JButton();
				statButtons[x][y].setBackground(colors[x + 2]);
				statButtons[x][y].setPreferredSize(d);
			}
		}
	}

	private void initializePanel() {
		setLayout(new GridLayout(kingdoms, 2));
		for (JButton[] r : statButtons) {
			for (JButton button : r) {
				add(button);
			}
		}
		setVisible(true);
	}
}
