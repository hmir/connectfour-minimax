import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class BoardGUI extends Canvas implements MouseListener, MouseMotionListener {

    public static final int WIDTH = 700; // width of Connect Four board
    public static final int HEIGHT = 620; // height of Connect Four board
    public static final int HEADER_HEIGHT = 40; // height of area above board

    private Board board; // represents current board state
    private OnePlayerGame game; // used to make player/agent moves

    private Image gameDisp; // image that is drawn onto window
    private Graphics bufferGraphics; // graphics object used to draw onto gameDisp

    private int hoveredColumn = -1; // column currently being hovered with the mouse

    public BoardGUI() {

        // initialize boar and game fields
        board = new Board();
        game = new OnePlayerGame(board, new MinimaxAgent(board, 7));

        // set background color
        setBackground(new Color(41, 128, 185));

        // do initial paint
        repaint();

        // add mouse and mouse motion listeners
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void paint(Graphics g) {

        // on first time painting, initialize gameDisk and bufferGraphics
        if(gameDisp == null) {
            gameDisp = createImage(WIDTH, HEIGHT + HEADER_HEIGHT);
            bufferGraphics = gameDisp.getGraphics();
        }

        // clear entire window
        bufferGraphics.clearRect(0, 0, WIDTH, HEIGHT + HEADER_HEIGHT);

        // print status of game onto window
        String status = "";
        if(board.isTie()) {
            status = "Tie Game!";
        }
        else if(board.getWinner() != null && board.getWinner().equals("r")) {
            status = "Red Wins!";
        }
        else if(board.getWinner() != null && board.getWinner().equals("b")) {
            status = "Black Wins!";
        }
        bufferGraphics.drawString(status, 20, 25);

        // instruct player if they have not made a move
        if(board.getChipsInPlay() == 0) {
            bufferGraphics.drawString("Click a column to drop a chip", WIDTH/2 - 100, 25);
        }

        // enable anti-aliasing
        ((Graphics2D) bufferGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // nested for loop draws all chips onto the board
        for(int i = 0; i < Board.HEIGHT; i++) {
            for(int j = 0; j < Board.WIDTH; j++) {
                try {
                    // draw red chip if the red player has made a move here
                    if(board.getState()[i][j] == 'r') {
                        bufferGraphics.drawImage(redChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                    // draw red chip if the black player has made a move here
                    else if(board.getState()[i][j] == 'b') {
                        bufferGraphics.drawImage(blackChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                    // draw yellow chip if column j is being hovered over and the game has not ended
                    else if(j == hoveredColumn && !board.isTie() && board.getWinner() == null) {
                        bufferGraphics.drawImage(yellowChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                    // draw white chip (representing an empty space) otherwise
                    else {
                        bufferGraphics.drawImage(whiteChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        // draw image onto window
        g.drawImage(gameDisp, 0, 0, this);

    }

    // the following methods returning BufferedImage are for accessing the icons for the various chip colors
    private BufferedImage redChip() throws IOException {
        return ImageIO.read(new File("res/red.png"));
    }

    private BufferedImage blackChip() throws IOException {
        return ImageIO.read(new File("res/black.png"));
    }

    private BufferedImage whiteChip() throws IOException {
        return ImageIO.read(new File("res/white.png"));
    }

    private BufferedImage yellowChip() throws IOException {
        return ImageIO.read(new File("res/yellow.png"));
    }

    public void update(Graphics g) {
        paint(g);
    }

    // update the hoveredColumn field if the mouse has moved
    public void mouseMoved(MouseEvent e) {
        boolean changed = false; // gets set to true if the value of hoveredColumn is changed

        // hovered column should be -1 if mouse is not on the board
        if(e.getY() < HEADER_HEIGHT) {
            if(hoveredColumn != -1) {
                changed = true;
            }
            hoveredColumn = -1;
        }
        else {
            // calculate which column is being hovered
            int newCol = (e.getX()-1)/100;
            if(newCol != hoveredColumn) {
                changed = true;
                hoveredColumn = newCol;
            }
        }

        // only repaint if the value of hoveredColumn has changed
        if(changed) {
            repaint();
        }
    }

    // mouseDragged behavior should emulate mouseMoved
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public void mouseReleased(MouseEvent e) {
        if(e.getY() < HEADER_HEIGHT || !game.isPlayerOneTurn() || board.isTie() || board.getWinner() != null) {
            return;
        }

        int col = (e.getX()-1)/100;

        game.makePlayerMove(col);
        if(board.getWinner() == null) {
            getGraphics().drawString("Opponent's Turn...", 20, 25);
            game.requestAgentMove();
        }
        repaint();

    }

    // if mouse leaves window, ensure hoveredColumn is set to -1
    public void mouseExited(MouseEvent e) {
        if(hoveredColumn != -1) {
            hoveredColumn = -1;
            repaint();
        }
    }

    // the below mouse event handlers require no implementation
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    public static void main(String[] args) {
        // instantiate JFrame and BoardGUI objects
        JFrame frame = new JFrame();
        BoardGUI gameBoard = new BoardGUI();

        // add gameBoard to frame and set frame properties
        frame.add(gameBoard);
        frame.setSize(BoardGUI.WIDTH, BoardGUI.HEIGHT + BoardGUI.HEADER_HEIGHT);
        frame.setTitle("Connect Four");
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
