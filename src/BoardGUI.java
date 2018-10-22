import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BoardGUI extends Applet implements MouseListener, MouseMotionListener {

    public static final int WIDTH = 700;
    public static final int HEIGHT = 620;
    public static final int HEADER_HEIGHT = 40;

    private Board board;
    private OnePlayerGame game;

    Graphics bufferGraphics;
    Image gameDisp;
    boolean initialized;

    private int hoveredColumn = -1;

    public void init() {

        board = new Board();
        game = new OnePlayerGame(board, new MinimaxAgent(board, 7));

        setBackground(new Color(41, 128, 185));
        gameDisp = createImage(WIDTH, HEIGHT + HEADER_HEIGHT);
        bufferGraphics = gameDisp.getGraphics();

        setLayout(null);
        initialized = true;
    }

    public void paint(Graphics g) {
        if(!initialized) {
            return;
        }
        bufferGraphics.clearRect(0, 0, WIDTH, HEIGHT + HEADER_HEIGHT);

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

        ((Graphics2D) bufferGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int i = 0; i < Board.HEIGHT; i++) {
            for(int j = 0; j < Board.WIDTH; j++) {
                try {
                    if(board.getState()[i][j] == 'r') {
                        bufferGraphics.drawImage(redChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                    else if(board.getState()[i][j] == 'b') {
                        bufferGraphics.drawImage(blackChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                    else if(j == hoveredColumn && !board.isTie() && board.getWinner() == null) {
                        bufferGraphics.drawImage(yellowChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                    else {
                        bufferGraphics.drawImage(whiteChip(), j * 100, i * 100 + HEADER_HEIGHT, this);
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        g.drawImage(gameDisp, 0, 0, this);

    }

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

    public void update(Graphics g)
    {
        paint(g);
    }

    public void mouseMoved(MouseEvent e) {

        boolean changed = false;
        if(e.getY() < HEADER_HEIGHT) {
            if (hoveredColumn != -1) {
                changed = true;
            }
            hoveredColumn = -1;
        }
        else {
            int newCol = (e.getX()-1)/100;
            if (newCol != hoveredColumn) {
                changed = true;
                hoveredColumn = newCol;
            }
        }
        if(hoveredColumn != -1 && !board.canAdd(hoveredColumn)) {
            hoveredColumn = -1;
            changed = true;
        }

        if (changed) {
            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        if(e.getY() < HEADER_HEIGHT || !game.isPlayerOneTurn() || board.isTie() || board.getWinner() != null) {
            return;
        }

        int col = (e.getX()-1)/100;

        game.makePlayerMove(col);
        getGraphics().drawString("Opponent's Turn...", 20, 25);
        game.requestAgentMove();
        repaint();

    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {

        JFrame frame = new JFrame();

        BoardGUI canvas = new BoardGUI();
        canvas.addMouseListener(canvas);
        canvas.addMouseMotionListener(canvas);

        frame.add(canvas);
        frame.setSize(BoardGUI.WIDTH, BoardGUI.HEIGHT + BoardGUI.HEADER_HEIGHT);
        frame.setTitle("Connect Four");
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.init();
        canvas.repaint();
        canvas.start();
    }
}
