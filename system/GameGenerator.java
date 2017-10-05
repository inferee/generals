package system;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

/**
 * A game board generator.
 * 
 * @author Axel Li
 */
public class GameGenerator {
	private Random r;
	private int i;
	private int j;
	private int mountains;
	private int cities;
	private int kingdoms;
	private int minManhattanDist;
	private int minMazeDist;
	private int[][] grid;
	private int[][] kings;
	private int[] playerSequence;

	/**
	 * Creates a new game board generator.
	 * 
	 * @param i
	 *            - the i size of the board
	 * @param j
	 *            - the j size of the board
	 * @param proportionMountains
	 *            - the proportion of mountains
	 * @param proportionCities
	 *            - the proportion of cities
	 * @param kingdoms
	 *            - the number of kingdoms
	 * @param minMazeDist
	 *            - the minimum maze distance between kingdoms
	 * @param minManhattanDist
	 *            - the minimum manhattan distance between kingdoms
	 */
	public GameGenerator(int i, int j, double proportionMountains, double proportionCities, int kingdoms,
			int minMazeDist, int minManhattanDist) {
		r = new Random();
		this.i = i;
		this.j = j;
		this.mountains = (int) (i * j * proportionMountains);
		this.cities = (int) (i * j * proportionCities);
		this.kingdoms = kingdoms;
		this.minManhattanDist = minManhattanDist;
		this.minMazeDist = minMazeDist;
		grid = new int[i][j];
		kings = new int[kingdoms][2];
		playerSequence = new int[kingdoms];
		initSequence();
	}

	/**
	 * Returns the grid.
	 * 
	 * @return the currently generated grid
	 */
	public int[][] getGrid() {
		return grid;
	}

	/**
	 * Randomly generates a player sequence and returns it.
	 * 
	 * @return the randomly generated sequence
	 */
	public int[] getSequence() {
		for (int i = 0; i < playerSequence.length; i++) {
			int x = i + r.nextInt(playerSequence.length - i);
			int hold = playerSequence[x];
			playerSequence[x] = playerSequence[i];
			playerSequence[i] = hold;
		}
		return playerSequence;
	}

	/**
	 * Returns the location of the kings.
	 * 
	 * @return the currently generated location of the kings
	 */
	public int[][] getKings() {
		return kings;
	}

