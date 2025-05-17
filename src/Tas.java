import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Tas extends Item {
    public Tas(int x, int y) {
        super(x, y, 40, 40, 50, loadImage());
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("RobberyBob/Assets/tas.png"));
        } catch (IOException e) {
            System.out.println("Gagal load gambar tas: " + e.getMessage());
            return null;
        }
    }
}
