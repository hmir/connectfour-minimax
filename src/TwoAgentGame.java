public class TwoAgentGame {

    private Board board;
    private boolean playerOneTurn;
    private boolean gameFinished;

    private Agent agent1;
    private Agent agent2;

    public TwoAgentGame(Board board, Agent agent1, Agent agent2) {
        this.board = board;
        playerOneTurn = true;
        gameFinished = false;

        this.agent1 = agent1;
        this.agent2 = agent2;

        agent1.setColor('r');
        agent2.setColor('b');
    }

    public void play() {
        char color;
        while(board.getWinner() == null && !board.isTie()) {
            if(playerOneTurn) {
                color = 'r';
                double time = System.nanoTime();
                char agentAction = agent1.getAction();
                double elapsedTime = System.nanoTime() - time;
                if(board.canAdd(agentAction)) {
                    board.add(agentAction, color);
                    playerOneTurn = !playerOneTurn;

                    System.out.println("Red's turn:");
                    System.out.println(board);
                    System.out.println("Elapsed time: " + elapsedTime / 1000000000);
                    System.out.println("States Expanded: " + agent1.getCount());
                    System.out.println();

                }

            }
            else {
                color = 'b';
                double time = System.nanoTime();
                char agentAction = agent2.getAction();
                double elapsedTime = System.nanoTime() - time;
                if(board.canAdd(agentAction)) {
                    board.add(agentAction, color);
                    playerOneTurn = !playerOneTurn;

                    System.out.println("Black's turn:");
                    System.out.println(board);
                    System.out.println("Elapsed time: " + elapsedTime / 1000000000);
                    System.out.println("States Expanded: " + agent2.getCount());
                    System.out.println();

                }
            }
        }

        if(board.isTie()) {
            System.out.println("Tie Game!");
            System.out.println();
        }
        else if(board.getWinner().equals("r")) {
            System.out.println("Red wins!");
            System.out.println();
        }
        else {
            System.out.println("Black wins!");
            System.out.println();
        }

        gameFinished = true;

    }
}
