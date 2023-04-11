package laboratoire4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client3 {

  private GameBoard gameBoard;

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
          gameBoard = new GameBoard(getBoardConfiguration(input));
          System.out.println(gameBoard);
          System.out.println(
            "Nouvelle partie! Vous jouez rouge, entrez votre premier coup : "
          );
          String move = null;
          move = console.readLine();
          output.write(move.getBytes(), 0, move.length());
          output.flush();
          break;
        case '2': // You have the second move
          gameBoard = new GameBoard(getBoardConfiguration(input));
          System.out.println(gameBoard);
          break;
        case '3': // New move
          aBuffer = new byte[16];
          size = input.available();
          System.out.println("size :" + size);
          input.read(aBuffer, 0, size);
          s = new String(aBuffer);
          System.out.println("Dernier coup :" + s);
          gameBoard.update(s);
          System.out.println(gameBoard);
          System.out.println("Entrez votre coup : ");
          move = null;
          move = console.readLine();
          gameBoard.update(move);
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

  private String getBoardConfiguration(BufferedInputStream input)
    throws IOException {
    byte[] aBuffer = new byte[1024];
    int size = input.available();
    input.read(aBuffer, 0, size);

    return new String(aBuffer).trim();
  }
}
