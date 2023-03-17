package laboratoire4;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    enum Case {
        Vide,
        Noir,
        NoirPusher,
        Rouge,
        RougePusher
    }

    private final Case[][] board = new Case[8][8];
    private final boolean isSecondPlayerTurn;

    // Caches of the boards to save some computation time
    private HashMap<Move, Board> nextMoves = new HashMap<Move, Board>();

    public Board(String init, boolean isSecondPlayerTurn) {
        this.isSecondPlayerTurn = isSecondPlayerTurn;
        // TODO - Init the board
    }

    // KEEP PRIVATE - use Board.getNextMove(Move) instead
    private Board(Board parent, Move move) {
        this.isSecondPlayerTurn = !parent.isSecondPlayerTurn;
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                if (x == move.colA && y == move.rowA) {
                    board[y][x] = Case.Vide;
                } else if (x == move.colB && y == move.rowB) {
                    board[y][x] = parent.board[move.rowA][move.colA];
                } else {
                    board[y][x] = parent.board[y][x];
                }
            }
        }
    }

    // Get all possible next moves
    public ArrayList<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<Move>();

        // TODO: Make a list of possible moves

        return moves;
    }

    // Get the board corresponding to a certain move. The move is NOT validated!
    public Board getNextMove(Move move) {
        if (!nextMoves.containsKey(move)) {
            nextMoves.put(move, new Board(this, move));
        }

        return nextMoves.get(move);
    }

    private boolean hasCachedHeuristic = false;
    private double cachedHeuristic;
    public double getHeuristic() {
        if (!hasCachedHeuristic) {
            // TODO: Evaluate the heuristic
            cachedHeuristic = 0.0;
            hasCachedHeuristic = true;
        }

        return cachedHeuristic;
    }

    public static class Move {
        public final int colA, rowA, colB, rowB;

        private Move(int colA, int rowA, int colB, int rowB) {
            this.colA = colA;
            this.rowA = rowA;
            this.colB = colB;
            this.rowB = rowB;
        }

        public Move(String cmd) {
            this.colA = cmd.charAt(0) - 'A';
            this.rowA = cmd.charAt(1) - '1';
            this.colB = cmd.charAt(3) - 'A';
            this.rowB = cmd.charAt(4) - '1';
        }

        @Override
        public String toString() {
            return new String(new byte[] { (byte) ('A' + colA) }) + (rowA + 1) + "-"
                 + new String(new byte[] { (byte) ('A' + colB) }) + (rowB + 1);
        }
    }
}
