import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Polisi extends Penjaga {
    private int[][] patrolPoints;
    private int patrolIndex = 0;
    private int speed = 2;
    private String arah = "kanan"; // default arah hadap
    private boolean chasing = false;
    private Queue<Point> bobTrail = new LinkedList<>();
    private RobberyBob targetBob; // referensi ke Bob
    private boolean isTeleporting = false;
    private int teleportFrame = 0;
    private Timer teleportTimer;
    private Timer teleportDelayTimer;
    private BufferedImage[] teleportSprites = new BufferedImage[9];
    private boolean isMovingToTarget = false;
    private int targetX, targetY;
    
    // Add collision detection variables
    private BufferedImage collisionMap;
    private int mapWidth, mapHeight;

    public Polisi(int x, int y, int width, int height, String imagePath, int[][] patrolPoints) {
        super(x, y, width, height, loadImage(imagePath));
        this.patrolPoints = patrolPoints;
        loadTeleportSprites();
    }

    private void loadTeleportSprites() {
        for (int i = 0; i < 9; i++) {
            try {
                teleportSprites[i] = ImageIO.read(new File("RobberyBob/Assets/Asset" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.out.println("Gagal load teleport sprite: " + e.getMessage());
            }
        }
    }

    public void moveTo(int x, int y) {
        this.targetX = x;
        this.targetY = y;
        this.isMovingToTarget = true;
    }

    public void setTargetBob(RobberyBob bob) {
        this.targetBob = bob;
    }
    
    // Set collision map for the police
    public void setCollisionMap(BufferedImage collisionMap, int mapWidth, int mapHeight) {
        this.collisionMap = collisionMap;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading polisi: " + e.getMessage());
            return null;
        }
    }
    
    // Check if a position is walkable
    private boolean isWalkable(int px, int py) {
        if (collisionMap == null) {
            return true; // If no collision map, allow movement
        }
        
        int imgW = collisionMap.getWidth();
        int imgH = collisionMap.getHeight();
        int scaledX = px * imgW / mapWidth;
        int scaledY = py * imgH / mapHeight;

        // Check if within map bounds
        if (scaledX < 0 || scaledY < 0 || scaledX >= imgW || scaledY >= imgH) {
            return false;
        }

        Color color = new Color(collisionMap.getRGB(scaledX, scaledY));
        
        // Allow walking on white areas (RGB all > 200)
        boolean isWhiteArea = color.getRed() > 200 && color.getGreen() > 200 && color.getBlue() > 200;
        
        // Allow walking on green areas (high green, low red and blue)
        boolean isGreenArea = color.getGreen() > 200 && color.getRed() < 100 && color.getBlue() < 100;
        
        return isWhiteArea || isGreenArea;
    }

@Override
public void update() {
    if (isTeleporting) return; // Jangan update saat teleport animasi

    // ==== CEK DULU: Apakah Bob terdeteksi? ====
    boolean bobDetected = false;
    if (targetBob != null) {
        int bobCenterX = targetBob.x + targetBob.width / 2;
        int bobCenterY = targetBob.y + targetBob.height / 2;

        int rectWidth = 300;
        int rectHeight = 250;
        int offset = 70;
        int rectX = x + width / 2 - offset;
        int rectY = y + (height / 2) - (rectHeight / 2);

        switch (arah) {
            case "kanan":
                rectX = x + width / 2 - offset;
                break;
            case "kiri":
                rectX = x + width / 2 - (rectWidth - offset);
                break;
            case "bawah":
                rectX = x + width / 2 - rectWidth / 2;
                rectY = y + height / 2 - offset;
                break;
            case "atas":
                rectX = x + width / 2 - rectWidth / 2;
                rectY = y + height / 2 - (rectHeight - offset);
                break;
        }
        // Cek apakah Bob ada di area deteksi dan tidak sembunyi
        if (bobCenterX >= rectX && bobCenterX <= rectX + rectWidth &&
            bobCenterY >= rectY && bobCenterY <= rectY + rectHeight &&
            !targetBob.isHiding()) {
            bobDetected = true;
        }
    }

    // ==== PRIORITAS: Jika Bob terdeteksi, langsung kejar Bob ====
    if (bobDetected) {
        chasing = true;
        isMovingToTarget = false; // Hentikan moveTo jika sedang
        if (teleportDelayTimer != null) {
            teleportDelayTimer.stop();
            teleportDelayTimer = null;
        }
        if (teleportTimer != null) {
            teleportTimer.stop();
            teleportTimer = null;
        }
        // Tambahkan jejak Bob
        int bobCenterX = targetBob.x + targetBob.width / 2;
        int bobCenterY = targetBob.y + targetBob.height / 2;
        if (bobTrail.isEmpty() || !bobTrail.peek().equals(new Point(bobCenterX, bobCenterY))) {
            bobTrail.add(new Point(bobCenterX, bobCenterY));
            if (bobTrail.size() > 30) bobTrail.poll();
        }
    } else if (chasing) {
        // Jika sebelumnya chasing, tapi sekarang Bob tidak terdeteksi, stop chasing
        chasing = false;
        bobTrail.clear();
        // Mulai delay teleport jika perlu
        if (teleportDelayTimer == null && !isTeleporting) {
            teleportDelayTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    teleportDelayTimer.stop();
                    teleportDelayTimer = null;
                    startTeleport();
                }
            });
            teleportDelayTimer.setRepeats(false);
            teleportDelayTimer.start();
        }
    }

    // === Jika sedang mengejar Bob ===
    if (chasing && !bobTrail.isEmpty()) {
        Point next = bobTrail.peek();
        int targetX = next.x - width / 2;
        int targetY = next.y - height / 2;
        int dx = targetX - x;
        int dy = targetY - y;

        // Try to move in X direction
        if (Math.abs(dx) > 0) {
            int nextX = x + ((dx > 0) ? Math.min(speed, Math.abs(dx)) : -Math.min(speed, Math.abs(dx)));
            // Check if next position is walkable
            if (isWalkable(nextX + width/2, y + height/2)) {
                x = nextX;
                arah = dx > 0 ? "kanan" : "kiri";
            }
        }
        
        // Try to move in Y direction
        if (Math.abs(dy) > 0) {
            int nextY = y + ((dy > 0) ? Math.min(speed, Math.abs(dy)) : -Math.min(speed, Math.abs(dy)));
            // Check if next position is walkable
            if (isWalkable(x + width/2, nextY + height/2)) {
                y = nextY;
                arah = dy > 0 ? "bawah" : "atas";
            }
        }

        // Check if reached target within speed tolerance
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            // Only move to exact position if it's walkable
            if (isWalkable(targetX + width/2, targetY + height/2)) {
                x = targetX;
                y = targetY;
            }
            bobTrail.poll();
        }

        // Cek tangkap Bob
        if (targetBob != null) {
            Rectangle polisiRect = new Rectangle(x, y, width, height);
            Rectangle bobRect = new Rectangle(targetBob.x, targetBob.y, targetBob.width, targetBob.height);
            if (polisiRect.intersects(bobRect) && !targetBob.isHiding()) {
                System.out.println("mantabbb");
                // (opsional) chasing = false;
            }
        }
        return; // PENTING: Jangan lakukan aksi lain jika sedang mengejar Bob!
    }

    // === Jika tidak mengejar Bob, baru lakukan moveTo/Patrol seperti biasa ===
    if (isMovingToTarget) {
        int dx = targetX - x;
        int dy = targetY - y;
        
        // Try to move in X direction with collision check
        if (Math.abs(dx) > 0) {
            int nextX = x + ((dx > 0) ? Math.min(speed, Math.abs(dx)) : -Math.min(speed, Math.abs(dx)));
            if (isWalkable(nextX + width/2, y + height/2)) {
                x = nextX;
                arah = dx > 0 ? "kanan" : "kiri";
            }
        }
        
        // Try to move in Y direction with collision check
        if (Math.abs(dy) > 0) {
            int nextY = y + ((dy > 0) ? Math.min(speed, Math.abs(dy)) : -Math.min(speed, Math.abs(dy)));
            if (isWalkable(x + width/2, nextY + height/2)) {
                y = nextY;
                arah = dy > 0 ? "bawah" : "atas";
            }
        }
        
        // Check if we've reached the target
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            // Only set exact position if walkable
            if (isWalkable(targetX + width/2, targetY + height/2)) {
                x = targetX;
                y = targetY;
            }
            isMovingToTarget = false;
            if (teleportDelayTimer == null && !isTeleporting) {
                teleportDelayTimer = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        teleportDelayTimer.stop();
                        teleportDelayTimer = null;
                        startTeleport();
                    }
                });
                teleportDelayTimer.setRepeats(false);
                teleportDelayTimer.start();
            }
        }
        return;
    }

    // Patrol normal jika tidak teleport dan tidak delay
    if (!isTeleporting && (teleportDelayTimer == null)) {
        if (patrolPoints == null || patrolPoints.length == 0) return;
        int targetX = patrolPoints[patrolIndex][0];
        int targetY = patrolPoints[patrolIndex][1];
        int dx = targetX - x;
        int dy = targetY - y;

        // Try to move in X direction with collision check
        if (Math.abs(dx) > 0) {
            int nextX = x + ((dx > 0) ? Math.min(speed, Math.abs(dx)) : -Math.min(speed, Math.abs(dx)));
            if (isWalkable(nextX + width/2, y + height/2)) {
                x = nextX;
                arah = dx > 0 ? "kanan" : "kiri";
            }
        }
        
        // Try to move in Y direction with collision check
        if (Math.abs(dy) > 0) {
            int nextY = y + ((dy > 0) ? Math.min(speed, Math.abs(dy)) : -Math.min(speed, Math.abs(dy)));
            if (isWalkable(x + width/2, nextY + height/2)) {
                y = nextY;
                arah = dy > 0 ? "bawah" : "atas";
            }
        }

        // Check if reached target
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            // Only set exact position if walkable
            if (isWalkable(targetX + width/2, targetY + height/2)) {
                x = targetX;
                y = targetY;
            }
            patrolIndex = (patrolIndex + 1) % patrolPoints.length;
        }
    }
}
// ...existing code...

    private void startTeleport() {
        isTeleporting = true;
        teleportFrame = 0;
        teleportTimer = new Timer(80, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                teleportFrame++;
                if (teleportFrame >= teleportSprites.length) {
                    teleportTimer.stop();
                    teleportTimer = null;
                    // Teleport ke patrol point berikutnya
                    int targetX = patrolPoints[patrolIndex][0];
                    int targetY = patrolPoints[patrolIndex][1];
                    x = targetX;
                    y = targetY;
                    patrolIndex = (patrolIndex + 1) % patrolPoints.length;
                    isTeleporting = false;
                }
            }
        });
        teleportTimer.start();
    }

    @Override
    public void draw(Graphics g) {
        // Gambar area deteksi (persegi panjang merah transparan)
        Graphics2D g2dRadius = (Graphics2D) g.create();
        g2dRadius.setColor(new Color(255, 0, 0, 60));
        int rectWidth = 300;
        int rectHeight = 250;
        int offset = 70; // geser polisi ke kanan 40px dari sisi kiri rectangle
        int rectX = x + width / 2 - offset;
        int rectY = y + (height / 2) - (rectHeight / 2);

        // Rotasi sesuai arah hadap polisi
        double angle = 0;
        switch (arah) {
    case "kanan":
        rectX = x + width / 2 - offset;
        break;
    case "kiri":
        rectX = x + width / 2 - (rectWidth - offset);
        break;
    case "bawah":
        rectX = x + width / 2 - rectWidth / 2;
        rectY = y + height / 2 - offset;
        break;
    case "atas":
        rectX = x + width / 2 - rectWidth / 2;
        rectY = y + height / 2 - (rectHeight - offset);
        break;
}
        g2dRadius.translate(rectX, rectY + rectHeight / 2);
        g2dRadius.rotate(angle);
        g2dRadius.translate(-rectX, -(rectY + rectHeight / 2));
        g2dRadius.fillRect(rectX, rectY, rectWidth, rectHeight);
        g2dRadius.dispose();

        // Gambar polisi seperti biasa
        if (isTeleporting && teleportFrame < teleportSprites.length && teleportSprites[teleportFrame] != null) {
            g.drawImage(teleportSprites[teleportFrame], x, y, width, height, null);
        } else if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            double imgAngle = 0;
            switch (arah) {
                case "kanan": imgAngle = Math.PI / 2; break;
                case "kiri": imgAngle = -Math.PI / 2; break;
                case "bawah": imgAngle = Math.PI; break;
                case "atas": imgAngle = 0; break;
            }
            g2d.translate(x + width / 2, y + height / 2);
            g2d.rotate(imgAngle);
            g2d.drawImage(image, -width / 2, -height / 2, width, height, null);
            g2d.dispose();
        }
    }

    // Jika ingin mengatur arah hadap secara manual dari luar
    public void setArah(String arah) {
        this.arah = arah;
    }

    public String getArah() {
        return arah;
    }
}