package system;

public interface Occupiable {
	public void reinforce();

	public int move();

	public int split();

	public void attacked(int type, int troops);
	
	public void updateType(int type);

	public int getType();

	public int getTroops();
}
