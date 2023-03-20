package laboratoire4;

import java.util.ArrayList;
import java.util.List;

public class Board {

  public static final int PLAYER_BLACK = 1;
  public static final int PLAYER_RED = 2;
  public static final int EMPTY = 0;

  private int[][] squares = new int[8][8];

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

  private List<int[]> getPossibleMoves(int player) {
    ArrayList<int[]> moves = new ArrayList<int[]>();
    int[] pawnTypes, pushTypes;
    int[][] directions;
    if (player == 1) {
      pawnTypes = new int[] { 1, 3 };
      pushTypes = new int[] { 2, 4 };
      directions = new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 } };
    } else {
      pawnTypes = new int[] { 2, 4 };
      pushTypes = new int[] { 1, 3 };
      directions = new int[][] { { 1, -1 }, { 1, 0 }, { 1, 1 } };
    }
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (this.squares[i][j] == 0) {
          // Empty square, check for possible moves
          for (int[] dir : directions) {
            int x = i + dir[0];
            int y = j + dir[1];
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
              if (contains(pawnTypes, this.squares[x][y])) {
                // Found a possible move to a square with a pawn
                int[] move = { i, j, x, y };
                moves.add(move);
              }
            }
          }
        } else if (contains(pushTypes, this.squares[i][j])) {
          // Found a pusher pawn, check for possible pushes
          for (int[] dir : directions) {
            int x = i + dir[0];
            int y = j + dir[1];
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
              if (this.squares[x][y] == 0) {
                // Found a possible push
                int[] move = { i, j, x, y };
                moves.add(move);
              } else {
                // Check for possible capture
                int x2 = x + dir[0];
                int y2 = y + dir[1];
                if (x2 >= 0 && x2 < 8 && y2 >= 0 && y2 < 8) {
                  if (
                    contains(pawnTypes, this.squares[x][y]) &&
                    this.squares[x2][y2] == 0
                  ) {
                    // Found a possible capture
                    int[] move = { i, j, x2, y2 };
                    moves.add(move);
                  }
                }
              }
            }
          }
        }
      }
    }
    return moves;
  }

  public long getLegalMoves(long playerPieces, long opponentPieces) {
    long emptySquares = ~(playerPieces | opponentPieces);
    long moves = 0L;

    // All possible left captures
    long leftCaptures =
      (opponentPieces << 7) & playerPieces & 0x7F7F7F7F7F7F7F7FL;
    if (leftCaptures != 0) {
      moves |= (leftCaptures << 7) & emptySquares & 0xFEFEFEFEFEFEFEFEL;
    }

    // All possible right captures
    long rightCaptures =
      (opponentPieces << 9) & playerPieces & 0xFEFEFEFEFEFEFEFEL;
    if (rightCaptures != 0) {
      moves |= (rightCaptures << 9) & emptySquares & 0x7F7F7F7F7F7F7F7FL;
    }

    // All possible non-capturing moves
    moves |= (playerPieces << 8) & emptySquares & 0xFFFFFFFFFFFFFF00L;
    moves |= (playerPieces >> 8) & emptySquares & 0x00FFFFFFFFFFFFFFL;

    return moves;
  }

  public int minimax(
    long playerPieces,
    long opponentPieces,
    int depth,
    int alpha,
    int beta,
    boolean maximizingPlayer
  ) {
    int score;
    long legalMoves = getLegalMoves(playerPieces, opponentPieces);

    if (depth == 0 || legalMoves == 0) {
      score = evaluate(playerPieces, opponentPieces);
      return score;
    }

    if (maximizingPlayer) {
      score = Integer.MIN_VALUE;
      while (legalMoves != 0) {
        long move = Long.lowestOneBit(legalMoves);
        legalMoves ^= move;
        long newPlayerPieces = playerPieces | move;
        long newOpponentPieces = opponentPieces & ~move;
        int value = minimax(
          newOpponentPieces,
          newPlayerPieces,
          depth - 1,
          alpha,
          beta,
          false
        );
        score = Math.max(score, value);
        alpha = Math.max(alpha, score);
        if (beta <= alpha) {
          break;
        }
      }
      return score;
    } else {
      score = Integer.MAX_VALUE;
      while (legalMoves != 0) {
        long move = Long.lowestOneBit(legalMoves);
        legalMoves ^= move;
        long newPlayerPieces = playerPieces & ~move;
        long newOpponentPieces = opponentPieces | move;
        int value = minimax(
          newOpponentPieces,
          newPlayerPieces,
          depth - 1,
          alpha,
          beta,
          true
        );
        score = Math.min(score, value);
        beta = Math.min(beta, score);
        if (beta <= alpha) {
          break;
        }
      }
      return score;
    }
  }

  public static boolean contains(int[] arr, int targetValue) {
    for (int value : arr) {
      if (value == targetValue) {
        return true;
      }
    }
    return false;
  }

  /**
   * Fonction qui détermine le prochain coup de l’ordinateur. Cette fonction dispose de 5 secondes pour choisir son coup.
   */
  public void minimaxAlphaBeta(
    int positionActuelle,
    String joueur,
    int alpha,
    int beta
  ) {
    // si positionActuelle est finale
    //   retourner staticEvaluator(position)

    // si joueur == MAX
    //   alphaActuel = -infini
    //   pour tous les successeurs de pi de position
    //     score = MinimaxAlphaBeta(pi, Min, MAX(alpha, alphaActuel),  beta)
    //     alphaActuel = MAX(alphaActuel, score)
    //     si alphaActuel >= beta
    //       retourner alphaActuel
    //   retourner alphaActuel

    // si joueur == MIN
    //   betaActuel = infini
    //   pour tous les successeurs de pi de position
    //     score = MinimaxAlphaBeta(pi, MIN(beta, betaActuel), Max,  alpha)
    //     betaActuel = MAX(betaActuel, score)
    //     si betaActuel <= alpha
    //       retourner betaActuel
    //   retourner betaActuel
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
