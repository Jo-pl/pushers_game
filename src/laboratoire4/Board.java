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
    private final boolean isFirstPlayerTurn;

    public Board(String init, boolean isFirstPlayerTurn) {
        this.isFirstPlayerTurn = isFirstPlayerTurn;
        String[] s = init.split(" ");

        if (s.length != 64) {
            System.err.println("/!\\ ERR: Creating board with a non-8x8 data grid. This should never happen.");
        }

        for (int i = 0; i < s.length; i++) {
            board[i / 8][i % 8] = Case.values()[Integer.parseInt(s[i])];
        }
    }

    // KEEP PRIVATE - use Board.getNextMove(Move) instead
    private Board(Board parent, Move move) {
        this.isFirstPlayerTurn = !parent.isFirstPlayerTurn;
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
    // The order is important! The most advantaging moves should be listed first
    private ArrayList<Move> cachedMoves;
    public ArrayList<Move> getMoves() {
        if (cachedMoves == null) {
            cachedMoves = new ArrayList<Move>();

            if (isFirstPlayerTurn) {
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        if (board[y][x] == Case.Rouge) {
                            for (int dx = -1; dx <= 1; dx++) {
                                // Un pion n'est jamais sur la première ligne, pas besoin de vérifier que y < 7
                                if (x + dx > 0 && x + dx < 8 && x - dx > 0 && x - dx < 8 && y > 0) {
                                    if (board[y + 1][x + dx] == Case.RougePusher && (board[y - 1][x - dx] == Case.Vide || (dx != 0 && (board[y - 1][x - dx] == Case.Noir || board[y - 1][x - dx] == Case.NoirPusher)))) {
                                        cachedMoves.add(new Move(x, y, x - dx, y - 1));
                                    }
                                }
                            }
                        } else if (board[y][x] == Case.RougePusher) {
                            for (int dx = -1; dx <= 1; dx++) {
                                if (x - dx > 0 && x - dx < 8 && y > 0) {
                                    if (board[y - 1][x - dx] == Case.Vide || (dx != 0 && (board[y - 1][x - dx] == Case.Noir || board[y - 1][x - dx] == Case.NoirPusher))) {
                                        cachedMoves.add(new Move(x, y, x - dx, y - 1));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (int y = 7; y >= 0; y--) {
                    for (int x = 0; x < 8; x++) {
                        if (board[y][x] == Case.Noir) {
                            for (int dx = -1; dx <= 1; dx++) {
                                // Un pion n'est jamais sur la première ligne, pas besoin de vérifier que y > 0
                                if (x + dx > 0 && x + dx < 8 && x - dx > 0 && x - dx < 8 && y < 7) {
                                    if (board[y - 1][x + dx] == Case.NoirPusher && (board[y + 1][x - dx] == Case.Vide || (dx != 0 && (board[y + 1][x - dx] == Case.Rouge || board[y + 1][x - dx] == Case.RougePusher)))) {
                                        cachedMoves.add(new Move(x, y, x - dx, y + 1));
                                    }
                                }
                            }
                        } else if (board[y][x] == Case.NoirPusher) {
                            for (int dx = -1; dx <= 1; dx++) {
                                if (x - dx > 0 && x - dx < 8 && y < 7) {
                                    if (board[y + 1][x - dx] == Case.Vide || (dx != 0 && (board[y + 1][x - dx] == Case.Rouge || board[y + 1][x - dx] == Case.RougePusher))) {
                                        cachedMoves.add(new Move(x, y, x - dx, y + 1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
            // TODO: Improve heuristic (currently, the heuristic os the row of
            // the furthest pawn. For example, if all of your pawns are on rows
            // 1, 2, 3, and one of them is on row 5, the heuristic is 5.)
            double hP1 = 0.0, hP2 = 0.0;

            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    switch (board[y][x].value()) {
                        case 3:
                        case 4:
                            hP1 = 1.0 - ((double) y) / 8.0;
                            break;

                        default:
                            break;
                    }

                    if (hP1 != 0)
                        break;
                }

                if (hP1 != 0)
                    break;
            }

            for (int y = 7; y >= 0; y--) {
                for (int x = 0; x < 8; x++) {
                    switch (board[y][x].value()) {
                        case 1:
                        case 2:
                            hP2 = ((double) y + 1) / 8.0;
                            break;

                        default:
                            break;
                    }

                    if (hP2 != 0)
                        break;
                }

                if (hP2 != 0)
                    break;
            }

            cachedHeuristic = (hP1 == 1) ? 100 : (hP2 == 1) ? -100 : hP1 - hP2;
            hasCachedHeuristic = true;
        }

        return cachedHeuristic;
    }

    public boolean isFirstPlayer() {
        return isFirstPlayerTurn;
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
        s += "\nPossible moves:";
        for (Move m : getMoves()) {
            s += "\n\t" + m;
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
            this.rowA = 8 - (cmd.charAt(1) - '0');
            this.colB = cmd.charAt(5) - 'A';
            this.rowB = 8 - (cmd.charAt(6) - '0');
        }

        @Override
        public String toString() {
            return new String(new byte[] { (byte) ('A' + colA) }) + (8 - rowA) + " - "
                 + new String(new byte[] { (byte) ('A' + colB) }) + (8 - rowB);
        }

        public static boolean isValid(String cmd) {
            return cmd != null && cmd.matches("^[A-H][1-8] - [A-H][1-8]$");
        }
    }
}
