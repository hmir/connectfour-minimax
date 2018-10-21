import java.util.Random;

/*
    The Agent class is an AI player which can be
    queried to make a move using getAction()
*/
public class Agent {

    private char color; // chip color of this agent
    private char otherColor; // chip color of opposing player
    private Board board; // board object which this agent is playing on
    private int count = 0; // total number of states agent has expanded in a search

    // default constructor sets agent's chip color to black
    public Agent(Board board) {
        color = 'b';
        this.board = board;
        this.otherColor = oppositeColor(color);
    }

    // argument can be supplied to choose agent's chip color
    public Agent(Board board, char color) {
        this(board);
        this.color = color;
    }

    // returns column that agent chooses to drop a chip
    public char getAction() {
        // for Agent objects, simply choose a random column
        return Board.letters[(new Random()).nextInt(7)];
    }

    // set chip color of agent object
    public void setColor(char color) {
        this.color = color;
        this.otherColor = oppositeColor(color);
    }

    // increment count field
    public void incrementCount() {
        count++;
    }

    // set count field to 0
    public void resetCount() {
        count = 0;
    }

    // getters for fields

    public int getCount() {
        return count;
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
