import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int boardWidth = 1280;
        int boardHeight = 720;

        JFrame frame = new JFrame("Robbery Bob");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RobberyBob robberyBob = new RobberyBob();
        frame.add(robberyBob);
        frame.pack(); // Optional: kalau kamu mau ukuran ngikutin panel (kalau tidak perlu, bisa dihapus)
        frame.setVisible(true); // Pindah di akhir
    }
}
