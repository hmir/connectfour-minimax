import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/*
    Each Board object acts a representation for one specific board
    state. They include functions to calculate the number of consecutive
    chips of a given color
*/
public class Board {
    // each letter represents a column on the board
    static char[] letters = {'A','B','C','D','E','F','G'};

    // HashMap which maps a column letter to its index in the "letters" array
    static HashMap<Character, Integer> letterDict = new HashMap<>();
    static {
        for(int i = 0; i < letters.length; i++) {
            letterDict.put(letters[i], i);
        }
    }

    public static final int WIDTH = 7; // number of columns
    public static final int HEIGHT = 6; // number of rows

    private String winner; // can either be "r", "b", or null
    private Boolean tie; // boolean value representing whether or not the game is tied

    private static final char[] colors = {'r','b'}; // possible chip colors

    // maps chip color ('r' or 'b') to another HashMap which maps the length
    // of a sequence of chips to the number of sequences of that length which
    // exist on the board
    private HashMap<Character, HashMap<Integer, Integer>> consecutiveChips;

    private int chipsInPlay; // number of chips currently on the board

    private char[][] state; // represents board, populated with values of 'r', 'b' or ' '

    // default constructor creates empty state array and initializes consecutiveChips
    public Board() {
        winner = null;
        tie = false;

        state = new char[HEIGHT][WIDTH];

        consecutiveChips = new HashMap<>();

        for(char color : colors) {
            HashMap<Integer, Integer> h1 = new HashMap<>();
            for(int i = 0; i <= WIDTH; i++) {
                h1.put(i, 0);
            }
            consecutiveChips.put(color, h1);
        }

        chipsInPlay = 0;

    }

    // constructor can be supplied with a state argument
    public Board(char[][] state) {
        this();
        this.state = state;
        // calculate consecutiveChips
        for(char color : colors) {
            consecutiveChips.put(color, calculateConsecutiveChips(color));
        }

        chipsInPlay = 0;

        // count number of chips in state and increment chipsInPlay accordingly
        for (char[] row : state) {
            for (char chip: row) {
                if(Arrays.asList(colors).contains(chip)) {
                    chipsInPlay++;
                }
            }
        }

    }

    // constructor can be supplied with a Board argument to create a deep copy
    private Board(Board oldBoard) {

        // create deep copy of fields from old board
        winner = oldBoard.getWinner();
        tie = oldBoard.isTie();
        chipsInPlay = oldBoard.getChipsInPlay();

        // create deep copy of state
        char[][] oldState = oldBoard.getState();
        state = new char[HEIGHT][WIDTH];
        for(int r = 0; r < HEIGHT; r++) {
            for(int c = 0; c < WIDTH; c++) {
                state[r][c] = oldState[r][c];
            }
        }

        // create deep copy of old consecutiveChips
        consecutiveChips = new HashMap<>();
        HashMap<Character, HashMap<Integer, Integer>> oldConsecutiveChips = oldBoard.getConsecutiveChips();
        for(Character c : oldConsecutiveChips.keySet()) {
            consecutiveChips.put(c, new HashMap<>());
            for(Integer consecutives : oldConsecutiveChips.get(c).keySet()) {
                consecutiveChips.get(c).put(consecutives, oldConsecutiveChips.get(c).get(consecutives));
            }
        }
    }

    // add chip of "color" to column of "columnLetter", return true if chip successfully added
    public boolean add(char columnLetter, char color) {
        int columnIndex = letterDict.get(columnLetter);
        return add(columnIndex, color);
    }

    // add chip of "color" to column of "columnIndex", return true if chip successfully added
    public boolean add(int columnIndex, char color) {
        boolean added = false;

        // add chip at requested column if possible
        for(int r = HEIGHT - 1; r >= 0; r--) {
            if(!Character.isLetter(state[r][columnIndex])) {
                state[r][columnIndex] = color;
                added = true;
                break;
            }
        }

        if(added) {
            // increment chips in play and recalculate consecutiveChips
            chipsInPlay++;
            for(char colr : colors) {
                consecutiveChips.put(colr, calculateConsecutiveChips(colr));
            }
            // check if game has tied
            if(chipsInPlay == WIDTH * HEIGHT && winner == null) {
                tie = true;
            }
        }
        return added;
    }

