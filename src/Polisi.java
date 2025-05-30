import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Polisi {
    private int x, y, width, height;
    private BufferedImage polisi;

    public Polisi(int x, int y, int width, int height, String polisiPath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        try {
            polisi = ImageIO.read(new File(polisiPath));
        } catch (IOException e) {
            System.out.println("Error loading polisi: " + e.getMessage());
        }
    }

    public void draw(Graphics g){
        if(polisi != null){
            g.drawImage(polisi, x, y, width, height, null);
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getPolisi() {
        return polisi;
    }

    public void setPolisi(BufferedImage polisi) {
        this.polisi = polisi;
    }

    public void setImage(String path) {
        try {
            this.polisi = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading polisi: " + e.getMessage());
        }
    }
}