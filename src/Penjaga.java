import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Penjaga {
    protected int x, y, width, height;
    protected BufferedImage image;

    public Penjaga(int x, int y, int width, int height, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public abstract void update(); // Untuk pergerakan/AI

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    // Getter & Setter
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}