import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Kalung extends Item {
    public Kalung(int x, int y) {
        super(x, y, 40, 40, 50, loadImage(), "cahaya"); // shine kuning
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("RobberyBob/Assets/Kalung.png"));
        } catch (IOException e) {
            System.out.println("Gagal load gambar Kalung: " + e.getMessage());
            return null;
        }
    }
}
