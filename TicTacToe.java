import javax.swing.*;
import java.awt.*;



@SuppressWarnings("CanBeFinal")
public class TicTacToe {
    final int boardWidth = 600;
    final int boardHeight = 650;

    JFrame frame = new JFrame("Tic Tac Toe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JButton[][] board = new JButton[3][3];
    final String playerX = "X";
    final String playerO = "O";
    String currentPlayer = playerX;

    boolean gameOver = false;
    int turns = 0;

    TicTacToe() {
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.darkGray);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial", Font.BOLD, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic-Tac-Toe");
        textLabel.setOpaque(true);


        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.darkGray);
        frame.add(boardPanel);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = new JButton();
                board[r][c] = tile;
                boardPanel.add(tile);

                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.white);
                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setFocusable(false);

                tile.addActionListener(e -> {
                    if (gameOver) return;
                    JButton tile1 = (JButton) e.getSource();
                    if (tile1.getText() == "") {
                        tile1.setText(currentPlayer);
                        turns++;
                        checkWinner();
                        if (!gameOver) {
                            currentPlayer = currentPlayer == playerX ? playerO : playerX;
                            textLabel.setText(currentPlayer + "'s turn.");
                        }
                    }

                });
            }
        }
    }


    /**
     * Checks for winners horizontally, diagonally, and vertical. If it finds a winning case, it sets
     * gameOver to True and calls setWinner to display the correct winner on the GUI
     * @date 1/8/2024
     * @author Tomas Bentolila
     */
     void checkWinner() {
        //horizontal
        for (int r = 0; r < 3; r++) {
            if (board[r][0].getText() == "") continue;

            if (board[r][0].getText() == board[r][1].getText() &&
                    board[r][1].getText() == board[r][2].getText()) {
                for (int i = 0; i < 3; i++) {
                    setWinner(board[r][i]);
                }
                gameOver = true;
                return;
            }
        }

        //vertical
        for (int c = 0; c < 3; c++) {
            if (board[0][c].getText() == "") continue;

            if (board[0][c].getText() == board[1][c].getText() &&
                    board[1][c].getText() == board[2][c].getText()) {
                for (int i = 0; i < 3; i++) {
                    setWinner(board[i][c]);
                }
                gameOver = true;
                showRestartDialog();
                return;
            }
        }

        //diagonally
        if (board[0][0].getText() == board[1][1].getText() &&
                board[1][1].getText() == board[2][2].getText() &&
                board[0][0].getText() != "") {
            for (int i = 0; i < 3; i++) {
                setWinner(board[i][i]);
            }
            gameOver = true;
            showRestartDialog();
            return;
        }

        //diagonally 2
        if (board[0][2].getText() == board[1][1].getText() &&
                board[1][1].getText() == board[2][0].getText() &&
                board[0][2].getText() != "") {
            setWinner(board[0][2]);
            setWinner(board[1][1]);
            setWinner(board[2][0]);
            gameOver = true;
            showRestartDialog();
            return;
        }

        if (turns == 9) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    setTie(board[r][c]);
                }
            }
            gameOver = true;
            showRestartDialog();
        }
    }

    /**
     * This method is called after checkWinner and is used to display the winner on the GUI
     * @param tile tiles to be changed color depending on how the user one
     * @author Tomas Bentolila
     * @date 1/8/2024
     */
    void setWinner(JButton tile) {
        tile.setForeground(Color.green);
        tile.setBackground(Color.gray);
        textLabel.setText(currentPlayer + " is the winner!");
    }
    /**
     * This method is called after checkWinner and is used to display that it is a tie on the GUI
     * @param tile tiles to be changed color depending on how the user one, since it is a tie all the tiles will be orange
     * @author Tomas Bentolila
     * @date 1/7/2024
     */
    void setTie(JButton tile) {
        tile.setForeground(Color.orange);
        tile.setBackground(Color.gray);
        textLabel.setText("Tie!");
    }

    /**
     * This method is used to display the restart game dialogue popup after the game has ended (tie or win).
     * If the user clicks Restart, it will call the restartGame() method to reset the game, if not it will close the application
     * @author Tomas Bentolila
     * @date 1/8/24
     */
    void showRestartDialog() {
        int option = JOptionPane.showOptionDialog(
                frame,
                "Do you want to restart the game?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Restart", "Exit"}, // Options
                "Restart" // Default option
        );

        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            // Handle exit or any other action
            System.exit(0);
        }
    }
    /**
     * This method is used to reset the game once the user clicks restart on the popup after a win or tie
     * it sets all variables to their default state and sets gameOver to false.
     * @author Tomas Bentolila
     * @date 1/8/24
     */
    void restartGame() {
        // Reset all game-related variables and UI components
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c].setText("");
                board[r][c].setBackground(Color.darkGray);
                board[r][c].setForeground(Color.white);
            }
        }

        currentPlayer = playerX;
        gameOver = false;
        turns = 0;

        textLabel.setText("Tic-Tac-Toe");
    }


    public static void main(String[] args) throws Exception {
        new TicTacToe();
    }
}