import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public abstract class Item {
    protected int x, y, width, height;
    protected int goldValue;
    protected String jenis;
    protected BufferedImage image;

    protected BufferedImage[] shineFrames = new BufferedImage[9]; // Sprite berkilau
    protected int frameIndex = 0;
    protected int updateCounter = 0;

    public Item(int x, int y, int width, int height, int goldValue, BufferedImage image, String shinePrefix, String jenis) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.goldValue = goldValue;
        this.image = image;
        loadShineFrames(shinePrefix); // gunakan prefix sesuai item
        this.jenis = jenis;
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getGoldValue() {
        return goldValue;
    }

    // Gambar item dan shine effect di atasnya
    public void draw(Graphics g, JPanel panel) {
        // Gambar item utama (tas, dll)
        g.drawImage(image, x, y, width, height, panel);

        // Gambar shine effect (mengkilap)
        if (shineFrames != null && shineFrames[frameIndex] != null) {
            g.drawImage(shineFrames[frameIndex], x, y, width, height, panel);
        }
    }

    // Update animasi frame shine, dipanggil dari Timer di Arena
    public void updateShine() {
        updateCounter++;
        if (updateCounter >= 5) { // ubah nilai ini untuk mempercepat/memperlambat animasi
            frameIndex = (frameIndex + 1) % shineFrames.length;
            updateCounter = 0;
        }
    }

    // Load sprite shine dari folder
    protected void loadShineFrames(String prefix) {
        for (int i = 0; i < shineFrames.length; i++) {
            try {
                shineFrames[i] = ImageIO.read(new File("RobberyBob/Assets/" + prefix + i + ".png"));
            } catch (IOException e) {
                System.out.println("Gagal load shine frame " + prefix + i + ": " + e.getMessage());
            }
        }
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

}
