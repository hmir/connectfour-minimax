public class Main {
    public static void main(String[] args) {

//        char state[][] = {
//                {0, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 0, 'r', 'r', 'r'},
//                {0, 0, 0, 'r', 'b', 'b', 'b'}
//        };
//
//        Board test = new Board(state);
//        System.out.println(test);
//        System.out.println(test.getConsecutiveChips().get('r'));

        Board b = new Board();
        //OnePlayerGame t = new OnePlayerGame(b, new MinimaxAgent(b, 7));
        TwoAgentGame t = new TwoAgentGame(b, new MinimaxAgent(b, 5), new MinimaxAgent(b, 2));
        t.play();
    }
}
