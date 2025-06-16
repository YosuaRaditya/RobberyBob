import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Kamera extends Item {
    public Kamera(int x, int y) {
        super(x, y, 40, 40, 50, loadImage(), "cahayaKuning", "Extra"); // shine kuning
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("RobberyBob/Assets/Kamera.png"));
        } catch (IOException e) {
            System.out.println("Gagal load gambar kamera: " + e.getMessage());
            return null;
        }
    }
}