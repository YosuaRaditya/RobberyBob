import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Uang extends Item {
    public Uang(int x, int y) {
        super(x, y, 40, 40, 50, loadImage(), "cahaya", "Biasa"); // shine kuning
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("RobberyBob/Assets/Uang.png"));
        } catch (IOException e) {
            System.out.println("Gagal load gambar Uang: " + e.getMessage());
            return null;
        }
    }
}
