import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CCTV {
    private int x, y, width, height;
    private BufferedImage cctv;

    public CCTV(int x, int y, int width, int height, String cctvPath){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        try {
            cctv = ImageIO.read(new File(cctvPath));
        } catch (IOException e) {
            System.out.println("Error loading cctv: " + e.getMessage());
        }
    }

    public void draw(Graphics g){
        if(cctv != null){
            g.drawImage(cctv, x, y, width, height, null);
        }
    }
}