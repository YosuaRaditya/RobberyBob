import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ArenaA extends JPanel {
    private BufferedImage[] sprites = new BufferedImage[16];
    private int spriteIndex = 0;
    private Timer timer;
    private BufferedImage mapImage;
    private JButton backButton;
    private int spriteX = 100, spriteY = 100;
    private String arah = "kanan";  // arah default

    // ✅ Ukuran tampilan sprite yang bisa diatur
    private int spriteWidth = 200;
    private int spriteHeight = 200;

    public ArenaA(JFrame parentFrame) {
        setLayout(null);

        try {
            for (int i = 0; i < 16; i++) {
                sprites[i] = ImageIO.read(new File("Assets/jalan" + i + ".png"));
            }
            mapImage = ImageIO.read(new File("Assets/mapArenaA.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading sprites or map: " + e.getMessage());
        }

        timer = new Timer(100, e -> {
            spriteIndex = (spriteIndex + 1) % 16;
            repaint();
        });
        timer.start();

        backButton = new JButton("Back");
        backButton.setBounds(30, 30, 100, 40);
        backButton.addActionListener(e -> {
            parentFrame.setContentPane(new HomePanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        add(backButton);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_W) {
                    spriteY -= 5;
                    arah = "atas";
                } else if (keyCode == KeyEvent.VK_S) {
                    spriteY += 5;
                    arah = "bawah";
                } else if (keyCode == KeyEvent.VK_A) {
                    spriteX -= 5;
                    arah = "kiri";
                } else if (keyCode == KeyEvent.VK_D) {
                    spriteX += 5;
                    arah = "kanan";
                }

                repaint();
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (mapImage != null) {
        g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
    }

    if (sprites[spriteIndex] != null) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Hitung rotasi
        double angle = switch (arah) {
            case "kiri" -> -Math.PI / 2;
            case "kanan" -> Math.PI / 2;
            case "bawah" -> Math.PI;
            default -> 0;
        };

        // Buat gambar sprite yang sudah diskalakan
        BufferedImage scaledSprite = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gScaled = scaledSprite.createGraphics();
        gScaled.drawImage(sprites[spriteIndex], 0, 0, spriteWidth, spriteHeight, null);
        gScaled.dispose();

        // Transformasi untuk rotasi di tengah sprite
        AffineTransform transform = new AffineTransform();
        transform.translate(spriteX + spriteWidth / 2.0, spriteY + spriteHeight / 2.0);
        transform.rotate(angle);
        transform.translate(-spriteWidth / 2.0, -spriteHeight / 2.0);

        g2d.drawImage(scaledSprite, transform, null);
        g2d.dispose();
    }
}

}
