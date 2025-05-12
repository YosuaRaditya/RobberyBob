import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ArenaB extends JPanel {
    private BufferedImage[] sprites = new BufferedImage[16];
    private int spriteIndex = 0;
    private Timer timer;
    private BufferedImage mapImage;
    private JButton backButton;
    private int spriteX = 260, spriteY = 40;
    private String arah = "kanan";  // arah default 

    private int spriteWidth = 200;
    private int spriteHeight = 200;
    private boolean isMoving = false;

    public ArenaB(JFrame parentFrame) {
        setLayout(null);

        try {
            for (int i = 0; i < 16; i++) {
                sprites[i] = ImageIO.read(new File("RobberyBob/Assets/jalan" + i + ".png"));
            }
            mapImage = ImageIO.read(new File("RobberyBob/Assets/mapArenaB.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading sprites or map: " + e.getMessage());
        }

        // Timer untuk animasi gerakan
        timer = new Timer(100, e -> {
            if (isMoving) {
                spriteIndex = (spriteIndex + 1) % 16;
                repaint();
            }
        });

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
                boolean wasMoving = isMoving;
                isMoving = true;

                int keyCode = e.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_W -> {
                        spriteY -= 5;
                        arah = "atas";
                    }
                    case KeyEvent.VK_S -> {
                        spriteY += 5;
                        arah = "bawah";
                    }
                    case KeyEvent.VK_A -> {
                        spriteX -= 5;
                        arah = "kiri";
                    }
                    case KeyEvent.VK_D -> {
                        spriteX += 5;
                        arah = "kanan";
                    }
                }

                if (!wasMoving && !timer.isRunning()) {
                    timer.start(); // mulai animasi saat pertama kali bergerak
                }

                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                isMoving = false;
                timer.stop();
                spriteIndex = 0; // kembali ke frame diam
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

            double angle = switch (arah) {
                case "kiri" -> -Math.PI / 2;
                case "kanan" -> Math.PI / 2;
                case "bawah" -> Math.PI;
                default -> 0; // atas
            };

            BufferedImage scaledSprite = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gScaled = scaledSprite.createGraphics();
            gScaled.drawImage(sprites[spriteIndex], 0, 0, spriteWidth, spriteHeight, null);
            gScaled.dispose();

            AffineTransform transform = new AffineTransform();
            transform.translate(spriteX + spriteWidth / 2.0, spriteY + spriteHeight / 2.0);
            transform.rotate(angle);
            transform.translate(-spriteWidth / 2.0, -spriteHeight / 2.0);

            g2d.drawImage(scaledSprite, transform, null);
            g2d.dispose();
        }
    }
}
