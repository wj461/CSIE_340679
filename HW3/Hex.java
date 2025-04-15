/* The Hex game
   https://en.wikipedia.org/wiki/Hex_(board_game)
   desigened by Jean-Christophe Filliâtre

   grid size : n*n

   playable cells : (i,j) with 1 <= i, j <= n

   blue edges (left and right) : i=0 or i=n+1, 1 <= j <= n
    red edges (top and bottom) : 1 <= i <= n, j=0 or j=n+1

      note: the four corners have no color

   adjacence :      i,j-1   i+1,j-1

                 i-1,j    i,j   i+1,j

                    i-1,j+1    i,j+1

*/

final class DisjointSet {
  public int[] parent;
  public int[] rank;

  public DisjointSet(int n) {
    parent = new int[n];
    rank = new int[n];

  }

  public int find(int x) {
    if (parent[x] != x) {
      parent[x] = find(parent[x]);
    }
    return parent[x];
  }

  public void union(int x, int y) {
    int xRoot = find(x);
    int yRoot = find(y);
    if (xRoot == yRoot) {
      return;
    }
    if (rank[xRoot] < rank[yRoot]) {
      parent[xRoot] = yRoot;
    } else if (rank[xRoot] > rank[yRoot]) {
      parent[yRoot] = xRoot;
    } else {
      parent[yRoot] = xRoot;
      rank[xRoot]++;
    }
  }
}

public class Hex {
  Player grid[][];
  Player currentPlayer = Player.RED;
  DisjointSet ds;


  enum Player {
    NOONE, BLUE, RED
  }

  // create an empty board of size n*n
  Hex(int n) {
    grid = new Player[n + 2][n + 2];
    ds = new DisjointSet((n + 2) * (n + 2));
    for (int i = 0; i < n + 2; i++) {
      for (int j = 0; j < n + 2; j++) {
        ds.parent[GetBoardId(i, j)] = GetBoardId(i, j);
        ds.rank[GetBoardId(i, j)] = 0;
        grid[i][j] = Player.NOONE;
      }
    }

    for (int i = 1; i < n + 1; i++) {
      ds.union(GetBoardId(i, 0), GetBoardId(1, 0));
      grid[i][0] = Player.RED;
      ds.union(GetBoardId(i, n + 1), GetBoardId(1, n + 1));
      grid[i][n + 1] = Player.RED;

      ds.union(GetBoardId(0, i), GetBoardId(0, 1));
      grid[0][i] = Player.BLUE;
      ds.union(GetBoardId(n+1, i), GetBoardId(n+1, 1));
      grid[n + 1][i] = Player.BLUE;
    }

  }

  int GetBoardId(int i, int j) {
    return i + (grid.length) * j;
  }

  // return the color of cell i,j
  Player get(int i, int j) {
    return grid[i][j];
  }

  // update the board after the player with the trait plays the cell (i, j).
  // Does nothing if the move is illegal.
  // Returns true if and only if the move is legal.
  boolean click(int i, int j) {
    if (grid[i][j] != Player.NOONE) {
      return false;
    }

    grid[i][j] = currentPlayer;
    UnionBlock(i, j);

    if (currentPlayer == Player.RED) {
      currentPlayer = Player.BLUE;
    } else {
      currentPlayer = Player.RED;
    }
    return true;
  }

  void UnionBlock(int i, int j) {
    int[][] directions = {{0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}};
    int currentPoint = GetBoardId(i, j);
    for (int[] direction : directions) {
      int x = i + direction[0];
      int y = j + direction[1];
      if (grid[x][y] == currentPlayer()) {
        ds.union(currentPoint, GetBoardId(x, y));
      }
    }
  }

  // return the player with the trait or Player.NOONE if the game is over
  // because of a player's victory.
  Player currentPlayer() {
    return currentPlayer;
  }

  // return the winning player, or Player.NOONE if no player has won yet
  Player winner() {
    if (ds.find(GetBoardId(0, 1)) == ds.find(GetBoardId(grid.length - 1, 1))) {
      return Player.BLUE;
    }
    else if (ds.find(GetBoardId(1, 0)) == ds.find(GetBoardId(1, grid.length - 1))) {
      return Player.RED;
    }

    return Player.NOONE;
  }

  int label(int i, int j) {
    return ds.find(i + (grid.length) * j);
  }

  public static void main(String[] args) {
    HexGUI.createAndShowGUI();
  }
}
