package laboratoire4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameBoard {

  public static final int EMPTY = 0;
  public static final int BLACK_PUSHED = 1;
  public static final int BLACK_PUSHER = 2;
  public static final int RED_PUSHED = 3;
  public static final int RED_PUSHER = 4;
  public static final int NB_ROW = 8;
  public static final int NB_COL = 8;

  private int[][] squares = new int[8][8];

  public GameBoard(String configuration) {
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

  /**
   * Fonction qui détermine le prochain coup de l’ordinateur. Cette fonction dispose de 5 secondes pour choisir son coup.
   */
  public double minimaxAlphaBeta(
    int[][] board,
    int depth,
    double alpha,
    double beta,
    boolean maximizingPlayer
  ) {
    if (depth == 0 || isGameOver(board, maximizingPlayer)) { // Or game over in position
      return evaluator(board);
    }

    if (maximizingPlayer) {
      double max = Double.NEGATIVE_INFINITY;
      for (int[] move : getPossibleMoves(board, true)) {
        int[][] position = makeMove(board, move);
        double evaluation = minimaxAlphaBeta(
          position,
          depth - 1,
          alpha,
          beta,
          false
        );
        max = Math.max(max, evaluation);
        alpha = Math.max(alpha, evaluation);
        if (beta <= alpha) {
          break;
        }
      }
      return max;
    } else {
      double min = Double.POSITIVE_INFINITY;
      for (int[] move : getPossibleMoves(board, true)) {
        int[][] position = makeMove(board, move);
        double evaluation = minimaxAlphaBeta(
          position,
          depth - 1,
          alpha,
          beta,
          false
        );
        min = Math.min(min, evaluation);
        beta = Math.min(beta, evaluation);
        if (beta <= alpha) {
          break;
        }
      }
      return min;
    }
  }

  public double evaluator(int[][] board) {
    return 0;
  }

  public boolean isGameOver(int[][] board, boolean isFirstPlayer) {
    boolean playerBlack = !isFirstPlayer;

    // Vérifier si tous les pions poussés ou pousseurs de l'un des joueurs ont été capturés ou bloqués
    boolean redPushedBlocked = true;
    boolean redPusherBlocked = true;
    boolean blackPushedBlocked = true;
    boolean blackPusherBlocked = true;
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        if (
          board[row][col] == 1 &&
          !isPushedBlocked(board, row, col, isFirstPlayer)
        ) {
          redPushedBlocked = false;
        } else if (
          board[row][col] == 2 &&
          !isPusherBlocked(board, row, col, isFirstPlayer)
        ) {
          redPusherBlocked = false;
        } else if (
          board[row][col] == 3 &&
          !isPushedBlocked(board, row, col, isFirstPlayer)
        ) {
          blackPushedBlocked = false;
        } else if (
          board[row][col] == 4 &&
          !isPusherBlocked(board, row, col, isFirstPlayer)
        ) {
          blackPusherBlocked = false;
        }
      }
    }
    if (isFirstPlayer && (redPushedBlocked || redPusherBlocked)) {
      System.out.println("Joueur rouge gagne!");
      return true;
    } else if (playerBlack && (blackPushedBlocked || blackPusherBlocked)) {
      System.out.println("Joueur noir gagne!");
      return true;
    }

    // Vérifier si l'un des joueurs ne peut plus effectuer de mouvement valide
    boolean redCanMove = false;
    boolean blackCanMove = false;
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        if (
          board[row][col] != 0 && isValidMove(board, row, col, isFirstPlayer)
        ) {
          redCanMove = true;
        }
        if (board[row][col] != 0 && isValidMove(board, row, col, playerBlack)) {
          blackCanMove = true;
        }
      }
    }
    if (!redCanMove) {
      System.out.println("Joueur noir gagne!");
      return true;
    } else if (!blackCanMove) {
      System.out.println("Joueur rouge gagne!");
      return true;
    }

    // Si aucune des conditions ci-dessus n'est remplie, la partie n'est pas terminée
    return false;
  }

  public boolean isValidMove(
    int[][] board,
    int row,
    int col,
    boolean isFirstPlayer
  ) {
    int piece = board[row][col];
    if (piece == 0) {
      // empty square, no move possible
      return false;
    }
    if (
      (isFirstPlayer && (piece == 3 || piece == 4)) ||
      (!isFirstPlayer && (piece == 1 || piece == 2))
    ) {
      // cannot move opponent's piece
      return false;
    }

    // Check if the piece can move to any of the neighboring squares
    int[][] directions = { { -1, 0 }, { 0, -1 }, { 1, 0 }, { 0, 1 } }; // Up, Left, Down, Right
    for (int[] direction : directions) {
      int newRow = row + direction[0];
      int newCol = col + direction[1];
      if (
        newRow >= 0 &&
        newRow < 8 &&
        newCol >= 0 &&
        newCol < 8 &&
        board[newRow][newCol] == 0
      ) {
        // empty square, can move there
        return true;
      }
    }

    // Check if the piece can push another piece
    int[][] pushDirections = { { -2, 0 }, { 0, -2 }, { 2, 0 }, { 0, 2 } }; // Up, Left, Down, Right
    for (int[] direction : pushDirections) {
      int newRow = row + direction[0];
      int newCol = col + direction[1];
      if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
        int targetPiece = board[newRow][newCol];
        if (
          (isFirstPlayer && (targetPiece == 3 || targetPiece == 4)) ||
          (!isFirstPlayer && (targetPiece == 1 || targetPiece == 2))
        ) {
          // cannot push opponent's piece
          continue;
        }
        int middleRow = row + direction[0] / 2;
        int middleCol = col + direction[1] / 2;
        if (board[middleRow][middleCol] == (isFirstPlayer ? 3 : 1)) {
          // can only push if there's a pusher behind the pushed piece
          return true;
        }
      }
    }

    // no possible moves found
    return false;
  }

  public boolean isPushedBlocked(
    int[][] board,
    int row,
    int col,
    boolean isFirstPlayer
  ) {
    int pushedPiece = board[row][col];
    int opponentPusher = isFirstPlayer ? 4 : 2;

    if (pushedPiece != 1 && pushedPiece != 3) {
      return true;
    }

    int pushRow = row + (pushedPiece == 1 ? 1 : -1);
    int pushCol = col;
    if (pushRow < 0 || pushRow > 7) {
      return true;
    }

    // Check if there is an opponent pusher blocking the push
    if (pushCol > 0 && board[pushRow][pushCol - 1] == opponentPusher) {
      return true;
    }
    if (pushCol < 7 && board[pushRow][pushCol + 1] == opponentPusher) {
      return true;
    }

    // Check if the push is blocked by the edge of the board or a friendly piece
    if (pushCol == 0 || pushCol == 7 || board[pushRow][pushCol] != 0) {
      return true;
    }

    return false;
  }

  public boolean isPusherBlocked(
    int[][] board,
    int row,
    int col,
    boolean isFirstPlayer
  ) {
    int pushedPiece = isFirstPlayer ? 3 : 1;
    int pusherPiece = isFirstPlayer ? 4 : 2;
    int opponentPiece = isFirstPlayer ? 1 : 3;

    if (row == 7 && isFirstPlayer || row == 0 && !isFirstPlayer) {
      return true; // pusher is on opposite side of board, cannot push
    }

    int targetRow = isFirstPlayer ? row + 1 : row - 1;
    int targetPiece = board[targetRow][col];
    if (targetPiece == opponentPiece) {
      return true; // cannot push opponent piece
    } else if (targetPiece == 0) {
      return false; // target cell is empty, push is valid
    } else if (targetPiece == pusherPiece) {
      return isPusherBlocked(board, targetRow, col, isFirstPlayer); // recursive call
    } else if (targetPiece == pushedPiece) {
      if (col == 0) {
        // check if push would go out of bounds on the left side of the board
        return true;
      } else if (col == 7) {
        // check if push would go out of bounds on the right side of the board
        return true;
      } else {
        // check if push would cause pushed piece to be pushed into another piece or off the board
        int leftTargetPiece = board[targetRow][col - 1];
        int rightTargetPiece = board[targetRow][col + 1];
        return leftTargetPiece != 0 && rightTargetPiece != 0;
      }
    }

    return true; // default case, should not be reached
  }

  public List<int[]> getPossibleMoves(int[][] board, boolean isFirstPlayer) {
    List<int[]> moves = new ArrayList<>(); // {x_src, y_src, x_dest, y_dest}

    int up = -1;
    int down = 1;
    int pusher = isFirstPlayer ? RED_PUSHER : BLACK_PUSHER;
    int pushed = isFirstPlayer ? RED_PUSHED : BLACK_PUSHED;
    int forward = isFirstPlayer ? up : down;

    for (int row = 0; row < NB_ROW; row++) {
      for (int col = 0; col < NB_COL; col++) {
        if (board[row][col] == pushed) {
          // Move
          if (row + forward >= 0 && row + forward < 8) {
            if (col - 1 >= 0 && board[row + forward][col - 1] == 0) { // Left
              moves.add(new int[] { row, col, row + forward, col - 1 });
            }
            if (board[row + forward][col] == 0) { // Forward
              moves.add(new int[] { row, col, row + forward, col });
            }
            if (col + 1 < 8 && board[row + forward][col + 1] == 0) { // Right
              moves.add(new int[] { row, col, row + forward, col + 1 });
            }
          }
          // Capture
          if (row + forward * 2 >= 0 && row + forward * 2 < 8) {
            if (
              col - 2 >= 0 &&
              board[row + forward][col - 1] == pusher &&
              board[row + forward * 2][col - 2] == 0
            ) {
              moves.add(new int[] { row, col, row + forward * 2, col - 2 });
            }
            if (
              col + 2 < 8 &&
              board[row + forward][col + 1] == pusher &&
              board[row + forward * 2][col + 2] == 0
            ) {
              moves.add(new int[] { row, col, row + forward * 2, col + 2 });
            }
          }
        } else if (board[row][col] == pusher) {
          // Moves
          if (row + forward >= 0 && row + forward < 8) {
            if (col - 1 >= 0 && board[row + forward][col - 1] == 0) { // Left
              moves.add(new int[] { row, col, row + forward, col - 1 });
            }
            if (board[row + forward][col] == 0) { // Forward
              moves.add(new int[] { row, col, row + forward, col });
            }
            if (col + 1 < 8 && board[row + forward][col + 1] == 0) { // Right
              moves.add(new int[] { row, col, row + forward, col + 1 });
            }
          }
          // TODO : Maybe consider capture moves too
        }
      }
    }

    return moves;
  }

  public static int[][] makeMove(int[][] board, int[] move) {
    int[][] newBoard = Arrays
      .stream(board)
      .map(int[]::clone)
      .toArray(int[][]::new);

    newBoard[move[2]][move[3]] = newBoard[move[0]][move[1]];
    newBoard[move[0]][move[1]] = EMPTY;

    return newBoard;
  }

  @Override
  public String toString() {
    String msg = "";
    for (int col = 0; col < this.squares.length; col++) {
      for (int row = 0; row < this.squares[0].length; row++) {
        msg += this.squares[row][col];
      }
      msg += "\n";
    }
    return msg;
  }
}
