import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Board {
    static char[] letters = {'A','B','C','D','E','F','G'};
    static HashMap<Character, Integer> letterDict = new HashMap<>();
    static {
        for(int i = 0; i < letters.length; i++) {
            letterDict.put(letters[i], i);
        }
    }

    private String winner;
    private Boolean tie;
    private static final int WIDTH = 7;
    private static final int HEIGHT = 6;
    private static final char[] colors = {'r','b'};

    private HashMap<Character, HashMap<Integer, Integer>> consecutiveChips;
    private int chipsInPlay;

    private char[][] state;

    public Board() {
        winner = null;
        tie = false;

        state = new char[HEIGHT][WIDTH];

        consecutiveChips = new HashMap<>();

        for(char color : colors) {
            HashMap<Integer, Integer> h1 = new HashMap<>();
            for(int i = 0; i <= 7; i++) {
                h1.put(i, 0);
            }
            consecutiveChips.put(color, h1);
        }

        chipsInPlay = 0;

    }

    public Board(char[][] state) {
        this();
        this.state = state;
        for(char colr : colors) {
//            System.out.println("Color being checked " + colr);
            consecutiveChips.put(colr, getConsecutiveChips(colr));
        }
    }

    private Board(Board oldBoard) {
        winner = oldBoard.getWinner();
        tie = oldBoard.isTie();

        char[][] oldState = oldBoard.getState();
        state = new char[HEIGHT][WIDTH];
        for(int r = 0; r < HEIGHT; r++) {
            for(int c = 0; c < WIDTH; c++) {
                state[r][c] = oldState[r][c];
            }
        }

        HashMap<Character, HashMap<Integer, Integer>> oldConsecutiveChips = oldBoard.accessConsecutiveChips();
        consecutiveChips = new HashMap<>();

        chipsInPlay = oldBoard.getChipsInPlay();

    }

    public boolean add(char columnLetter, char color) {
        int columnIndex = letterDict.get(columnLetter);
        boolean added = false;

        for(int r = HEIGHT - 1; r >= 0; r--) {
            if(!Character.isLetter(state[r][columnIndex])) {
                state[r][columnIndex] = color;
                added = true;
                break;
            }
        }

        if(added) {
            chipsInPlay++;
            for(char colr : colors) {
                consecutiveChips.put(colr, getConsecutiveChips(colr));
            }
            if(chipsInPlay == WIDTH * HEIGHT && winner == null) {
                tie = true;
            }
        }
        return added;
    }

    public boolean canAdd(char columnLetter) {

        if(!letterDict.containsKey(columnLetter)) {
            return false;
        }

        int columnIndex = letterDict.get(columnLetter);

        for(int r = HEIGHT - 1; r >= 0; r--) {
            if(!Character.isLetter(state[r][columnIndex])) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Character> getLegalActions() {
        ArrayList<Character> actions = new ArrayList<>();

        for(char letter : letters) {
            if(canAdd(letter)) {
                actions.add(letter);
            }
        }
        return actions;
    }

    private Board successorAtColumn(char columnLetter, char color) {
        Board successorBoard = deepCopy(this);
        successorBoard.add(columnLetter, color);
        return successorBoard;

    }

    public ArrayList<Board> getSuccessors(char color) {
        ArrayList<Board> successors = new ArrayList<>();
        for(char letter : getLegalActions()) {
            successors.add(successorAtColumn(letter, color));
        }

        return successors;
    }

    public Board deepCopy(Board b) {
        return new Board(b);
    }

    private HashMap<Integer, Integer> getConsecutiveChips(char color) {
        HashMap<Integer, Integer> consecutives = new HashMap<>();

        for(int i = 0; i <= 7; i++) {
            consecutives.put(i, 0);
        }

        HashSet<Position> rowChecked = new HashSet<>();
        HashSet<Position> colChecked = new HashSet<>();
        HashSet<Position> diagRightChecked = new HashSet<>();
        HashSet<Position> diagLeftChecked = new HashSet<>();

        for(int r = 0; r < HEIGHT; r++) {
            for(int c = WIDTH - 1; c >= 0; c--) {
                Position currentPos = new Position(r, c);
                if(state[r][c] != color) {
                    continue;
                }

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

        consecutives.remove(0);

        for(int i = 4; i <= 7; i++) {
            if(consecutives.get(i) > 0) {
                winner = color + "";
            }
        }

        return consecutives;
    }

    private int checkRow(int r, int c, char color, HashSet<Position> rowChecked) {
        int currentC = c;

        int chipCount = 0;
        int emptyCount = 0;

        for(int j = c - 1; j >= 0; j--) {
            if(state[r][j] != 0) {
                break;
            }

            if(!inBounds(r + 1, j) || state[r + 1][j] != 0) {
                emptyCount++;
            }
        }

        while(inBounds(r, currentC) && state[r][currentC] == color) {
            rowChecked.add(new Position(r, currentC));
            chipCount++;
            currentC++;
        }

        for(int j = currentC; j < WIDTH; j++) {
            if(state[r][j] != 0) {
                break;
            }

            if(!inBounds(r + 1, j) || state[r + 1][j] != 0) {
                emptyCount++;
            }
        }

        if(chipCount + emptyCount >= 4) {
//            System.out.println("row");
//            System.out.println(r + "," + c + ": " + chipCount);
            return chipCount;
        }
        else {
            return 0;
        }
    }

    private int checkCol(int r, int c, char color, HashSet<Position> colChecked) {

        int chipCount = 0;
        int emptyCount = 0;

        for(int i = HEIGHT-1; i >= 0; i--) {
            if(state[i][c] == color) {
                colChecked.add(new Position(i, c));
                chipCount++;
            }
            else if(!Character.isLetter(state[i][c])) {
                emptyCount += 1;
            }
            else {
                chipCount = 0;
            }
        }

        if(chipCount + emptyCount >= 4) {
//            System.out.println("col");
//            System.out.println(r + "," + c + ": " + chipCount);
            return chipCount;
        }
        else {
            return 0;
        }
    }

    private int checkDiagRight(int r, int c, char color, HashSet<Position> diagRightChecked) {

        diagRightChecked.add(new Position(r, c));

        int chipCount = 1;
        int emptyCount = 0;

        int currentC = c + 1;
        boolean emptyFound = false;

        for(int i = r - 1; i >= 0; i--) {
            if(!inBounds(i, currentC)) {
                break;
            }
            if(!emptyFound && state[i][currentC] == color) {
                diagRightChecked.add(new Position(i, currentC));
                chipCount++;
            }

            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
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

        for(int i = r + 1; i < HEIGHT; i++) {
            if(!inBounds(i, currentC)) {
                break;
            }
            if(!emptyFound && state[i][currentC] == color) {
                diagRightChecked.add(new Position(i, currentC));
                chipCount++;
            }
            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
                if(!inBounds(i + 1, currentC) || Character.isLetter(state[i + 1][currentC])) {
                    emptyCount++;
                }
            }
            else {
                break;
            }
            currentC--;
        }

        if(chipCount + emptyCount >= 4) {
//            System.out.println("diagRight");
//            System.out.println(r + "," + c + ": " + chipCount);
            return chipCount;
        }
        else {
            return 0;
        }
    }

    private int checkDiagLeft(int r, int c, char color, HashSet<Position> diagLeftChecked) {

        diagLeftChecked.add(new Position(r, c));

        int chipCount = 1;
        int emptyCount = 0;

        int currentC = c - 1;
        boolean emptyFound = false;

        for(int i = r - 1; i >= 0; i--) {
            if(!inBounds(i, currentC)) {
                break;
            }
            if(!emptyFound && state[i][currentC] == color) {
                diagLeftChecked.add(new Position(i, currentC));
                chipCount++;
            }

            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
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

        for(int i = r + 1; i < HEIGHT; i++) {
            if(!inBounds(i, currentC)) {
                break;
            }
            if(!emptyFound && state[i][currentC] == color) {
                diagLeftChecked.add(new Position(i, currentC));
                chipCount++;
            }
            else if(!Character.isLetter(state[i][currentC])) {
                emptyFound = true;
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
//            System.out.println("diagLeft");
//            System.out.println(r + "," + c + ": " + chipCount);
            return chipCount;
        }
        else {
            return 0;
        }
    }

    public void equals(Board other) {
        Arrays.deepEquals(state, other.getState());
    }

    public char[][] getState() {
        return state;
    }

    public String toString() {
        String totalString = "";
        String letterStr = "";
        for (char letter : letters) {
            letterStr += letter + " ";
        }

        totalString = letterStr + "\n";
        for (int r = 0; r < HEIGHT; r++) {
            String rowStr = "";
            for (int c = 0; c < WIDTH; c++) {
                rowStr += state[r][c] + " ";
            }
            totalString += rowStr + "\n";
        }
        return totalString;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < HEIGHT && c >= 0 && c < WIDTH;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isTie() {
        return tie;
    }

    public HashMap<Character, HashMap<Integer, Integer>> accessConsecutiveChips() {
        return consecutiveChips;
    }

    public int getChipsInPlay() {
        return chipsInPlay;
    }

}

class Position {
    private int r;
    private int c;

    private static int[][] codes;

    static {
        codes = new int[10][10];
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
        Position other = (Position)o;
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
