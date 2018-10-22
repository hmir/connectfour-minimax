import java.util.Scanner;

public class OnePlayerGame {
    private Board board;
    private Agent agent;
    private boolean playerOneTurn;
    private boolean gameFinished;

    public OnePlayerGame(Board board, Agent agent) {
        this.board = board;
        playerOneTurn = true;
        gameFinished = false;
        this.agent = agent;
        this.agent.setColor('b');
    }

    public void makePlayerMove(int column) {
        if(board.canAdd(column) && playerOneTurn) {
            board.add(column, agent.getOtherColor());
            playerOneTurn = !playerOneTurn;
            System.out.println(board);
        }
        else {
            System.out.println("Invalid input, please try again...");
        }
    }

    public void requestAgentMove() {
        double time = System.nanoTime();
        char agentAction = agent.getAction();
        double elapsedTime = System.nanoTime() - time;
        if (board.canAdd(agentAction) && !playerOneTurn) {
            board.add(agentAction, agent.getColor());
            playerOneTurn = !playerOneTurn;

            System.out.println(board);
            System.out.println("Elapsed time: " + elapsedTime / 1000000000);
            System.out.println("States Expanded: " + agent.getCount());
            System.out.println();
        }
    }

    public void play() {
        System.out.println(board);
        Scanner scanner = new Scanner(System.in);
        char color;
        while(board.getWinner() == null && !board.isTie()) {
            if(playerOneTurn) {
                color = 'r';
                System.out.print("Red's turn, choose a column: ");
                char selectedColumn = scanner.nextLine().trim().toUpperCase().charAt(0);
                if(board.canAdd(selectedColumn)) {
                    board.add(selectedColumn, color);
                    playerOneTurn = !playerOneTurn;

                    System.out.println(board);
                }
                else {
                    System.out.println("Invalid input, please try again...");
                }

            }
            else {
                color = 'b';
                double time = System.nanoTime();
                char agentAction = agent.getAction();
                double elapsedTime = System.nanoTime() - time;
                if(board.canAdd(agentAction)) {
                    board.add(agentAction, color);
                    playerOneTurn = !playerOneTurn;

                    System.out.println("Black's turn:");
                    System.out.println(board);
                    System.out.println("Elapsed time: " + elapsedTime / 1000000000);
                    System.out.println("States Expanded: " + agent.getCount());
                    System.out.println();

                }
            }
        }

        if(board.isTie()) {
            System.out.println("Tie Game!");
        }
        else if(board.getWinner().equals("r")) {
            System.out.println("Red wins!");
        }
        else {
            System.out.println("Black wins!");
        }

        gameFinished = true;

    }

    public boolean isPlayerOneTurn() {
        return playerOneTurn;
    }
}
