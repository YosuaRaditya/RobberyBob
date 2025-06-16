import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Polisi2 extends Penjaga {
    public Polisi2(int x, int y, int width, int height, String imagePath, int[][] patrolPoints) {
        super(x, y, width, height, loadImage(imagePath), patrolPoints);
    } 

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading Upin: " + e.getMessage());
            return null;
        }
    }
}