    // return true of chip can be added at column indicted by "columnLetter"
    public boolean canAdd(char columnLetter) {

        if(!letterDict.containsKey(columnLetter)) {
            return false;
        }

        int columnIndex = letterDict.get(columnLetter);
        return canAdd(columnIndex);
    }

    // return true of chip can be added at column indicted by "columnIndex"
    public boolean canAdd(int columnIndex) {

        if(!letterDict.containsValue(columnIndex)) {
            return false;
        }

        for(int r = HEIGHT - 1; r >= 0; r--) {
            if(!Character.isLetter(state[r][columnIndex])) {
                return true;
            }
        }
        return false;
    }

    // return list of possible columnLetters corresponding to columns where chips can be added
    public ArrayList<Character> getLegalActions() {
        ArrayList<Character> actions = new ArrayList<>();

        for(char letter : letters) {
            if(canAdd(letter)) {
                actions.add(letter);
            }
        }
        return actions;
    }

    // return deep copy of a board where a chip has been added at a certain column
    private Board successorAtColumn(char columnLetter, char color) {
        Board successorBoard = deepCopy(this);
        successorBoard.add(columnLetter, color);
        return successorBoard;

    }

    // return deep copies of all possible boards one move ahead
    public ArrayList<Board> getSuccessors(char color) {
        ArrayList<Board> successors = new ArrayList<>();
        for(char letter : getLegalActions()) {
            successors.add(successorAtColumn(letter, color));
        }

        return successors;
    }

    // return deep copy of a board
    public Board deepCopy(Board b) {
        return new Board(b);
    }

    // calculates the number of sequences of chips (of length 1, 2, 3, etc) of a given chip color
    private HashMap<Integer, Integer> calculateConsecutiveChips(char color) {
        // maps the length of a chip sequence to the number of those sequences that exist
        HashMap<Integer, Integer> consecutives = new HashMap<>();

        // initialize consecutives
        for(int i = 0; i <= WIDTH; i++) {
            consecutives.put(i, 0);
        }

        // the following HashSets contain all the positions whose row, column,
        // and left and right diagonals have been checked for consecutive chips...
        // these HashSets are used to avoid double counting sequences of chips
        HashSet<Position> rowChecked = new HashSet<>();
        HashSet<Position> colChecked = new HashSet<>();
        HashSet<Position> diagRightChecked = new HashSet<>();
        HashSet<Position> diagLeftChecked = new HashSet<>();

        // iterate through all board spaces and check for sequences of chips
        for(int r = 0; r < HEIGHT; r++) {
            for(int c = WIDTH - 1; c >= 0; c--) {

                // continue if current board space is empty
                if(state[r][c] != color) {
                    continue;
                }

                Position currentPos = new Position(r, c);

                // check for consecutive chips in the row, column, and diagonals of currentPos
                int val;
                if(!rowChecked.contains(currentPos)) {
                    val = checkRow(r, c, color, rowChecked);
                    consecutives.put(val, consecutives.get(val) + 1);
                }
                if(!colChecked.contains(currentPos)) {
                    val = checkCol(r, c, color, colChecked);
                    consecutives.put(val, consecutives.get(val) + 1);
                }
                if(!diagRightChecked.contains(currentPos)) {
                    val = checkDiagRight(r, c, color, diagRightChecked);
                    consecutives.put(val, consecutives.get(val) + 1);
                }
                if(!diagLeftChecked.contains(currentPos)) {
                    val = checkDiagLeft(r, c, color, diagLeftChecked);
                    consecutives.put(val, consecutives.get(val) + 1);
                }
            }
        }

        // sequences of length zero are irrelevant
        consecutives.remove(0);

        // check for sequences of 4 or more chips and declare a winner if one exists
        for(int i = 4; i <= WIDTH; i++) {
            if(consecutives.get(i) > 0) {
                winner = color + "";
            }
        }

        return consecutives;
    }

