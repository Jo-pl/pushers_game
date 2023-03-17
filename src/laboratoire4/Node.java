package laboratoire4;

import java.util.Stack;

public class Node {
    private int[][] board = new int[8][8];
    Stack<Node> children = new Stack<Node>();
    int score = 0;

    public int[][] getBoard(){
        return this.board;
    }

    public void setBoard(int[][] board){
        this.board = board;
    }

    public Stack<Node> getChildren(){
        return children;
    }

    public void setChildren(Stack<Node> children){
        this.children = children;
    }

    public int getScore(){
        return this.score;
    }

    public void calculateScore(){
        //TODO
    }

    public void addChildren(Node node){
        this.children.push(node);
    }
}