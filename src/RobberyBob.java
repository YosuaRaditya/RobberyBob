import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;


public class RobberyBob {
    private BufferedImage[] sprites = new BufferedImage[16];
    private BufferedImage[] smokeSprites = new BufferedImage[9];
    private int spriteIndex = 0;
    private int smokeIndex = 0;
    private int x, y;   
    private int width = 170, height = 170;
    private String arah = "kanan";
    private boolean isMoving = false;
    private boolean isHiding = false;
    private boolean inHidingArea = false;
    private boolean isPlayingSmoke = false;
    private Set<Integer> keysPressed = new HashSet<>();
    private Timer animTimer;
    private Timer smokeTimer;
    private boolean hasExtraItem = false; // Tambahkan ini
    private Runnable onLevelComplete; // Callback untuk level selesai
    private int visibilityRadius = 150;

    public RobberyBob(int startX, int startY) {
        this.x = startX;
        this.y = startY;

        try {
            // Load character sprites
            for (int i = 0; i < 16; i++) {
                sprites[i] = ImageIO.read(new File("RobberyBob/Assets/jalan" + i + ".png"));
            }
            
            // Load smoke sprites
            for (int i = 0; i < 9; i++) {
                smokeSprites[i] = ImageIO.read(new File("RobberyBob/Assets/Asset" + (i+1) + ".png"));
            }
        } catch (IOException e) {
            System.out.println("Error loading sprites: " + e.getMessage());
        }

        animTimer = new Timer(100, e -> {
            if (isMoving) {
                spriteIndex = (spriteIndex + 1) % sprites.length;
            }
        });
        
        // Create smoke timer but don't start it yet
        smokeTimer = new Timer(100, e -> {
            smokeIndex = (smokeIndex + 1) % smokeSprites.length;
            
            // If we've completed a full cycle of the smoke animation when hiding/unhiding
            if (smokeIndex == 0 && isPlayingSmoke) {
                isPlayingSmoke = false;
                ((Timer)e.getSource()).stop(); // Stop the timer when animation completes
            }
        });
    }

    public void handleKeyPressed(int keyCode, BufferedImage collisionMap, int panelW, int panelH) {
        keysPressed.add(keyCode);
        
        // Toggle hiding state when 'I' is pressed and in hiding area
        if (keyCode == KeyEvent.VK_I && inHidingArea) {
            isHiding = !isHiding;
            
            // Start smoke animation when changing hiding state
            smokeIndex = 0; // Reset to start of animation
            isPlayingSmoke = true; // Mark that we're playing the animation
            if (!smokeTimer.isRunning()) {
                smokeTimer.start();
            }
            return;
        }
        
        // Don't move if hiding
        if (isHiding) {
            return;
        }
        
        isMoving = true;

        int dx = 0, dy = 0;
        boolean w = keysPressed.contains(KeyEvent.VK_W);
        boolean a = keysPressed.contains(KeyEvent.VK_A);
        boolean s = keysPressed.contains(KeyEvent.VK_S);
        boolean d = keysPressed.contains(KeyEvent.VK_D);
        boolean shift = keysPressed.contains(KeyEvent.VK_SHIFT);

        switch ((w ? 8 : 0) | (a ? 4 : 0) | (s ? 2 : 0) | (d ? 1 : 0) | (shift ? 16 : 0)) {
            // Diagonal with shift (fast)
            case 8 | 1 | 16: // W + D + Shift
            dx = 10; dy = -10; arah = "kanan_atas"; break;
            case 8 | 4 | 16: // W + A + Shift
            dx = -10; dy = -10; arah = "kiri_atas"; break;
            case 2 | 1 | 16: // S + D + Shift
            dx = 10; dy = 10; arah = "kanan_bawah"; break;
            case 2 | 4 | 16: // S + A + Shift
            dx = -10; dy = 10; arah = "kiri_bawah"; break;
            // Cardinal with shift (fast)
            case 8 | 16: // W + Shift
            dx = 0; dy = -10; arah = "atas"; break;
            case 2 | 16: // S + Shift
            dx = 0; dy = 10; arah = "bawah"; break;
            case 4 | 16: // A + Shift
            dx = -10; dy = 0; arah = "kiri"; break;
            case 1 | 16: // D + Shift
            dx = 10; dy = 0; arah = "kanan"; break;
            // Diagonal (normal)
            case 8 | 1: // W + D
            dx = 5; dy = -5; arah = "kanan_atas"; break;
            case 8 | 4: // W + A
            dx = -5; dy = -5; arah = "kiri_atas"; break;
            case 2 | 1: // S + D
            dx = 5; dy = 5; arah = "kanan_bawah"; break;
            case 2 | 4: // S + A
            dx = -5; dy = 5; arah = "kiri_bawah"; break;
            // Cardinal (normal)
            case 8: // W
            dx = 0; dy = -5; arah = "atas"; break;
            case 2: // S
            dx = 0; dy = 5; arah = "bawah"; break;
            case 4: // A
            dx = -5; dy = 0; arah = "kiri"; break;
            case 1: // D
            dx = 5; dy = 0; arah = "kanan"; break;
            default:
            break;
        }

        System.out.println("walking");

        if (isWalkable(x + dx + width / 2, y + dy + height / 2, collisionMap, panelW, panelH)) {
            x += dx;
            y += dy;
        }
        
        // Check if player is in hiding area after movement
        inHidingArea = isInHidingArea(x + width / 2, y + height / 2, collisionMap, panelW, panelH);

        if (!animTimer.isRunning()) animTimer.start();
    }

