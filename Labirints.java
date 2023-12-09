package dip107;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class Labirints {
	public static void main(String[] args) {
		System.out.println("Darija Rumjanceva 1.grupa 221RMC087");
		System.out.println("Artjoms Fomins 9.grupa 231RDB152");

		Scanner sc = new Scanner(System.in);

		int rowCount, columnCount;

		try {
			System.out.print("row count: ");
			rowCount = sc.nextInt();

			System.out.print("column count: ");
			columnCount = sc.nextInt();
			sc.nextLine();

		} catch (InputMismatchException e) {
			System.out.println("Invalid value. Expected number.");
			sc.close();
			return;
		}

		int[][] labyrinth;

		char answer;
		System.out.print("Auto fill maze (y/n)? ");
		answer = sc.nextLine().charAt(0);

		if (answer == 'n') {
			labyrinth = new int[rowCount][columnCount];
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					int step;
					try {
						step = sc.nextInt();
						if (step != 0 && step != 1) {
							throw new InputMismatchException();
						}

					} catch (InputMismatchException e) {
						System.out.println("Invalid value. Expected number 0 or 1.");
						sc.close();
						return;
					}
					labyrinth[i][j] = step;
				}
			}
		} else {
			labyrinth = new int[rowCount][columnCount];
			Random random = new Random();

			double probabilityThreshold = 0.75;
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					// make sure the entry and exit points are always 0
					if ((j == 0 && i == 0) || (j == columnCount - 1 && i == rowCount - 1)) {
						labyrinth[i][j] = 0;
						continue;
					}
					double randomValue = random.nextDouble(1);
					// increase the probability of a cell being a path
					if (randomValue < probabilityThreshold) {
						labyrinth[i][j] = 0;
					} else {
						labyrinth[i][j] = 1;
					}

				}
			}
			for (int[] row : labyrinth) {
				for (int col : row) {
					System.out.print(col + " ");
				}
				System.out.println();
			}
		}

		System.out.print("method number (1-3): ");
		int m = sc.nextInt();
		sc.close();

		switch (m) {
			case 1: // 1. algoritms
				LabyrinthSolverResoursive mazeSolver = new LabyrinthSolverResoursive(labyrinth);
				mazeSolver.solveLabyrinth();
				break;
			case 2: // 2. algoritms
				LabyrinthSolverQueue mazeSolverStack = new LabyrinthSolverQueue(labyrinth);
				mazeSolverStack.solveLabyrinth();

				break;
			case 3: // 3. algoritms
				LabyrinthSolverDijkstra mazeSolverDijkstra = new LabyrinthSolverDijkstra(labyrinth);
				mazeSolverDijkstra.solveLabyrinth();
				break;
		}

	}
}

class LabyrinthSolverResoursive {
	private int[][] maze;
	private int rows;
	private int cols;
	private boolean[][] visited;
	private List<Integer[]> path;

	public LabyrinthSolverResoursive(int[][] maze) {
		this.maze = maze;
		this.rows = maze.length;
		this.cols = maze[0].length;
		this.visited = new boolean[rows][cols];
		this.path = new ArrayList<>();
	}

	public void solveLabyrinth() {
		solveLabyrinthHelper(0, 0);
		printPath();
	}

	private boolean solveLabyrinthHelper(int row, int col) {
		if (!isValidPosition(row, col) || visited[row][col] || maze[row][col] == 1) {
			return false;
		}

		Integer[] currentPosition = { row, col };

		if (row == this.rows - 1 && col == this.cols - 1) {
			path.add(0, currentPosition);
			return true;
		}

		// Mark the current position as visited
		visited[row][col] = true;

		// Recursively explore the neighboring positions. Up, Left, Down, Right
		if (solveLabyrinthHelper(row - 1, col)) {
			path.add(0, currentPosition);
			return true;
		}

		if (solveLabyrinthHelper(row, col - 1)) {
			path.add(0, currentPosition);
			return true;
		}

		if (solveLabyrinthHelper(row + 1, col)) {
			path.add(0, currentPosition);
			return true;
		}
		if (solveLabyrinthHelper(row, col + 1)) {
			path.add(0, currentPosition);
			return true;
		}

		return false;
	}

	private boolean isValidPosition(int row, int col) {
		return row >= 0 && row < rows && col >= 0 && col < cols;
	}

