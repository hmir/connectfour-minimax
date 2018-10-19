import java.util.ArrayList;
import java.util.Random;

public class Agent {

    private char color;
    private char otherColor;
    private Board board;
    private int count = 0;


    public Agent(Board board) {
        color = 'b';
        this.board = board;
        this.otherColor = oppositeColor(color);
    }

    public Agent(Board board, char color) {
        this(board);
        this.color = color;
    }

    public char getAction() {
        return Board.letters[(new Random()).nextInt(7)];
    }

    public void setColor(char color) {
        this.color = color;
        this.otherColor = oppositeColor(color);

    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }

    public char getColor() {
        return color;
    }

    public char getOtherColor() {
        return otherColor;
    }

    public Board getBoard() {
        return board;
    }

    public char oppositeColor(char color) {
        if(color == 'r') {
            return 'b';
        }
        else {
            return 'r';
        }
    }
}
