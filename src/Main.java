import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {

      Board b = new Board();
        //OnePlayerGame t = new OnePlayerGame(b, new MinimaxAgent(b, 3));
        TwoPlayerGame t = new TwoPlayerGame(b, new MinimaxAgent(b, 5), new MinimaxAgent(b, 2));
        t.play();

//        for(int r = 1; r <= 7; r++) {
//            for(int b = 1; b <= 7; b++) {
//                Board board = new Board();
//                System.out.println("r " + r + ", b " + b);
//                TwoPlayerGame t = new TwoPlayerGame(board, new MinimaxAgent(board, r), new MinimaxAgent(board, b));
//                t.play();
//            }
//        }
//
//        System.out.println(TwoPlayerGame.redCount);
//        System.out.println(TwoPlayerGame.blackCount);
//        System.out.println(TwoPlayerGame.tieCount);

    }
}
