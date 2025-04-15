/**
 * HW4. Backtracking, solution and maze generation <br>
 * This file contains 2 classes: <br> designed by Jean-Christophe Filliâtre
 * 	- ExtendCell provides a cell of the maze with operations to calculate a path to the exit and generate a maze recursively <br>
 * 	- Maze models a maze.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class extends and enriches the representation of a cell of the maze. <br>
 * It provides to a cell the operations to: <br>
 * -) find a path to the exit <br>
 * -) Generate a maze recursively
 */
class ExtendedCell extends Cell {
	
	public ExtendedCell(Maze maze) {
		super(maze);
	}

	// Question 1

	/**
	 * Test if there is a path from the current cell to an exit
	 * 
	 * @return true if there is a path from the current cell to an exit
	 */
	boolean searchPath() {
		maze.slow(); // slow down the search animation (to help debugging)
		setMarked(true);
		for (Cell neighbor : getNeighbors(false)) {
			if (neighbor.isExit()) {
				neighbor.setMarked(true);
				return true;
			}
			if (!neighbor.isMarked()) {
				neighbor.setMarked(true);
				if (neighbor.searchPath()) {
					return true;
				}
			}
		}
		setMarked(false);
		return false;
	}

	// Question 2

	/**
	 * generate a perfect maze using recursive backtracking
	 */
	void generateRec() {
		maze.slow();
		List<Cell> shuffled_neighbors; // 
		shuffled_neighbors = getNeighbors(true);

		Collections.shuffle(shuffled_neighbors);
		for (Cell neighbor : shuffled_neighbors) {
			if (neighbor.isIsolated()) {
				breakWall(neighbor);
				neighbor.generateRec();
			}
		}
	}
}

/**
 * this class models a maze
 */
class Maze {

	private int height, width;
	/** the grid (array of cells) representing the maze */ 
	private Cell[][] grid;


	// Question 3

	/**
	 * generate a perfect maze using iterative backtracking
	 */
	void generateIter(int selectionMethod) {
		Bag cells = new Bag(selectionMethod);
		cells.add(getFirstCell());

		while(!cells.isEmpty()) {
			slow();
			Cell currentCell = cells.peek();
			List<Cell> shuffled_neighbors; // 
			shuffled_neighbors = currentCell.getNeighbors(true);
			Collections.shuffle(shuffled_neighbors);

			boolean flag = false;
			for (Cell cell : shuffled_neighbors) {
				if (cell.isIsolated()) {
					currentCell.breakWall(cell);
					cells.add(cell);
					flag = true;
					break;
				}
			}
			if (flag) {
				continue;
			}
			cells.pop();
		}
	}


	// Question 4
	
