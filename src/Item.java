import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;


public abstract class Item {
    protected int x, y, width, height;
    protected int goldValue;
    protected BufferedImage image;

    public Item(int x, int y, int width, int height, int goldValue, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.goldValue = goldValue;
        this.image = image;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getGoldValue() {
        return goldValue;
    }

    public void draw(Graphics g, JPanel panel) {
        g.drawImage(image, x, y, width, height, panel);
    }
}
