import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Robbery Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null); // center window
            HomePanel homePanel = new HomePanel(frame); // ✅ constructor butuh frame
            frame.setContentPane(homePanel);
            frame.setVisible(true);
        });
    }
}