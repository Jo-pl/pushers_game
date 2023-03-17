package laboratoire4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;

public class Client3 {

  private int[][] board = new int[8][8];

  public static void main(String[] args) throws IOException {
    // char cmd = (char) input.read();
    Socket MyClient;
    try {
      MyClient = new Socket("localhost", 8888);
      BufferedInputStream input = new BufferedInputStream(
        MyClient.getInputStream()
      );
      BufferedOutputStream output = new BufferedOutputStream(
        MyClient.getOutputStream()
      );
      BufferedReader console = new BufferedReader(
        new InputStreamReader(System.in)
      );
      System.out.println("Connected");
      Client3 client = new Client3();
      client.play(input, output, console);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void play(
    BufferedInputStream input,
    BufferedOutputStream output,
    BufferedReader console
  ) throws IOException {
    byte[] aBuffer = new byte[1024];
    int size = 0;
    String s = null;

    while (true) {
      char cmd = 0;
      cmd = (char) input.read();
      // System.out.println(cmd);
      switch (cmd) {
        case '1': // You have the first move
          aBuffer = new byte[1024];
          size = input.available();
          // System.out.println("size " + size);
          input.read(aBuffer, 0, size);
          s = new String(aBuffer).trim();
          /* System.out.println(s); */
          buildBoard(s);
          printBoard();
          System.out.println(
            "Nouvelle partie! Vous jouer blanc, entrez votre premier coup : "
          );
          String move = null;
          move = console.readLine();
          output.write(move.getBytes(), 0, move.length());
          output.flush();
          break;
        case '2': // You have the second move
          aBuffer = new byte[1024];
          size = input.available();
          // System.out.println("size " + size);
          input.read(aBuffer, 0, size);
          s = new String(aBuffer).trim();
          /* System.out.println(s); */
          buildBoard(s);
          printBoard();
          break;
        case '3': // New move
          aBuffer = new byte[16];
          size = input.available();
          System.out.println("size :" + size);
          input.read(aBuffer, 0, size);
          s = new String(aBuffer);
          System.out.println("Dernier coup :" + s);
          updateBoard(s);
          System.out.println("Entrez votre coup : ");
          move = null;
          move = console.readLine();
          updateBoard(move);
          output.write(move.getBytes(), 0, move.length());
          output.flush();
          System.out.println("end of cmd3");
          break;
        case '4': //Move is invalid
          System.out.println("Coup invalide, entrez un nouveau coup : ");
          move = null;
          move = console.readLine();
          output.write(move.getBytes(), 0, move.length());
          output.flush();
          break;
        case '5': //Game over
          aBuffer = new byte[16];
          size = input.available();
          input.read(aBuffer, 0, size);
          s = new String(aBuffer);
          System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
          move = null;
          move = console.readLine();
          output.write(move.getBytes(), 0, move.length());
          output.flush();
          break;
        default:
          // The input cmd also print spaces
          break;
      }
    }
  }

  public void printBoard() {
    for (int col = 0; col < board.length; col++) {
      for (int line = 0; line < board[0].length; line++) {
        System.out.print(this.board[line][col]);
      }
      System.out.println();
    }
  }

  public void buildBoard(String s) {
    //System.out.println(s);
    String[] boardValues;
    boardValues = s.split(" ");
    int x = 0, y = 0;
    for (int i = 0; i < boardValues.length; i++) {
      board[x][y] = Integer.parseInt(boardValues[i]);
      x++;
      if (x == 8) {
        x = 0;
        y++;
      }
    }
  }

  public void updateBoard(String s) {
    char[] lastMove = s.replaceAll("[-\\s+\u0000]", "").toCharArray();
    //New token
    this.board[getAlphabetPosition(lastMove[2]) - 1][this.board[0].length -
      Character.getNumericValue(lastMove[3])] =
      this.board[getAlphabetPosition(lastMove[0]) - 1][this.board[0].length -
        Character.getNumericValue(lastMove[1])];
    //Delete the old token
    this.board[getAlphabetPosition(lastMove[0]) - 1][this.board[0].length -
      Character.getNumericValue(lastMove[1])] =
      0;
    printBoard();
  }

  public int getAlphabetPosition(char letter) {
    return Character.toUpperCase(letter) - 64;
  }
}