	private void printPath() {
		if (path.isEmpty()) {
			System.out.println("No path found.");
		} else {
			System.out.println("results:");
			for (int i = 0; i < path.size(); i++) {
				Integer[] point = path.get(i);
				System.out.print("(" + point[0] + "," + point[1] + ")" + " ");
			}
		}
	}

}

class LabyrinthSolverQueue {
	private int[][] labyrinth;
	private int rows;
	private int cols;
	private boolean[][] visited;
	private List<Integer[]> path;
	private boolean pathExists;

	public LabyrinthSolverQueue(int[][] labyrinth) {
		this.labyrinth = labyrinth;
		this.rows = labyrinth.length;
		this.cols = labyrinth[0].length;
		this.visited = new boolean[rows][cols];
		this.path = new ArrayList<>();
		this.pathExists = false;
	}

	public void solveLabyrinth() {
		solveLabyrinthHelper();
		printPath();
	}

	private void solveLabyrinthHelper() {
		Queue<Integer[]> queue = new LinkedList<>();
		queue.add(new Integer[] { 0, 0 });

		int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		while (!queue.isEmpty()) {
			Integer[] current = queue.poll();
			int row = current[0];
			int col = current[1];

			if (!isValid(row, col) || visited[row][col] || labyrinth[row][col] == 1) {
				continue;
			}

			visited[row][col] = true;
			path.add(new Integer[] { row, col });

			if (row == this.rows - 1 && col == this.cols - 1) {
				this.pathExists = true;
				break;
			}

			for (int i = 0; i < directions.length; i++) {
				int[] direction = directions[i];
				queue.add(new Integer[] { row + direction[0], col + direction[1] });
			}
		}
	}

	private boolean isValid(int row, int col) {
		return row >= 0 && row < rows && col >= 0 && col < cols;
	}

	private void printPath() {
		if (!pathExists) {
			System.out.println("No path found.");
		} else {
			System.out.println("results:");
			for (int i = 0; i < path.size(); i++) {
				Integer[] point = path.get(i);
				System.out.print("(" + point[0] + "," + point[1] + ")" + " ");
			}
		}
	}

}

class LabyrinthSolverDijkstra {
	private int[][] labyrinth;
	private List<Node> path;
	private int rows;
	private int cols;

	LabyrinthSolverDijkstra(int[][] labyrinth) {
		this.labyrinth = labyrinth;
		this.path = new ArrayList<>();
		this.rows = labyrinth.length;
		this.cols = labyrinth[0].length;
	}

	private List<Node> solvelabyrinthHelper() {

		PriorityQueue<Node> minHeap = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
		boolean[][] visited = new boolean[rows][cols];
		Map<Node, Node> parentMap = new HashMap<>();

		minHeap.add(new Node(0, 0, 0));

		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // Up, Down, Left, Right

		while (!minHeap.isEmpty()) {
			Node current = minHeap.poll();

			int x = current.x;
			int y = current.y;

			if (x == rows - 1 && y == cols - 1) {
				// Path to exit found, reconstruct the path
				return reconstructPath(parentMap, current);
			}

			if (!isValid(x, y) || visited[x][y] || labyrinth[x][y] == 1) {
				continue;
			}

			visited[x][y] = true;

			for (int[] dir : directions) {
				int newX = x + dir[0];
				int newY = y + dir[1];

				if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && labyrinth[newX][newY] == 0
						&& !visited[newX][newY]) {
					int newCost = current.cost + 1;
					Node neighbor = new Node(newX, newY, newCost);
					minHeap.add(neighbor);
					parentMap.put(neighbor, current);
				}
			}
		}

		return null; // No path to exit
	}

	private List<Node> reconstructPath(Map<Node, Node> parentMap, Node endNode) {
		Node current = endNode;

		while (current != null) {
			path.add(current);
			current = parentMap.get(current);
		}

		Collections.reverse(path);
		return path;
	}

	private void printPath() {
		if (path.isEmpty()) {
			System.out.println("No path found.");
		} else {
			System.out.println("results:");
			for (Node node : path) {
				System.out.print("(" + node.x + "," + node.y + ")" + " ");
			}
		}
	}

	private class Node {
		int x, y, cost;

		public Node(int x, int y, int cost) {
			this.x = x;
			this.y = y;
			this.cost = cost;
		}
	}

	private boolean isValid(int row, int col) {
		return row >= 0 && row < rows && col >= 0 && col < cols;
	}

	public void solveLabyrinth() {
		solvelabyrinthHelper();
		printPath();
	}
}