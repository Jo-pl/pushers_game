package laboratoire4;

public class Board {

  private int[][] squares = new int[8][8];
  private Piece[] red;
  private Piece[] black;

  public Board(String configuration) {
    String[] boardValues;
    boardValues = configuration.split(" ");
    int x = 0, y = 0;
    for (int i = 0; i < boardValues.length; i++) {
      squares[x][y] = Integer.parseInt(boardValues[i]);
      x++;
      if (x == 8) {
        x = 0;
        y++;
      }
    }
  }

  public void update(String move) {
    char[] lastMove = move.replaceAll("[-\\s+\u0000]", "").toCharArray();
    //New token
    this.squares[getAlphabetPosition(lastMove[2]) - 1][this.squares[0].length -
      Character.getNumericValue(lastMove[3])] =
      this.squares[getAlphabetPosition(lastMove[0]) -
        1][this.squares[0].length - Character.getNumericValue(lastMove[1])];
    //Delete the old token
    this.squares[getAlphabetPosition(lastMove[0]) - 1][this.squares[0].length -
      Character.getNumericValue(lastMove[1])] =
      0;
  }

  private int getAlphabetPosition(char letter) {
    return Character.toUpperCase(letter) - 64;
  }

  @Override
  public String toString() {
    String msg = "";
    for (int col = 0; col < this.squares.length; col++) {
      for (int line = 0; line < this.squares[0].length; line++) {
        msg += this.squares[line][col];
      }
      msg += "\n";
    }
    return msg;
  }
}