    // check if a there's a sequence of chips of color "color" to the right of the position given by (r, c)
    private int checkRow(int r, int c, char color, HashSet<Position> rowChecked) {
        int chipCount = 0; // number of chips in the sequence
        int emptyCount = 0; // number of empty spaces above the sequence which can be filled by next turn

        // count number of empty chips before this position
        for(int j = c - 1; j >= 0; j--) {
            if(state[r][j] != 0) {
                break;
            }

            if(!inBounds(r + 1, j) || state[r + 1][j] != 0) {
                emptyCount++;
            }
        }

        int currentC = c; // current column being checked

        // count number of chips of this color including and to the right of this position
        while(inBounds(r, currentC) && state[r][currentC] == color) {
            // add this position to rowChecked to avoid double checking this space in the future
            rowChecked.add(new Position(r, currentC));

            chipCount++;
            currentC++;
        }

        // count number of empty chips after sequence
        for(int j = currentC; j < WIDTH; j++) {
            // break if (r, j) is an empty space
            if(state[r][j] != 0) {
                break;
            }

            // if a chip can be added at (r, j) on the next turn, increment emptyCount
            if(!inBounds(r + 1, j) || state[r + 1][j] != 0) {
                emptyCount++;
            }
        }

        // return number of chips in sequence only if a sequence of 4 or more can be created
        if(chipCount + emptyCount >= 4) {
            return chipCount;
        }
        // return 0 otherwise
        else {
            return 0;
        }
    }

    // check if a there's a sequence of chips of color "color" above the position given by (r, c)
    private int checkCol(int r, int c, char color, HashSet<Position> colChecked) {

        int chipCount = 0; // number of chips in the sequence
        int emptyCount = 0; // number of empty spaces above the sequence which can be filled by next turn

        // iterate through board spaces in the column
        for(int i = HEIGHT-1; i >= 0; i--) {
            // increment chipCount if chip of given color is found
            if(state[i][c] == color) {
                // add this position to rowChecked to avoid double checking this space in the future
                colChecked.add(new Position(i, c));
                chipCount++;
            }
            // increment emptyCount if empty space is found
            else if(!Character.isLetter(state[i][c])) {
                emptyCount += 1;
            }
            // sequence is void if chip of opposite color is found
            else {
                chipCount = 0;
            }
        }

        // return number of chips in sequence only if a sequence of 4 or more can be created
        if(chipCount + emptyCount >= 4) {
            return chipCount;
        }
        // return 0 otherwise
        else {
            return 0;
        }
    }

    // check if a there's a sequence of chips of color "color" above and to the right of the position given by (r, c)
    private int checkDiagRight(int r, int c, char color, HashSet<Position> diagRightChecked) {

        // add this position to diagRightChecked
        diagRightChecked.add(new Position(r, c));

        int chipCount = 1; // number of chips in the sequence
        int emptyCount = 0; // number of empty spaces above the sequence which can be filled by next turn

        int currentC = c + 1; // current column being checked
        boolean emptyFound = false; // true if an empty space has been found in the diagonal

        // for loop checking the board diagonally right in the upwards direction
        for(int i = r - 1; i >= 0; i--) {
            // break if (i, currentC) is out of bounds)
            if(!inBounds(i, currentC)) {
                break;
            }
            // if sequence has not been broken by an empty space and current space is of specified color,
            // increment chipCount
            if(!emptyFound && state[i][currentC] == color) {
                // add this position to diagRightChecked to avoid double checking this space in the future
                diagRightChecked.add(new Position(i, currentC));
                chipCount++;
            }

            // if empty space is found, set emptyFound to true
            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
                // if a chip can be added to (i, currentC) on the next turn, increment emptyCount
                if(!inBounds(i + 1, currentC) || Character.isLetter(state[i + 1][currentC])) {
                    emptyCount++;
                }
            }
            else {
                break;
            }
            currentC++;
        }

        currentC = c - 1;
        emptyFound = false;