    public void setOnLevelComplete(Runnable callback) {
        this.onLevelComplete = callback;
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
        if (isHiding) {
            if (isPlayingSmoke && smokeSprites[smokeIndex] != null) {
                // Draw smoke animation when transitioning
                g.drawImage(smokeSprites[smokeIndex], x, y, width, height, c);
            } else {
                // When fully hidden and animation is complete, don't draw anything
                // This makes the player invisible when hiding
            }
        } else {
            if (isPlayingSmoke && smokeSprites[smokeIndex] != null) {
                // Draw smoke animation when unhiding
                g.drawImage(smokeSprites[smokeIndex], x, y, width, height, c);
            } else if (sprites[spriteIndex] != null) {
                // Draw normal character when not hiding
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
        }
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
        
        // Kriteria untuk merah muda (233, 73, 75):
        if (color.getRed() > 220 && color.getGreen() < 80 && color.getBlue() < 80) {
            if (hasExtraItem()) {
                System.out.println("Mantap"); // Muncul hanya jika bawa item Extra
            }
            return false; // Blokir pergerakan
        }

        // Allow walking on white areas (RGB all > 200)
        boolean isWhiteArea = color.getRed() > 200 && color.getGreen() > 200 && color.getBlue() > 200;
        
        // Allow walking on green areas (high green, low red and blue)
        boolean isGreenArea = color.getGreen() > 200 && color.getRed() < 100 && color.getBlue() < 100;
        
        // Return true if either white area or green area
        return isWhiteArea || isGreenArea;
    }

    // Check if player is in green hiding area
    private boolean isInHidingArea(int px, int py, BufferedImage map, int panelW, int panelH) {
        int imgW = map.getWidth();
        int imgH = map.getHeight();
        int scaledX = px * imgW / panelW;
        int scaledY = py * imgH / panelH;

        if (scaledX < 0 || scaledY < 0 || scaledX >= imgW || scaledY >= imgH) {
            return false;
        }

        Color color = new Color(map.getRGB(scaledX, scaledY));
        
        // Detect green area (high green, low red and blue)
        return color.getGreen() > 200 && color.getRed() < 100 && color.getBlue() < 100;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Ellipse2D getDetectionCircle() {
        int radius = 40;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        return new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void drawDetectionArea(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int radius = 70;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int diameter = radius * 2;

        g2d.setColor(new Color(255, 255, 0, 100)); // Kuning transparan
        g2d.fillOval(centerX - radius, centerY - radius, diameter, diameter);
        g2d.dispose();
    }

    public void drawVisibilityOverlay(Graphics g, int panelWidth, int panelHeight) {
        // Don't draw visibility overlay when hiding
        if (isHiding) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing for smoother circle
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create the overlay with a circular hole
        Area overlay = new Area(new Rectangle(0, 0, panelWidth, panelHeight));

        // Player center coordinates
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;

        // Create circle area to subtract from overlay
        Area circle = new Area(new Ellipse2D.Double(
            playerCenterX - visibilityRadius, 
            playerCenterY - visibilityRadius, 
            visibilityRadius * 2, 
            visibilityRadius * 2
        ));

        // Subtract the circle from the overlay to create transparency
        overlay.subtract(circle);

        // Set semi-transparent black color and draw the overlay
        g2d.setColor(new Color(0, 0, 0, 120)); // Semi-transparent black
        g2d.fill(overlay);

        g2d.dispose();
    }

    public void setHasExtraItem(boolean status) {
        this.hasExtraItem = status;
    }

    public boolean hasExtraItem() {
        return hasExtraItem;
    }
    
    public boolean isHiding() {
        return isHiding;
    }
    
    public void setHiding(boolean hiding) {
        if (this.isHiding != hiding) {
            this.isHiding = hiding;
            
            // Start smoke animation when toggling hiding state
            smokeIndex = 0;
            isPlayingSmoke = true;
            if (!smokeTimer.isRunning()) {
                smokeTimer.start();
            }
        }
    }
    
    public boolean isInHidingArea() {
        return inHidingArea;
    }
}
