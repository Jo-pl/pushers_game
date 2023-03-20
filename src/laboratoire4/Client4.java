package laboratoire4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;
import laboratoire4.Board.Move;

public class Client4 {

    public static void main(String[] args) throws IOException {
        try (Socket s = new Socket("localhost", 8888)) {
            BufferedInputStream input = new BufferedInputStream(s.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(s.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connected");
            Client4 client = new Client4();
            client.play(input, output, console);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(BufferedInputStream input, BufferedOutputStream output, BufferedReader console) throws IOException {

        byte[] aBuffer = new byte[1024];
        int size = 0;
        String s = null;
        String move = null;
        Board board = null, lastBoard = null;

        while (true) {
            char cmd = 0;
            cmd = (char) input.read();
            System.out.println("CMD: " + cmd);

            switch (cmd) {
                case '1': // New game (I'm player 1)
                case '2': // New game (I'm player 2)
                    aBuffer = new byte[1024];
                    size = input.available();
                    input.read(aBuffer, 0, size);
                    s = new String(aBuffer).trim();

                    board = new Board(s, true);

                    System.out.println(board);
                    if (cmd == '1') {
                        System.out.println("Nouvelle partie, joueur " + cmd + " (à vous!) :");
                        move = getMoveToPlay(board);
                        System.out.println(move);

                        lastBoard = board;
                        board = board.getNextMove(new Board.Move(move));
                        System.out.println(board);

                        output.write(move.getBytes(), 0, move.length());
                        output.flush();
                    }
                    break;

                case '3': // New move
                    aBuffer = new byte[16];
                    size = input.available();
                    input.read(aBuffer, 0, size);
                    s = new String(aBuffer);

                    lastBoard = board;
                    board = board.getNextMove(new Board.Move(s));

                    System.out.println("Dernier coup : " + s);
                    System.out.println(board);
                    System.out.println("Votre coup :");

                    move = getMoveToPlay(board);
                    System.out.println(move);

                    lastBoard = board;
                    board = board.getNextMove(new Board.Move(move));
                    System.out.println(board);
                    wait(1000);

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                    break;

                case '4': // Move is invalid
                    System.out.println("Coup invalide, veuillez entrer votre coup :");
                    System.exit(1);

                    board = lastBoard;
                    System.out.println(board);

                    move = getMoveToPlay(board);
                    System.out.println(move);

                    lastBoard = board;
                    board = board.getNextMove(new Board.Move(move));
                    System.out.println(board);

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                    break;

                case '5': // Game over
                    aBuffer = new byte[16];
                    size = input.available();
                    input.read(aBuffer, 0, size);
                    s = new String(aBuffer);

                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                    System.out.println(board);
                    lastBoard = null;
                    board = null;
                    break;

                default:
                    // The input cmd also print spaces
                    break;
            }
        }
    }

    public String getMoveToPlay(Board board) {
        double max = Double.NEGATIVE_INFINITY;
        ArrayList<Move> maxMoves = new ArrayList<Move>();
        boolean fp = board.isFirstPlayer();

        for (Move m : board.getMoves()) {
            Board b = board.getNextMove(m);

            // double h = MiniMax(b, fp, 4);
            double h = MiniMaxAlphaBeta(b, fp, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 4);

            if (h > max) {
                max = h;
                maxMoves.clear();
                maxMoves.add(m);
            } else if (h == max) {
                maxMoves.add(m);
            }
        }

        System.out.println("Best move: " + maxMoves.get(0) + " : " + max);
        return maxMoves.get(0 /*(int) (Math.random() * maxMoves.size())*/).toString();
    }

    public double MiniMax(Board board, boolean isFirstPlayer, int iterRemain) {
        double h = board.getHeuristic();
        if (h == 100 || h == -100 || iterRemain == 0) {
            return h;
        }

        if (board.isFirstPlayer() == isFirstPlayer) {
            // Max
            double maxScore = Double.NEGATIVE_INFINITY;
            for (Move m : board.getMoves()) {
                // The board automatically flips "isFirstPlayer()" on each move, no need to flip the player here
                double score = MiniMax(board.getNextMove(m), isFirstPlayer, iterRemain - 1);
                maxScore = Math.max(maxScore, score);
            }
            return maxScore;
        } else {
            // Min
            double minScore = Double.POSITIVE_INFINITY;
            for (Move m : board.getMoves()) {
                // The board automatically flips "isFirstPlayer()" on each move, no need to flip the player here
                double score = MiniMax(board.getNextMove(m), isFirstPlayer, iterRemain - 1);
                minScore = Math.min(minScore, score);
            }
            return minScore;
        }
    }

    public double MiniMaxAlphaBeta(Board board, boolean isFirstPlayer, double alpha, double beta, int iterRemain) {
        double h = board.getHeuristic();
        if (h == 100 || h == -100 || iterRemain == 0) {
            return h;
        }

        if (board.isFirstPlayer() == isFirstPlayer) {
            // Max
            double alpha1 = Double.NEGATIVE_INFINITY;
            for (Move m : board.getMoves()) {
                // The board automatically flips "isFirstPlayer()" on each move, no need to flip the player here
                double score = MiniMaxAlphaBeta(board.getNextMove(m), isFirstPlayer, Math.max(alpha, alpha1), beta, iterRemain - 1);
                alpha1 = Math.max(alpha1, score);
                if (alpha1 >= beta)
                    return alpha1;
            }
            return alpha1;
        } else {
            // Min
            double beta1 = Double.POSITIVE_INFINITY;
            for (Move m : board.getMoves()) {
                // The board automatically flips "isFirstPlayer()" on each move, no need to flip the player here
                double score = MiniMaxAlphaBeta(board.getNextMove(m), isFirstPlayer, alpha, Math.min(beta, beta1), iterRemain - 1);
                beta1 = Math.min(beta1, score);
                if (beta1 <= alpha)
                    return beta1;
            }
            return beta1;
        }
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
