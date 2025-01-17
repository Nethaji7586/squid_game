import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;

public class TicTac {

    private static final int SIZE = 3;
    private static JButton[][] buttons = new JButton[SIZE][SIZE];
    private static char currentPlayer = 'X';
    private static boolean gameOver = false;
    private static boolean isAI = false;
    private static Image backgroundImage;
    private static ImageIcon xIcon;
    private static ImageIcon oIcon;

    private static final Color modebtn = new Color(250, 71, 171);
    private static String xpath="x.png";
    private static String opath="o.png";
    private static final Color FRAME_BACKGROUND_COLOR = new Color(205, 76, 81);
    private static final Color BUTTON_BORDER_COLOR = Color.BLACK;
    private static final String BACKGROUND_IMAGE_PATH = "background.jpg";
    private static Font BUTTON_FONT;

    public static void main(String[] args) {
        try {
            File fontFile = new File("semi.ttf");
            BUTTON_FONT = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(20f);
        } catch (IOException | FontFormatException e) {
            BUTTON_FONT = new Font("Arial", Font.PLAIN, 60);
            System.err.println("Error loading custom font: " + e.getMessage());
        }

        try {
            backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading background image!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try {
            xIcon = new ImageIcon(ImageIO.read(new File(xpath)).getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            oIcon = new ImageIcon(ImageIO.read(new File(opath)).getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading X or O images!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JFrame frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(415, 485);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.getContentPane().setBackground(FRAME_BACKGROUND_COLOR);

        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage));
        backgroundLabel.setLayout(new GridLayout(SIZE, SIZE));

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 400));

        backgroundLabel.setBounds(0, 0, 400, 400);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel panel = new JPanel(new GridLayout(SIZE, SIZE));
        panel.setOpaque(false);
        panel.setBounds(0, 0, 400, 400);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(BUTTON_FONT);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setOpaque(false);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(BUTTON_BORDER_COLOR));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                panel.add(buttons[i][j]);
            }
        }

        JButton modeButton = new JButton("Switch to AI");
        modeButton.setBackground(modebtn);
        modeButton.setFont(BUTTON_FONT);
        modeButton.setBorder(new LineBorder(Color.BLACK, 2));
        modeButton.setPreferredSize(new Dimension(200, 50));

        modeButton.addActionListener(e -> {
            isAI = !isAI;
            modeButton.setText(isAI ? "SINGLE" : "MULTIPLAYER");
            resetGame();
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setPreferredSize(new Dimension(400, 50));
        centerPanel.add(modeButton);

        layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    static class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getIcon() == null && !gameOver) {
                buttons[row][col].setIcon(currentPlayer == 'X' ? xIcon : oIcon);
                if (checkWin()) {
                    JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                    gameOver = true;
                    Timer timer = new Timer(2000, event -> resetGame());
                    timer.setRepeats(false);
                    timer.start();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(null, "It's a draw!");
                    gameOver = true;
                    Timer timer = new Timer(2000, event -> resetGame());
                    timer.setRepeats(false);
                    timer.start();
                }
                switchPlayer();

                if (isAI && !gameOver) {
                    makeAIMove();
                }
            }
        }
    }

    private static void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private static void resetGame() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j].setIcon(null);
            }
        }
        currentPlayer = 'X';
        gameOver = false;
    }

    private static boolean checkWin() {
        for (int i = 0; i < SIZE; i++) {
            if (buttons[i][0].getIcon() != null &&
                buttons[i][0].getIcon().equals(buttons[i][1].getIcon()) &&
                buttons[i][1].getIcon().equals(buttons[i][2].getIcon())) {
                return true;
            }
            if (buttons[0][i].getIcon() != null &&
                buttons[0][i].getIcon().equals(buttons[1][i].getIcon()) &&
                buttons[1][i].getIcon().equals(buttons[2][i].getIcon())) {
                return true;
            }
        }
        if (buttons[0][0].getIcon() != null &&
            buttons[0][0].getIcon().equals(buttons[1][1].getIcon()) &&
            buttons[1][1].getIcon().equals(buttons[2][2].getIcon())) {
            return true;
        }
        if (buttons[0][2].getIcon() != null &&
            buttons[0][2].getIcon().equals(buttons[1][1].getIcon()) &&
            buttons[1][1].getIcon().equals(buttons[2][0].getIcon())) {
            return true;
        }
        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (buttons[i][j].getIcon() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void makeAIMove() {
        int[] bestMove = minimax();
        buttons[bestMove[0]][bestMove[1]].setIcon(currentPlayer == 'X' ? xIcon : oIcon);
        if (checkWin()) {
            JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
            gameOver = true;
            Timer timer = new Timer(2000, event -> resetGame());
            timer.setRepeats(false);
            timer.start();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(null, "It's a draw!");
            gameOver = true;
            Timer timer = new Timer(2000, event -> resetGame());
            timer.setRepeats(false);
            timer.start();
        } else {
            switchPlayer();
        }
    }

    private static int[] minimax() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (buttons[i][j].getIcon() == null) {
                    buttons[i][j].setIcon(currentPlayer == 'X' ? xIcon : oIcon);
                    int score = minimaxScore(false);
                    buttons[i][j].setIcon(null);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    private static int minimaxScore(boolean isMaximizing) {
        if (checkWin()) {
            return isMaximizing ? -1 : 1;
        }
        if (isBoardFull()) {
            return 0;
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (buttons[i][j].getIcon() == null) {
                    buttons[i][j].setIcon(isMaximizing ? xIcon : oIcon);
                    int score = minimaxScore(!isMaximizing);
                    buttons[i][j].setIcon(null);

                    bestScore = isMaximizing ? Math.max(bestScore, score) : Math.min(bestScore, score);
                }
            }
        }

        return bestScore;
    }
}
