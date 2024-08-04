package Multiplayer;

import java.io.*;
import java.net.*;
import java.util.*;

@SuppressWarnings("CallToPrintStackTrace")
public class Server {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final String[][] board = new String[3][3];
    private static String currentPlayer = "X";
    private static boolean gameStarted = false;

    public static void main(String[] args) {
        System.out.println("Tic-Tac-Toe Server starting...");
        resetBoard();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started and listening on port " + PORT);

            while(true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                if (clients.size() == 2) {
                    gameStarted = true;
                    notifyPlayers("Game started! X's turn.");
                } else {
                    notifyPlayers("Waiting for another player to join...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resetBoard() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c] = null;
            }
        }
        currentPlayer = "X";
    }

    private static void notifyPlayers(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String command = in.readLine();
                    if (command == null) continue;
                    System.out.println("Received command: " + command);

                    if (gameStarted && command.startsWith("MOVE")) {
                        String[] parts = command.split(" ");
                        if (parts.length != 3) continue;
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
                            if (makeMove(row, col)) {
                                broadcastBoard();
                                if (checkWin()) {
                                    notifyPlayers(currentPlayer + " wins!");
                                    gameStarted = false;
                                } else if (checkDraw()) {
                                    notifyPlayers("It's a draw!");
                                    gameStarted = false;
                                } else {
                                    currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                                    notifyPlayers(currentPlayer + "'s turn.");
                                }
                            }
                        }
                    } else if (command.startsWith("RESET")) {
                        resetBoard();
                        broadcastBoard();
                        notifyPlayers("Board reset. Waiting for another player to join...");
                        gameStarted = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean makeMove(int row, int col) {
            if (board[row][col] == null && currentPlayer.equals(getCurrentPlayer())) {
                board[row][col] = currentPlayer;
                return true;
            }
            return false;
        }

        private String getCurrentPlayer() {
            return clients.indexOf(this) % 2 == 0 ? "X" : "O";
        }

        private void broadcastBoard() {
            for (ClientHandler client : clients) {
                client.sendBoard();
            }
        }

        private void sendBoard() {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    out.println("UPDATE " + r + " " + c + " " + (board[r][c] == null ? "" : board[r][c]));
                }
            }
        }

        private void sendMessage(String message) {
            out.println("MESSAGE " + message);
        }

        private boolean checkWin() {
            // Check rows and columns
            for (int i = 0; i < 3; i++) {
                if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                    return true;
                }
                if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) {
                    return true;
                }
            }
            // Check diagonals
            if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
                return true;
            }
            return board[0][2] != null && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]);
        }

        private boolean checkDraw() {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board[r][c] == null) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
