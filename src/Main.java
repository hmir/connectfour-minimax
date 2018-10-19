public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        OnePlayerGame t = new OnePlayerGame(b, new MinimaxAgent(b, 3));
        t.play();
    }
}
