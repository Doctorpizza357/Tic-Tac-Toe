import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

public class Main {
    private static JFrame frame;
    private static JButton multiplayerButton;
    private static boolean serverRunning = false;

    public static void main(String[] args) {
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton singlePlayerButton = new JButton("Single Player");
        multiplayerButton = new JButton("Multiplayer");
        JButton startServerButton = new JButton("Start Server");

        multiplayerButton.setEnabled(false);

        singlePlayerButton.addActionListener(e -> {
            frame.dispose();
            new TicTacToe();
        });

        multiplayerButton.addActionListener(e -> {
            if (serverRunning) {
                try {
                    InetAddress address = InetAddress.getLocalHost();
                    String hostIP = address.getHostAddress();
                    String hostName = address.getHostName();
                    frame.dispose();
                    Multiplayer.Client.main(new String[]{hostName, hostIP});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        startServerButton.addActionListener(e -> new Thread(() -> {
            try {
                serverRunning = true;
                multiplayerButton.setEnabled(true);
                JOptionPane.showMessageDialog(frame, "Server started successfully!");
                Multiplayer.Server.main(new String[]{});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to start server: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start());

        panel.add(singlePlayerButton);
        panel.add(multiplayerButton);
        panel.add(startServerButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}
