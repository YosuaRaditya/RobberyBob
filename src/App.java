import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameData.audioPlayer = new MusicPlayer();
            GameData.audioOn = true;
            GameData.audioPlayer.play("RobberyBob/Assets/Trouble Makers (Loopable).wav");

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
