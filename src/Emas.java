import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Emas extends Item {
    public Emas(int x, int y) {
        super(x, y, 40, 40, 50, loadImage(), "cahaya", "Biasa"); // shine kuning
    } 
    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("RobberyBob/Assets/Emas.png"));
        } catch (IOException e) {
            System.out.println("Gagal load gambar Emas: " + e.getMessage());
            return null;
        }
    }
}
