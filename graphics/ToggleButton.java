package graphics;

import javax.swing.JToggleButton;

/**
 * Custom toggle buttons for the human player interface.
 * 
 * @author Axel Li
 */
public class ToggleButton extends JToggleButton {
	public final int i;
	public final int j;

	/**
	 * Creates a new JToggleButton with position.
	 * 
	 * @param i
	 *            - the i position
	 * @param j
	 *            - the j position
	 */
	public ToggleButton(int i, int j) {
		this.i = i;
		this.j = j;
	}
}
