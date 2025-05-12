import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class ArenaA extends JPanel {
    private BufferedImage[] sprites = new BufferedImage[16];
    private int spriteIndex = 0;
    private Timer timer;
    private BufferedImage mapImage;
    private JButton backButton;
    private int spriteX = 120, spriteY = 370;
    private String arah = "kanan";  // arah default
    private int spriteWidth = 200;
    private int spriteHeight = 200;
    private boolean isMoving = false;
    private Set<Integer> keysPressed = new HashSet<>();

    public ArenaA(JFrame parentFrame) {
        setLayout(null);

        try {
            for (int i = 0; i < 16; i++) {
                sprites[i] = ImageIO.read(new File("RobberyBob/Assets/jalan" + i + ".png"));
            }
            mapImage = ImageIO.read(new File("RobberyBob/Assets/mapArenaA.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading sprites or map: " + e.getMessage());
        }

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
                keysPressed.add(e.getKeyCode());
                isMoving = true;

                // Deteksi kombinasi arah
                if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_D)) {
                    spriteY -= 5;
                    spriteX += 5;
                    arah = "kanan_atas";
                } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_A)) {
                    spriteY -= 5;
                    spriteX -= 5;
                    arah = "kiri_atas";
                } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_D)) {
                    spriteY += 5;
                    spriteX += 5;
                    arah = "kanan_bawah";
                } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_A)) {
                    spriteY += 5;
                    spriteX -= 5;
                    arah = "kiri_bawah";
                } else if (keysPressed.contains(KeyEvent.VK_W)) {
                    spriteY -= 5;
                    arah = "atas";
                } else if (keysPressed.contains(KeyEvent.VK_S)) {
                    spriteY += 5;
                    arah = "bawah";
                } else if (keysPressed.contains(KeyEvent.VK_A)) {
                    spriteX -= 5;
                    arah = "kiri";
                } else if (keysPressed.contains(KeyEvent.VK_D)) {
                    spriteX += 5;
                    arah = "kanan";
                }

                if (!timer.isRunning()) timer.start();
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
                if (keysPressed.isEmpty()) {
                    isMoving = false;
                    timer.stop();
                    spriteIndex = 0;
                    repaint();
                }
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
                case "kanan" -> Math.PI / 2;
                case "kiri" -> -Math.PI / 2;
                case "bawah" -> Math.PI;
                case "kiri_atas" -> Math.toRadians(-45);
                case "kiri_bawah" -> Math.toRadians(-135);
                case "kanan_atas" -> Math.toRadians(45);
                case "kanan_bawah" -> Math.toRadians(135);
                default -> 0; // atas
            };

            // Skala dan rotasi
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
