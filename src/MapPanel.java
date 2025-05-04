import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MapPanel extends JPanel {

    private Arena arena;

    public MapPanel(Arena arena) {
        this.arena = arena;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage map = arena.getMapImage();
        if (map != null) {
            g.drawImage(map, 0, 0, getWidth(), getHeight(), null);
        }
    }
}