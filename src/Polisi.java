import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Polisi extends Penjaga {
    private int[][] patrolPoints;
    private int patrolIndex = 0;
    private int speed = 2;
    private String arah = "kanan"; // default arah hadap

    public Polisi(int x, int y, int width, int height, String imagePath, int[][] patrolPoints) {
        super(x, y, width, height, loadImage(imagePath));
        this.patrolPoints = patrolPoints;
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading polisi: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update() {
        if (patrolPoints == null || patrolPoints.length == 0) return;
        int targetX = patrolPoints[patrolIndex][0];
        int targetY = patrolPoints[patrolIndex][1];
        int dx = targetX - x;
        int dy = targetY - y;
        int dist = (int)Math.sqrt(dx * dx + dy * dy);
        if (dist > speed) {
            x += speed * dx / dist;
            y += speed * dy / dist;
            // Update arah hadap berdasarkan gerakan
            if (Math.abs(dx) > Math.abs(dy)) {
                arah = dx > 0 ? "kanan" : "kiri";
            } else if (Math.abs(dy) > 0) {
                arah = dy > 0 ? "bawah" : "atas";
            }
        } else {
            x = targetX;
            y = targetY;
            patrolIndex = (patrolIndex + 1) % patrolPoints.length;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            double angle = 0;
            switch (arah) {
                case "kanan": angle = Math.PI / 2; break;
                case "kiri": angle = -Math.PI / 2; break;
                case "bawah": angle = Math.PI; break;
                case "atas": angle = 0; break;
            }
            g2d.translate(x + width / 2, y + height / 2);
            g2d.rotate(angle);
            g2d.drawImage(image, -width / 2, -height / 2, width, height, null);
            g2d.dispose();
        }
    }

    // Jika ingin mengatur arah hadap secara manual dari luar
    public void setArah(String arah) {
        this.arah = arah;
    }

    public String getArah() {
        return arah;
    }
}