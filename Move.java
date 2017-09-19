package system;

public class Move {
	public int i;
	public int j;
	public boolean ij;// true->i
	public boolean pos;// true->positive
	public boolean all;// true->move all

	public Move(int i, int j, boolean ij, boolean pos, boolean all) {
		this.i = i;
		this.j = j;
		this.ij = ij;
		this.pos = pos;
		this.all = all;
	}
}
