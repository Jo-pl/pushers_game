package laboratoire4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;

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

            switch (cmd) {
                case '1': // New game (I'm player 1)
                case '2': // New game (I'm player 2)
                    aBuffer = new byte[1024];
                    size = input.available();
                    input.read(aBuffer, 0, size);
                    s = new String(aBuffer).trim();

                    board = new Board(s, cmd == 1);

                    System.out.println(board);
                    if (cmd == '1') {
                        System.out.println("Nouvelle partie, joueur " + cmd + " (à vous!) :");
                        do {
                            move = console.readLine();
                        } while (!Board.Move.isValid(move));

                        lastBoard = board;
                        board = board.getNextMove(new Board.Move(move));

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

                    do {
                        move = console.readLine();
                    } while (!Board.Move.isValid(move));

                    lastBoard = board;
                    board = board.getNextMove(new Board.Move(move));

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                    break;

                case '4': // Move is invalid
                    System.out.println("Coup invalide, veuillez entrer votre coup :");

                    board = lastBoard;
                    do {
                        move = console.readLine();
                    } while (!Board.Move.isValid(move));

                    lastBoard = board;
                    board = board.getNextMove(new Board.Move(move));

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                    break;

                case '5': // Game over
                    aBuffer = new byte[16];
                    size = input.available();
                    input.read(aBuffer, 0, size);
                    s = new String(aBuffer);

                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                    lastBoard = null;
                    board = null;
                    break;

                default:
                    // The input cmd also print spaces
                    break;
            }
        }

    }
}