        // for loop checking the board diagonally right in the downwards direction
        for(int i = r + 1; i < HEIGHT; i++) {
            // break if (i, currentC) is out of bounds)
            if(!inBounds(i, currentC)) {
                break;
            }
            // if sequence has not been broken by an empty space and current space is of specified color,
            // increment chipCount
            if(!emptyFound && state[i][currentC] == color) {
                // add this position to diagRightChecked to avoid double checking this space in the future
                diagRightChecked.add(new Position(i, currentC));
                chipCount++;
            }
            // if empty space is found, set emptyFound to true
            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
                // if a chip can be added to (i, currentC) on the nextTurn, increment emptyCount
                if(!inBounds(i + 1, currentC) || Character.isLetter(state[i + 1][currentC])) {
                    emptyCount++;
                }
            }
            else {
                break;
            }
            currentC--;
        }

        // return number of chips in sequence only if a sequence of 4 or more can be created
        if(chipCount + emptyCount >= 4) {
            return chipCount;
        }
        // return 0 otherwise
        else {
            return 0;
        }
    }

    private int checkDiagLeft(int r, int c, char color, HashSet<Position> diagLeftChecked) {

        // add this position to diagLeftChecked
        diagLeftChecked.add(new Position(r, c));

        int chipCount = 1; // number of chips in the sequence
        int emptyCount = 0; // number of empty spaces above the sequence which can be filled by next turn

        int currentC = c - 1; // current column being checked
        boolean emptyFound = false; // true if an empty space has been found in the diagonal

        // for loop checking the board diagonally left in the upwards direction
        for(int i = r - 1; i >= 0; i--) {
            // break if (i, currentC) is out of bounds)
            if(!inBounds(i, currentC)) {
                break;
            }
            // if sequence has not been broken by an empty space and current space is of specified color,
            // increment chipCount
            if(!emptyFound && state[i][currentC] == color) {
                // add this position to diagLeftChecked to avoid double checking this space in the future
                diagLeftChecked.add(new Position(i, currentC));
                chipCount++;
            }
            // if empty space is found, set emptyFound to true
            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
                // if a chip can be added to (i, currentC) on the nextTurn, increment emptyCount
                if(!inBounds(i + 1, currentC) || Character.isLetter(state[i + 1][currentC])) {
                    emptyCount++;
                }
            }
            else {
                break;
            }
            currentC--;
        }

        currentC = c + 1;
        emptyFound = false;

        // for loop checking the board diagonally left in the downwards direction
        for(int i = r + 1; i < HEIGHT; i++) {
            // break if (i, currentC) is out of bounds)
            if(!inBounds(i, currentC)) {
                break;
            }
            // if sequence has not been broken by an empty space and current space is of specified color,
            // increment chipCount
            if(!emptyFound && state[i][currentC] == color) {
                // add this position to diagLeftChecked to avoid double checking this space in the future
                diagLeftChecked.add(new Position(i, currentC));
                chipCount++;
            }
            // if empty space is found, set emptyFound to true
            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
                // if a chip can be added to (i, currentC) on the nextTurn, increment emptyCount
                if(!inBounds(i + 1, currentC) || Character.isLetter(state[i + 1][currentC])) {
                    emptyCount++;
                }
            }
            else {
                break;
            }
            currentC++;
        }

        if(chipCount + emptyCount >= 4) {
            return chipCount;
        }
        else {
            return 0;
        }
    }

    // equals method returns true if states are equal
    public boolean equals(Object o) {
        return Arrays.deepEquals(state, ((Board) o).getState());
    }

    // returns state field
    public char[][] getState() {
        return state;
    }

    // prints state array as a WIDTH * HEIGHT grid
    public String toString() {
        String totalString = "";

        // print column letters as header
        String letterStr = "";
        for (char letter : letters) {
            letterStr += letter + " ";
        }

        totalString = letterStr + "\n";

        // print each space on the board
        for (int r = 0; r < HEIGHT; r++) {
            String rowStr = "";
            for (int c = 0; c < WIDTH; c++) {
                rowStr += state[r][c] + " ";
            }
            totalString += rowStr + "\n";
        }
        return totalString;
    }

    // returns true if the given board location exists
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < HEIGHT && c >= 0 && c < WIDTH;
    }

    // getters for fields

    public String getWinner() {
        return winner;
    }

    public boolean isTie() {
        return tie;
    }

    public HashMap<Character, HashMap<Integer, Integer>> getConsecutiveChips() {
        return consecutiveChips;
    }

    public int getChipsInPlay() {
        return chipsInPlay;
    }

}

/*
    Position object encodes a position on the board given by
    r (row index) and c (column index). Each position has a
    precomputed hash code so it can be used within HashMaps
*/
class Position {
    private int r;
    private int c;

    private static int[][] codes; // 2d array containing hash codes for each position

    // hash codes are pre-computed as a minor optimization
    static {
        codes = new int[Board.HEIGHT][Board.WIDTH];
        int num = 0;
        for(int i = 0; i < codes.length; i++) {
            for(int j = 0; j < codes[i].length; j++) {
                codes[i][j] = num++;
            }
        }
    }

    public Position(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public boolean equals(Object o) {
        Position other = (Position) o;
        return r == other.getR() && c == other.getC();
    }

    private int getR() {
        return r;
    }

    private int getC() {
        return c;
    }

    public int hashCode() {
        return codes[r][c];
    }
}
