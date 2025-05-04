import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HomePanel extends JPanel {

    private BufferedImage background;
    private BufferedImage arenaA, arenaB, arenaC, arenaD;
    private Rectangle arenaABox, arenaBBox, arenaCBox, arenaDBox;
    private int gold = 100;

    private JFrame parentFrame;

    public HomePanel(JFrame frame) {
        this.parentFrame = frame;

        try {
            background = ImageIO.read(new File("Assets/bgHome.jpg"));
            arenaA = ImageIO.read(new File("Assets/arenaA.png"));
            arenaB = ImageIO.read(new File("Assets/arenaB.png"));
            arenaC = ImageIO.read(new File("Assets/arenaC.png"));
            arenaD = ImageIO.read(new File("Assets/arenaD.png"));
        } catch (IOException e) {
            System.out.println("Gagal memuat gambar: " + e.getMessage());
            e.printStackTrace();
        }

        // Atur posisi keempat arena
        arenaABox = new Rectangle(500, 100, 330, 300);
        arenaBBox = new Rectangle(900, 107, 327, 297);
        arenaCBox = new Rectangle(500, 390, 328, 298);
        arenaDBox = new Rectangle(910, 380, 317, 287);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (arenaABox.contains(e.getPoint())) {
                    System.out.println("Arena A dipilih!");
                    parentFrame.setContentPane(new ArenaA(parentFrame));
                    parentFrame.revalidate();
                } else if (arenaBBox.contains(e.getPoint())) {
                    System.out.println("Arena B dipilih!");
                    parentFrame.setContentPane(new ArenaB(parentFrame));
                    parentFrame.revalidate();
                } else if (arenaCBox.contains(e.getPoint())) {
                    System.out.println("Arena C dipilih!");
                    parentFrame.setContentPane(new ArenaC(parentFrame));
                    parentFrame.revalidate();
                } else if (arenaDBox.contains(e.getPoint())) {
                    System.out.println("Arena D dipilih!");
                    parentFrame.setContentPane(new ArenaD(parentFrame));
                    parentFrame.revalidate();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Gambar background
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        // Gambar jumlah gold
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(" " + gold, 1060, 52);

        // Gambar arena
        if (arenaA != null) g.drawImage(arenaA, arenaABox.x, arenaABox.y, arenaABox.width, arenaABox.height, null);
        if (arenaB != null) g.drawImage(arenaB, arenaBBox.x, arenaBBox.y, arenaBBox.width, arenaBBox.height, null);
        if (arenaC != null) g.drawImage(arenaC, arenaCBox.x, arenaCBox.y, arenaCBox.width, arenaCBox.height, null);
        if (arenaD != null) g.drawImage(arenaD, arenaDBox.x, arenaDBox.y, arenaDBox.width, arenaDBox.height, null);
    }
}
