import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Polisi extends Penjaga {
    public Polisi(int x, int y, int width, int height, String imagePath, int[][] patrolPoints) {
        super(x, y, width, height, loadImage(imagePath), patrolPoints);
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading polisi: " + e.getMessage());
            return null;
        }
    }
}