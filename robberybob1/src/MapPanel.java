import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    protected Image mapImage;

    public MapPanel(String imagePath) {
        setPreferredSize(new Dimension(1280, 720));
        loadImage(imagePath);
    }

    private void loadImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        mapImage = icon.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.drawString("Gambar tidak ditemukan", 50, 50);
        }
    }
}
