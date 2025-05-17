import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.Ellipse2D;


public class RobberyBob {
    private BufferedImage[] sprites = new BufferedImage[16];
    private int spriteIndex = 0;
    private int x, y;
    private int width = 200, height = 200;
    private String arah = "kanan";
    private boolean isMoving = false;
    private Set<Integer> keysPressed = new HashSet<>();
    private Timer animTimer;

    public RobberyBob(int startX, int startY) {
        this.x = startX;
        this.y = startY;

        try {
            for (int i = 0; i < 16; i++) {
                sprites[i] = ImageIO.read(new File("RobberyBob/Assets/jalan" + i + ".png"));
            }
        } catch (IOException e) {
            System.out.println("Error loading sprites: " + e.getMessage());
        }

        animTimer = new Timer(100, e -> {
            if (isMoving) {
                spriteIndex = (spriteIndex + 1) % sprites.length;
            }
        });
    }

    public void handleKeyPressed(int keyCode, BufferedImage collisionMap, int panelW, int panelH) {
        keysPressed.add(keyCode);
        isMoving = true;

        int dx = 0, dy = 0;

        if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_D) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 10; dy = -10; arah = "kanan_atas";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_A) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = -10; dy = -10; arah = "kiri_atas";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_D) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 10; dy = 10; arah = "kanan_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_A) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = -10; dy = 10; arah = "kiri_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 0; dy = -10; arah = "atas";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 0; dy = 10; arah = "bawah";
        } else if (keysPressed.contains(KeyEvent.VK_A) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = -10; dy = 0; arah = "kiri";
        } else if (keysPressed.contains(KeyEvent.VK_D) && keysPressed.contains(KeyEvent.VK_SHIFT)) {
            dx = 10; dy = 0; arah = "kanan";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_D)) {
            dx = 5; dy = -5; arah = "kanan_atas";
        } else if (keysPressed.contains(KeyEvent.VK_W) && keysPressed.contains(KeyEvent.VK_A)) {
            dx = -5; dy = -5; arah = "kiri_atas";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_D)) {
            dx = 5; dy = 5; arah = "kanan_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_A)) {
            dx = -5; dy = 5; arah = "kiri_bawah";
        } else if (keysPressed.contains(KeyEvent.VK_W)) {
            dx = 0; dy = -5; arah = "atas";
        } else if (keysPressed.contains(KeyEvent.VK_S)) {
            dx = 0; dy = 5; arah = "bawah";
        } else if (keysPressed.contains(KeyEvent.VK_A)) {
            dx = -5; dy = 0; arah = "kiri";
        } else if (keysPressed.contains(KeyEvent.VK_D)) {
            dx = 5; dy = 0; arah = "kanan";
        }

        if (isWalkable(x + dx + width / 2, y + dy + height / 2, collisionMap, panelW, panelH)) {
            x += dx;
            y += dy;
        }

        if (!animTimer.isRunning()) animTimer.start();
    }

    public void handleKeyReleased(int keyCode) {
        keysPressed.remove(keyCode);
        if (keysPressed.isEmpty()) {
            isMoving = false;
            spriteIndex = 0;
            animTimer.stop();
        }
    }

    public void draw(Graphics g, Component c) {
        if (sprites[spriteIndex] == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        BufferedImage scaledSprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gScaled = scaledSprite.createGraphics();
        gScaled.drawImage(sprites[spriteIndex], 0, 0, width, height, null);
        gScaled.dispose();

        double angle = switch (arah) {
            case "kanan" -> Math.PI / 2;
            case "kiri" -> -Math.PI / 2;
            case "bawah" -> Math.PI;
            case "kiri_atas" -> Math.toRadians(-45);
            case "kiri_bawah" -> Math.toRadians(-135);
            case "kanan_atas" -> Math.toRadians(45);
            case "kanan_bawah" -> Math.toRadians(135);
            default -> 0;
        };

        AffineTransform transform = new AffineTransform();
        transform.translate(x + width / 2.0, y + height / 2.0);
        transform.rotate(angle);
        transform.translate(-width / 2.0, -height / 2.0);

        g2d.drawImage(scaledSprite, transform, null);
        g2d.dispose();
    }

    private boolean isWalkable(int px, int py, BufferedImage map, int panelW, int panelH) {
        int imgW = map.getWidth();
        int imgH = map.getHeight();
        int scaledX = px * imgW / panelW;
        int scaledY = py * imgH / panelH;

        if (scaledX < 0 || scaledY < 0 || scaledX >= imgW || scaledY >= imgH) {
            return false;
        }

        Color color = new Color(map.getRGB(scaledX, scaledY));
        return color.getRed() > 200 && color.getGreen() > 200 && color.getBlue() > 200;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Ellipse2D getDetectionCircle() {
        int radius = 60;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        return new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void drawDetectionArea(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int radius = 60;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int diameter = radius * 2;

        g2d.setColor(new Color(255, 255, 0, 100)); // Kuning transparan
        g2d.fillOval(centerX - radius, centerY - radius, diameter, diameter);
        g2d.dispose();
    }

}
