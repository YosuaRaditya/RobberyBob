import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Laptop extends Item {
    public Laptop(int x, int y) {
        super(x, y, 40, 40, 50, loadImage(), "cahayaKuning", "Extra"); // shine kuning
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("RobberyBob/Assets/Laptop.png"));
        } catch (IOException e) {
            System.out.println("Gagal load gambar laptop: " + e.getMessage());
            return null;
        }
    }
}