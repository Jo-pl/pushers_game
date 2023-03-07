package laboratoire4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Hashtable;

public class Client2 {

    /*Might be useful someday
    enum tokenType {
        Case_vide,
        Petit_pion_noir,
        Pion_noir,
        Petit_pion_rouge,
        Pion_rouge,
    }
    */

    private Hashtable<Character,Integer> columnTableAssociation = new Hashtable<Character,Integer>();
    private int[][] board = new int[8][8];

    public static void main(String[] args) throws IOException {
        // char cmd = (char) input.read();
        Socket MyClient;
        try {
            MyClient = new Socket("localhost", 8888);
            BufferedInputStream input = new BufferedInputStream(MyClient.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connected");
            Client2 client = new Client2();
            client.initializeColumnTableAssociation();
            client.gameOn(input, output, console);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gameOn(BufferedInputStream input, BufferedOutputStream output, BufferedReader console) throws IOException {

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
                    System.out.println(s);
                    buildTable(s);
                    viewTable();
                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
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
                    System.out.println(s);
                    buildTable(s);
                    viewTable();
                    break;
                case '3': // New move
                    aBuffer = new byte[16];
                    size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer, 0, size);
                    s = new String(aBuffer);
                    System.out.println("Dernier coup :" + s);
                    updateTable(s);
                    System.out.println("Entrez votre coup : ");
                    move = null;
                    move = console.readLine();
                    updateTable(move);
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

    public void initializeColumnTableAssociation(){
        this.columnTableAssociation.put('A',1);
        this.columnTableAssociation.put('B',2);
        this.columnTableAssociation.put('C',3);
        this.columnTableAssociation.put('D',4);
        this.columnTableAssociation.put('E',5);
        this.columnTableAssociation.put('F',6);
        this.columnTableAssociation.put('G',7);
        this.columnTableAssociation.put('H',8);
    }

    public void viewTable() {
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                System.out.print(board[j][i]);
            }
            System.out.println();
        }
    }

    public void buildTable(String s) {
        System.out.println(s);
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

    public void updateTable(String s) {
        char[] currentMove = s.replaceAll("\\s+","").toCharArray();
        //New token
        this.board[this.columnTableAssociation.get(currentMove[3])-1][this.board[0].length-Character.getNumericValue(currentMove[4])] = 
            this.board[this.columnTableAssociation.get(currentMove[0])-1][this.board[0].length-Character.getNumericValue(currentMove[1])];
        //Delete the old token
        this.board[this.columnTableAssociation.get(currentMove[0])-1][this.board[0].length-Character.getNumericValue(currentMove[1])] = 0;
        viewTable();
    }
}
