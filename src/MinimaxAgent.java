import java.util.ArrayList;

public class MinimaxAgent extends Agent {
    private int depth;

    private char testC;

    public MinimaxAgent(Board board, int depth) {
        super(board);
        this.depth = depth;
        setColor('b');

    }

    public MinimaxAgent(Board board, char color, int depth) {
        super(board, color);
        this.depth = depth;
        setColor(color);
    }

    public int evalFn(Board board, char agentColor, char otherColor, int depth) {
        int reward = 0;

        if(board.getWinner() != null && board.getWinner().charAt(0) == agentColor) {
            return 999999 - depth;
        }
        else if(board.getWinner() != null && board.getWinner().charAt(0) == otherColor) {
            return -999999 + depth;
        }

        for(int key = 1; key <= 3; key++) {
            reward += Math.pow(10, key) * board.accessConsecutiveChips().get(agentColor).get(key);
        }

        for(int key = 1; key <= 3; key++) {
            reward -= Math.pow(10, key) * board.accessConsecutiveChips().get(otherColor).get(key);
        }

        return reward;
    }

    public char getAction() {
        ArrayList<Character> actions = getBoard().getLegalActions();
        ArrayList<Board> successors = getBoard().getSuccessors(getColor());

        int bestValue = Integer.MIN_VALUE;
        int bestIndex = 0;

        incrementCount();

        for(int i = 0; i < successors.size(); i++) {
            incrementCount();
//            System.out.println(actions.get(i));
            testC = actions.get(i);
            int val = minValue(successors.get(i), getOtherColor(), 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
//            System.out.println(testC + " value: " + val);
            if(bestValue < val) {
                bestValue = val;
                bestIndex = i;
            }
        }
        return actions.get(bestIndex);
    }

    public int minValue(Board board, char currentColor, int depth, int alpha, int beta) {
        if(board.isTie() || board.getWinner() != null || depth == this.depth) {
            return evalFn(board, getColor(), getOtherColor(), depth);
        }
        int v = Integer.MAX_VALUE;
        for(Board successor : board.getSuccessors(currentColor)) {
            incrementCount();
            int q = maxValue(successor, oppositeColor(currentColor), depth + 1, alpha, beta);
            if(depth == 1){
//                System.out.println("Min value, " + q);
//                System.out.println(successor);
//                evalFnm.out.println(successor.accessConsecutiveChips());
            }
            v = Math.min(v, q);
            beta = Math.min(v, beta);

            if(beta <= alpha) {
                break;
            }
        }
        return v;
    }

    public int maxValue(Board board, char currentColor, int depth, int alpha, int beta) {
        if(board.isTie() || board.getWinner() != null || depth == this.depth) {
            return evalFn(board, getColor(), getOtherColor(), depth);
        }
        int v = Integer.MIN_VALUE;
        for(Board successor : board.getSuccessors(currentColor)) {
            incrementCount();
            v = Math.max(v, minValue(successor, oppositeColor(currentColor), depth + 1, alpha, beta));
            alpha = Math.max(v, alpha);

            if(beta <= alpha) {
                break;
            }
        }
        return v;
    }

}
