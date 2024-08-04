package Multiplayer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Objects;

public class Client {
    final int boardWidth = 600;
    final int boardHeight = 650;
    JFrame frame = new JFrame("Tic Tac Toe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton[][] board = new JButton[3][3];
    boolean gameOver = false;
    Socket socket;
    PrintWriter out;
    BufferedReader in;

    Client(String serverAddress) throws IOException {
        socket = new Socket(serverAddress, 12345);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

                int finalR = r;
                int finalC = c;
                tile.addActionListener(e -> {
                    if (gameOver) return;
                    JButton tile1 = (JButton) e.getSource();
                    if (Objects.equals(tile1.getText(), "")) {
                        out.println("MOVE " + finalR + " " + finalC);
                    }
                });
            }
        }

        new Thread(() -> {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line == null) continue;
                    System.out.println("Received update: " + line);
                    if (line.startsWith("UPDATE")) {
                        String[] parts = line.split(" ");
                        if (parts.length != 4) continue;
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        String player = parts[3];
                        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
                            board[row][col].setText(player);
                        }
                    } else if (line.startsWith("MESSAGE")) {
                        String message = line.substring(8);
                        textLabel.setText(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = JOptionPane.showInputDialog(
                "Enter Server Address:");
        new Client(serverAddress);
    }
}
