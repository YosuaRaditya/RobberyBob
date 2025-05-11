import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ArenaD extends Arena {
    private BufferedImage mapImage;
    private JButton backButton;

    public ArenaD(JFrame frame) {
        setLayout(null);
        try {
            mapImage = ImageIO.read(new File("RobberyBob/Assets/mapArenaD.jpg"));
        } catch (IOException e) {
            System.out.println("Gagal load map Arena A: " + e.getMessage());
        }

        // Tombol Back
        backButton = new JButton("Back");
        backButton.setBounds(30, 30, 100, 40);
        backButton.addActionListener(e -> {
            frame.setContentPane(new HomePanel(frame));
            frame.revalidate();
            frame.repaint();
        });
        add(backButton);
    }

    @Override
    public BufferedImage getMapImage() {
        return mapImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
