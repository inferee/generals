package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The game statistics for the human player interface.
 * 
 * @author Axel Li
 */
public class StatGraphics extends JPanel {
	private final int kingdoms;
	private final Color[] colors;
	private final int buttonWidth;
	private JButton[][] statButtons;
	private int[][] armyState;

	/**
	 * Creates stat graphics for the player interface
	 * 
	 * @param kingdoms
	 *            - the number of kingdoms
	 * @param colors
	 *            - the color scheme for the players
	 */
	public StatGraphics(int kingdoms, Color[] colors, int buttonWidth) {
		this.kingdoms = kingdoms;
		this.colors = colors;
		this.buttonWidth = buttonWidth;
		initializeButtons();
		initializePanel();

	}

	/**
	 * Updates the stats.
	 * 
	 * @param armyState
	 *            - the new army state for all players
	 */
	public void update(int[][] armyState) {
		this.armyState = armyState;
		for (int i = 0; i < armyState.length; i++) {
			for (int j = 0; j < 2; j++) {
				statButtons[i][j].setText(Integer.toString(armyState[i][j]));
			}
		}
	}

	/**
	 * Initializes all buttons.
	 */
	private void initializeButtons() {
		statButtons = new JButton[kingdoms][2];
		Dimension d = new Dimension(buttonWidth, 0);
		for (int x = 0; x < kingdoms; x++) {
			for (int y = 0; y < 2; y++) {
				statButtons[x][y] = new JButton();
				statButtons[x][y].setBackground(colors[x + 2]);
				statButtons[x][y].setPreferredSize(d);
			}
		}
	}

	/**
	 * Initializes the panel.
	 */
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
