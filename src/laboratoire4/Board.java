package laboratoire4;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    public enum Case {
        Vide(0),
        Noir(1),
        NoirPusher(2),
        Rouge(3),
        RougePusher(4);

        private int v;
        private Case(int v) {
            this.v = v;
        }
        public int value() {
            return v;
        }
    }

    private final Case[][] board = new Case[8][8];
    private final boolean isSecondPlayerTurn;

    public Board(String init, boolean isSecondPlayerTurn) {
        this.isSecondPlayerTurn = isSecondPlayerTurn;
        String[] s = init.split(" ");
        if (s.length != 64) {
            // ???
        }
        for (int i = 0; i < s.length; i++) {
            board[i / 8][i % 8] = Case.values()[Integer.parseInt(s[i])];
        }
    }

    // KEEP PRIVATE - use Board.getNextMove(Move) instead
    private Board(Board parent, Move move) {
        this.isSecondPlayerTurn = !parent.isSecondPlayerTurn;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
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
    private ArrayList<Move> cachedMoves;
    public ArrayList<Move> getMoves() {
        if (cachedMoves == null) {
            cachedMoves = new ArrayList<Move>();

            // TODO: Make a list of possible moves
        }

        return cachedMoves;
    }

    // Get the board corresponding to a certain move. The move is NOT verified to be valid!
    private HashMap<Move, Board> cachedNextMoves = new HashMap<Move, Board>();
    public Board getNextMove(Move move) {
        if (!cachedNextMoves.containsKey(move)) {
            cachedNextMoves.put(move, new Board(this, move));
        }

        return cachedNextMoves.get(move);
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

    @Override
    public String toString() {
        String s = new String();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                s += board[y][x].value() + " ";
            }
            s += "\n";
        }
        return s;
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
            cmd = cmd.trim();
            this.colA = cmd.charAt(0) - 'A';
            this.rowA = cmd.charAt(1) - '1';
            this.colB = cmd.charAt(5) - 'A';
            this.rowB = cmd.charAt(6) - '1';
        }

        public static boolean isValid(String cmd) {
            return cmd != null && cmd.matches("^[A-H][1-8] - [A-H][1-8]$");
        }

        @Override
        public String toString() {
            return new String(new byte[] { (byte) ('A' + colA) }) + (rowA + 1) + " - "
                 + new String(new byte[] { (byte) ('A' + colB) }) + (rowB + 1);
        }
    }
}