	/**
	 * generate a maze using Wilson's algorithm
	 */
	void generateWilson() {
		List<Cell> cells = new ArrayList<>();
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				cells.add(grid[i][j]);
			}
		}
		Collections.shuffle(cells);
		cells.getFirst().setMarked(true);
		for (Cell cell : cells) {
			if (cell.isMarked()) {
				continue;
			}
			findPath(cell);
			Cell current = cell;
			while(current.next != null){
				current.setMarked(true);
				current.breakWall(current.next);
				current = current.next;
			}
		}
	}
	void findPath(Cell cell){
		if (cell.isMarked()){
			return;
		}

		List<Cell> shuffled_neighbors = cell.getNeighbors(true);
		Collections.shuffle(shuffled_neighbors);
		cell.next = shuffled_neighbors.getFirst();
		findPath(cell.next);
	}

	/**
	 * return the cell with coordinates (i, j)
	 * 
	 * @return the cell with coordinates (i, j)
	 */
	Cell getCell(int i, int j) {
		if(i < 0 || i >= height || j < 0 || j >= width)
			throw new IllegalArgumentException("invalid indices");

		return grid[i][j];
	}

	/**
	 * return the cell with coordinates (0, 0)
	 * 
	 * @return the cell with coordinates (0, 0)
	 */
	Cell getFirstCell() {
		return getCell(0, 0);
	}

	// translate coordinates to cell number
	int coordToInt(int i, int j) {
		if(i < 0 || i >= height || j < 0 || j >= width)
			throw new IndexOutOfBoundsException();

		return i*width + j;
	}

	// translate cell number to coordinates
	Coordinate intToCoord(int x) {
		if(x < 0 || x >= height*width)
			throw new IndexOutOfBoundsException();

		return new Coordinate(x/width, x%width);
	}


	// slow down the display of the maze if a graphical window is open
	void slow(){
		if (frame == null) return;

		try {
			Thread.sleep(10);
			frame.repaint();
		} catch (InterruptedException e) {}
	}

	private MazeFrame frame;
	private static final int step = 20;

	Maze(int height, int width) {
		this(height, width, true);
	}

	Maze(int height, int width, boolean window) {
		if((height <= 0) || (width <= 0))
			throw new IllegalArgumentException("height and width of a Maze must be positive");

		this.height = height;
		this.width = width;

		grid = new Cell[height][width];

		for(int i = 0; i < height; ++i)
			for(int j = 0; j < width; ++j)
				grid[i][j] = new ExtendedCell(this);

		for(int i = 0; i < height; ++i) {
			for(int j = 0; j < width; ++j) {
				if(i < height - 1) {
					grid[i][j].addNeighbor(grid[i+1][j]);
					grid[i+1][j].addNeighbor(grid[i][j]);
				}

				if(j < width - 1) {
					grid[i][j].addNeighbor(grid[i][j+1]);
					grid[i][j+1].addNeighbor(grid[i][j]);
				}
			}
		}

		grid[height-1][width-1].setExit(true);

		if(window)
			frame = new MazeFrame(grid, height, width, step);
	}

	Maze(String path) throws IOException {
		this(Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8));
	}

	Maze(String path, boolean window) throws IOException {
		this(Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8), window);
	}

	Maze(List<String> lines) {
		this(lines, true);
	}

	Maze(List<String> lines, boolean window) {
		if(lines.size() < 2)
			throw new IllegalArgumentException("too few lines");

		this.height = Integer.parseInt(lines.get(0));
		this.width = Integer.parseInt(lines.get(1));

		this.grid = new Cell[height][width];
		for(int i = 0; i < height; ++i)
			for(int j = 0; j < width; ++j)
				grid[i][j] = new ExtendedCell(this);

		for(int i = 0; i < height; ++i) {
			for(int j = 0; j < width; ++j) {
				if(i < height - 1) {
					grid[i][j].addNeighbor(grid[i+1][j]);
					grid[i+1][j].addNeighbor(grid[i][j]);
				}

				if(j < width - 1) {
					grid[i][j].addNeighbor(grid[i][j+1]);
					grid[i][j+1].addNeighbor(grid[i][j]);
				}
			}
		}

		grid[height-1][width-1].setExit(true);

		int i = 0;
		int j = 0;

		for(String line : lines.subList(2, lines.size())) {

			for(int k = 0; k < line.length(); ++k) {
				switch(line.charAt(k)) {
					case 'N':
						grid[i][j].breakWall(grid[i-1][j]);
						break;
					case 'E':
						grid[i][j].breakWall(grid[i][j+1]);
						break;
					case 'S':
						grid[i][j].breakWall(grid[i+1][j]);
						break;
					case 'W':
						grid[i][j].breakWall(grid[i][j-1]);
						break;
					case '*':
						grid[i][j].setMarked(true);
						break;
					default:
						throw new IllegalArgumentException("illegal character");
				}
			}
			++j;
			if(j >= width) {
				j = 0;
				++i;
			}
		}

		if(window)
			frame = new MazeFrame(grid, height, width, step);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(height);
		sb.append('\n');
		sb.append(width);
		sb.append('\n');

		for(int i = 0; i < height; ++i) {
			for(int j = 0; j < width; ++j) {
				if(i > 0 && grid[i][j].hasPassageTo(grid[i-1][j]))
					sb.append('N');
				if(j < width-1 && grid[i][j].hasPassageTo(grid[i][j+1]))
					sb.append('E');
				if(i < height-1 && grid[i][j].hasPassageTo(grid[i+1][j]))
					sb.append('S');
				if(j > 0 && grid[i][j].hasPassageTo(grid[i][j-1]))
					sb.append('W');
				if(grid[i][j].isMarked())
					sb.append('*');
				sb.append('\n');
			}
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Maze))
			return false;
		Maze that = (Maze)o;

		return this.toString().equals(that.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	boolean isPerfect() {
		UnionFind uf = new UnionFind(height*width);

		// union find cycle detection
		for(int i = 0; i < height; ++i) {
			// horizontal edges
			for(int j = 0; j < width-1; ++j) {
				if(grid[i][j].hasPassageTo(grid[i][j+1])) {
					if(uf.sameClass(coordToInt(i,j), coordToInt(i,j+1)))
						return false;
					uf.union(coordToInt(i,j), coordToInt(i,j+1));
				}
			}

			// there are no vertical edges in last row, so we're done
			if(i == height-1)
				continue;

			// vertical edges
			for(int j = 0; j < width; ++j) {
				if(grid[i][j].hasPassageTo(grid[i+1][j])) {
					if(uf.sameClass(coordToInt(i,j), coordToInt(i+1,j)))
						return false;
					uf.union(coordToInt(i,j), coordToInt(i+1,j));
				}
			}
		}

		// check if connected
		return (uf.getSize(0) == height*width);
	}

	void clearMarks() {
		for (Cell[] row : grid)
			for (Cell c : row)
				c.setMarked(false);
	}
}