	/**
	 * Attempts to create a new grid.
	 * 
	 * @param attempts
	 *            - the number of attempts before giving up
	 * @param print
	 *            - prints out the generated grid if <code> true </code> and if
	 *            successful
	 * @return <code> true </code> if successful
	 */
	public boolean create(int attempts, boolean print) {
		boolean unsuccessful = true;
		int[][] newKings = new int[kingdoms][2];
		int[][] newBoard = new int[i][j];
		while (attempts > 0 && unsuccessful) {
			unsuccessful = false;
			attempts--;
			newBoard = generate(mountains, cities);
			for (int x = 0; x < kingdoms; x++) {
				boolean duplicate = true;
				while (duplicate) {
					duplicate = false;
					newKings[x] = new int[] { r.nextInt(i), r.nextInt(j) };
					if (newBoard[newKings[x][0]][newKings[x][1]] != 0) {
						duplicate = true;
					} else {
						for (int y = 0; y < x; y++) {
							if (newKings[x][0] == newKings[y][0] && newKings[x][1] == newKings[y][1]) {
								duplicate = true;
							}
						}
					}
				}
			}
			for (int x = 0; x < newKings.length; x++) {
				for (int y = 0; y < x; y++) {
					int astar = AStar(newBoard, newKings[x][0], newKings[x][1], newKings[y][0], newKings[y][1]);
					int manhattan = heuristic(newKings[x][0], newKings[x][1], newKings[y][0], newKings[y][1]);
					if (astar < minMazeDist || manhattan < minManhattanDist) {
						unsuccessful = true;
						break;
					}
				}
				if (unsuccessful) {
					break;
				}
			}
		}
		if (attempts > 0) {
			kings = newKings;
			grid = newBoard;
			addKings();
			if (print) {
				print();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds the kings to the grid manually.
	 */
	private void addKings() {
		for (int x = 0; x < kings.length; x++) {
			grid[kings[x][0]][kings[x][1]] = x + 1;
		}
	}

	/**
	 * Prints out the board.
	 */
	public void print() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j] == 0) {
					System.out.print(".");
				} else if (grid[i][j] == -1) {
					System.out.print("^");
				} else if (grid[i][j] == -2) {
					System.out.print("X");
				} else {
					System.out.print(grid[i][j]);
				}
				System.out.print(" ");

			}
			System.out.println();
		}
	}

	/**
	 * Generates a grid randomly.
	 * 
	 * @return the generated grid
	 */
	private int[][] generate(int mountains, int cities) {
		int[][] newGrid = new int[i][j];
		while (mountains > 0) {
			int nextI = r.nextInt(i);
			int nextJ = r.nextInt(j);
			if (newGrid[nextI][nextJ] == 0) {
				newGrid[nextI][nextJ] = -1;
				mountains--;
			}
		}
		while (cities > 0) {
			int nextI = r.nextInt(i);
			int nextJ = r.nextInt(j);
			if (newGrid[nextI][nextJ] == 0) {
				newGrid[nextI][nextJ] = -2;
				cities--;
			}
		}
		return newGrid;
	}

	/**
	 * Initializes the player sequence.
	 */
	private void initSequence() {
		for (int i = 0; i < playerSequence.length; i++) {
			playerSequence[i] = i;
		}
	}

	/**
	 * Conducts a A* search between two locations.
	 * 
	 * @param board
	 *            - the board
	 * @param iStart
	 *            - the starting i position
	 * @param jStart
	 *            - the starting j position
	 * @param iEnd
	 *            - the ending i position
	 * @param jEnd
	 *            - the ending j position
	 * @return the distance between the two locations, -1 if no path exists
	 */
	private static int AStar(int[][] board, int iStart, int jStart, int iEnd, int jEnd) {// currently broken, needs
																							// to update
		int I = board.length;
		int J = board[0].length;
		Set<Integer> visited = new HashSet<>();
		PriorityQueue<Integer[]> pq = new PriorityQueue<>(new Comparator<Integer[]>() {
			public int compare(Integer[] o1, Integer[] o2) {
				return o1[2] + o1[3] - o2[2] - o2[3];
			}
		});
		pq.offer(new Integer[] { iStart, jStart, 0, heuristic(iStart, jStart, iEnd, jEnd) });
		while (!pq.isEmpty()) {
			Integer[] current = pq.poll();
			int i = current[0];
			int j = current[1];
			int steps = current[2];
			if (i == iEnd && j == jEnd) {
				return steps;
			}
			if (!visited.contains(i + j * I)) {
				visited.add(i + j * I);
				if (i + 1 < I && !visited.contains(i + 1 + j * I) && board[i + 1][j] == 0) {
					pq.offer(new Integer[] { i + 1, j, steps + 1, heuristic(i + 1, j, iEnd, jEnd) });
				}
				if (i - 1 >= 0 && !visited.contains(i - 1 + j * I) && board[i - 1][j] == 0) {
					pq.offer(new Integer[] { i - 1, j, steps + 1, heuristic(i - 1, j, iEnd, jEnd) });
				}
				if (j + 1 < J && !visited.contains(i + j * I + I) && board[i][j + 1] == 0) {
					pq.offer(new Integer[] { i, j + 1, steps + 1, heuristic(i, j + 1, iEnd, jEnd) });
				}
				if (j - 1 >= 0 && !visited.contains(i + j * I - I) && board[i][j - 1] == 0) {
					pq.offer(new Integer[] { i, j - 1, steps + 1, heuristic(i, j - 1, iEnd, jEnd) });
				}
			}
		}
		return -1;
	}

	private static int heuristic(int i1, int j1, int i2, int j2) {
		return Math.abs(i1 - i2) + Math.abs(j1 - j2);
	}
}
