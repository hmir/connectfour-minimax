import java.util.ArrayList;

/*
    Agent object which uses a minimax algorithm with alpha-
    beta pruning (to a given depth) to generate actions in
    getAction(). The evaluation function rates board states
    as more desirable when there are high-valued sequences
    of consecutive chips of the agent's color on the board
*/
public class MinimaxAgent extends Agent {

    private int depth; // max depth
    private final int MAX_BOARD_REWARD = 999999; // maximum value of a board state
    private final int MIN_BOARD_REWARD = -999999; // minimum value of a board state

    // default constructor sets agent's chip color to black
    public MinimaxAgent(Board board, int depth) {
        super(board);
        this.depth = depth;
        setColor('b');
    }

    // argument can be supplied to choose agent's chip color
    public MinimaxAgent(Board board, char color, int depth) {
        super(board, color);
        this.depth = depth;
        setColor(color);
    }

    // evaluation function to determine board state value for minimax function
    public int evalFn(Board board, char agentColor, char otherColor, int depth) {
        int reward = 0;

        // if the agent wins in a given board state, return MAX_BOARD_REWARD, and
        // subtract the depth to make more immediate states have greater value
        if(board.getWinner() != null && board.getWinner().charAt(0) == agentColor) {
            return MAX_BOARD_REWARD - depth;
        }

        // if the opponent wins in a given board state, return MIN_BOARD_REWARD, and
        // add the depth to make less immediate states have greater value
        else if(board.getWinner() != null && board.getWinner().charAt(0) == otherColor) {
            return MIN_BOARD_REWARD + depth;
        }

        // reward should be incremented (or decremented for opponent states) by 10^key * n, where
        // key is the length of the sequence of consecutive chips and n is the number of sequences
        // of that length which exist

        for(int key = 1; key <= 3; key++) {
            reward += Math.pow(10, key) * board.getConsecutiveChips().get(agentColor).get(key);
        }

        for(int key = 1; key <= 3; key++) {
            reward -= Math.pow(10, key) * board.getConsecutiveChips().get(otherColor).get(key);
        }

        return reward;
    }

    // uses minimax to determine the highest move (or "action") from all possible moves
    public char getAction() {

        resetCount(); // reset count field

        // list of all allowed moves that can be taken
        ArrayList<Character> actions = getBoard().getLegalActions();
        // list of all possible board states one move ahead
        ArrayList<Board> successors = getBoard().getSuccessors(getColor());

        int bestValue = Integer.MIN_VALUE; // best value of all states evaluated
        int bestIndex = 0; // index of best action that can be taken

        // iterate through all successor states
        for(int i = 0; i < successors.size(); i++) {

            incrementCount(); // increment number of states expanded

            // calculate minimum value of the successor states in terms of the opponent
            int val = minValue(successors.get(i), getOtherColor(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

            // check if val is greater than best value
            if(bestValue < val) {
                bestValue = val;
                bestIndex = i;
            }
        }

        return actions.get(bestIndex);
    }

    public int minValue(Board board, char currentColor, int depth, int alpha, int beta) {
        // if max depth reached or game has ended, simply return value of current board state
        if(board.isTie() || board.getWinner() != null || depth == this.depth) {
            return evalFn(board, getColor(), getOtherColor(), depth);
        }
        int v = Integer.MAX_VALUE;
        // determine mininmum value of successor states
        for(Board successor : board.getSuccessors(currentColor)) {
            incrementCount(); // increment number of states expanded

            v = Math.min(v, maxValue(successor, oppositeColor(currentColor), depth + 1, alpha, beta));

            // calculate beta and break if it's less than or equal to alpha
            beta = Math.min(v, beta);
            if(beta <= alpha) {
                break;
            }
        }
        return v;
    }

    public int maxValue(Board board, char currentColor, int depth, int alpha, int beta) {
        // if max depth reached or game has ended, simply return value of current board state
        if(board.isTie() || board.getWinner() != null || depth == this.depth) {
            return evalFn(board, getColor(), getOtherColor(), depth);
        }
        int v = Integer.MIN_VALUE;
        // determine maximum value of successor states
        for(Board successor : board.getSuccessors(currentColor)) {
            incrementCount(); // increment number of states expanded

            v = Math.max(v, minValue(successor, oppositeColor(currentColor), depth + 1, alpha, beta));

            // calculate alpha and break if it's greater than beta
            alpha = Math.max(v, alpha);
            if(beta <= alpha) {
                break;
            }
        }
        return v;
    }

}
