package system;

public class Move {
	public int startI;
	public int startJ;
	public int endI;
	public int endJ;
	public boolean all;

	public Move(int startI, int startJ, int endI, int endJ, boolean all) {
		this.startI = startI;
		this.startJ = startJ;
		this.endI = endI;
		this.endJ = endJ;
		this.all = all;
	}
}
