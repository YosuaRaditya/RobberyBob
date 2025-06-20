import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //menjamin UI thread khusus, mencegah error saat update tampilan
            GameData.audioPlayer = new MusicPlayer();
            GameData.audioOn = true;
            GameData.audioPlayer.play("RobberyBob/Assets/Trouble Makers (Loopable).wav");

            JFrame frame = new JFrame("Robbery Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //agar aplikasi berhenti saat window ditutup
            frame.setResizable(false); //mengunci ukuran window agar tidak bisa diubah
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null); // center window
            HomePanel homePanel = new HomePanel(frame); // ✅ constructor butuh frame
            frame.setContentPane(homePanel); //mengganti konten frame dengan HomePanel
            frame.setVisible(true); //menampilkan frame
        });
    }
}
