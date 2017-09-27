package system;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

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

	public static void main(String[] args) {
		GameGenerator g = new GameGenerator(20, 20, 40, 10, 2, 20, 14);
		// GameGenerator g = new GameGenerator(35, 35, 200, 40, 8, 10, 6);
		g.create(100, true);
	}

	public GameGenerator(int i, int j, int mountains, int cities, int kingdoms, int minMazeDist, int minManhattanDist) {
		r = new Random();
		this.i = i;
		this.j = j;
		this.mountains = mountains;
		this.cities = cities;
		this.kingdoms = kingdoms;
		this.minManhattanDist = minManhattanDist;
		this.minMazeDist = minMazeDist;
		grid = new int[i][j];
		kings = new int[kingdoms][2];
	}

	public int[][] getGrid() {
		return grid;
	}

	public int[][] getKings() {
		return kings;
	}

	public boolean create(int attempts, boolean print) {
		boolean unsuccessful = true;
		int[][] newKings = new int[kingdoms][2];
		int[][] newBoard = new int[i][j];
		while (attempts > 0 && unsuccessful) {
			unsuccessful = false;
			attempts--;
			newBoard = generate(i, j, mountains, cities);
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

	private void addKings() {
		for (int x = 0; x < kings.length; x++) {
			grid[kings[x][0]][kings[x][1]] = x + 1;
		}
	}

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

	private int[][] generate(int i, int j, int mountains, int cities) {
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
