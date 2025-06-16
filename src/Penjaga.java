import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Penjaga {
    protected int x, y, width, height;
    protected BufferedImage image;
    protected int[][] patrolPoints;
    protected int patrolIndex = 0;
    protected int speed = 3;
    protected String arah = "kanan";
    protected boolean chasing = false;
    protected Queue<Point> bobTrail = new LinkedList<>();
    protected RobberyBob targetBob;
    protected boolean isTeleporting = false;
    protected int teleportFrame = 0;
    protected Timer teleportTimer;
    protected Timer teleportDelayTimer;
    protected BufferedImage[] teleportSprites = new BufferedImage[9];
    protected boolean isMovingToTarget = false;
    protected int targetX, targetY;
    protected boolean isCCTVTriggered = false;
    protected BufferedImage collisionMap;
    protected int panelW, panelH;
    protected Runnable onBobCaught;
    private int smokeIndex = 0;
    private boolean isPlayingSmoke = false;
    private Timer smokeTimer;
    private boolean isUnconscious = false;
    private Timer unconsciousTimer;
    private int unconsciousDuration = 5000;

    public void setCollisionMap(BufferedImage map, int panelW, int panelH) {
        this.collisionMap = map;
        this.panelW = panelW;
        this.panelH = panelH;
    }

    protected boolean isWalkable(int px, int py) {
        if (collisionMap == null || panelW == 0 || panelH == 0) return false;
        int imgW = collisionMap.getWidth();
        int imgH = collisionMap.getHeight();
        int scaledX = px * imgW / panelW;
        int scaledY = py * imgH / panelH;
        if (scaledX < 0 || scaledY < 0 || scaledX >= imgW || scaledY >= imgH) return false;
        Color color = new Color(collisionMap.getRGB(scaledX, scaledY));
        return !(color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50);
    }

    public Penjaga(int x, int y, int width, int height, BufferedImage image, int[][] patrolPoints) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.patrolPoints = patrolPoints;
        loadTeleportSprites();
    }

    protected void loadTeleportSprites() {
        for (int i = 0; i < 9; i++) {
            try {
                teleportSprites[i] = ImageIO.read(new File("RobberyBob/Assets/Asset" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.out.println("Gagal load teleport sprite: " + e.getMessage());
            }
        }
    }

    public void setOnBobCaught(Runnable onBobCaught) {
        this.onBobCaught = onBobCaught;
    }

    public void moveTo(int x, int y) {
        this.targetX = x;
        this.targetY = y;
        this.isMovingToTarget = true;
        this.isCCTVTriggered = true;
    }

    public void setTargetBob(RobberyBob bob) {
        this.targetBob = bob;
    }

    public void update() {
    if (isTeleporting) return;
    if (isUnconscious){
        return;
    }
    if (isTeleporting){
        return;
    }

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
            case "kanan": rectX = x + width / 2 - offset; break;
            case "kiri": rectX = x + width / 2 - (rectWidth - offset); break;
            case "bawah":
                rectX = x + width / 2 - rectWidth / 2;
                rectY = y + height / 2 - offset;
                break;
            case "atas":
                rectX = x + width / 2 - rectWidth / 2;
                rectY = y + height / 2 - (rectHeight - offset);
                break;
        }
        if (bobCenterX >= rectX && bobCenterX <= rectX + rectWidth &&
            bobCenterY >= rectY && bobCenterY <= rectY + rectHeight &&
            !targetBob.isHiding()) {
            bobDetected = true;
        }
    }

    if (bobDetected) {
        chasing = true;
        isMovingToTarget = false;
        isCCTVTriggered = false;
        if (teleportDelayTimer != null) {
            teleportDelayTimer.stop();
            teleportDelayTimer = null;
        }
        if (teleportTimer != null) {
            teleportTimer.stop();
            teleportTimer = null;
        }
        int bobCenterX = targetBob.x + targetBob.width / 2;
        int bobCenterY = targetBob.y + targetBob.height / 2;
        if (bobTrail.isEmpty() || !bobTrail.peek().equals(new Point(bobCenterX, bobCenterY))) {
            bobTrail.add(new Point(bobCenterX, bobCenterY));
            if (bobTrail.size() > 30) bobTrail.poll();
        }
    } else if (chasing) {
        chasing = false;
        bobTrail.clear();
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

    // === CHASING MODE ===
    if (chasing && !bobTrail.isEmpty()) {
        Point next = bobTrail.peek();
        int targetX = next.x - width / 2;
        int targetY = next.y - height / 2;
        int dx = targetX - x;
        int dy = targetY - y;

        int nextX = x, nextY = y;
        if (Math.abs(dx) > 0) {
            int step = Math.min(speed, Math.abs(dx));
            nextX += (dx > 0) ? step : -step;
            arah = dx > 0 ? "kanan" : "kiri";
        } else if (Math.abs(dy) > 0) {
            int step = Math.min(speed, Math.abs(dy));
            nextY += (dy > 0) ? step : -step;
            arah = dy > 0 ? "bawah" : "atas";
        }

        // Cek tembok
        if (isWalkable(nextX + width / 2, nextY + height / 2)) {
            x = nextX;
            y = nextY;
        } else if (isWalkable(nextX + width / 2, y + height / 2)) {
            x = nextX;
        } else if (isWalkable(x + width / 2, nextY + height / 2)) {
            y = nextY;
        }
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            x = targetX;
            y = targetY;
            bobTrail.poll();
        }

        if (targetBob != null) {
            Rectangle polisiRect = new Rectangle(x, y, width, height);
            Rectangle bobRect = new Rectangle(targetBob.x, targetBob.y, targetBob.width, targetBob.height);
            // Cek overlap rectangle
            if (polisiRect.intersects(bobRect) && !targetBob.isHiding()) {
                // Cek jarak pusat
                int polisiCenterX = x + width / 2;
                int polisiCenterY = y + height / 2;
                int bobCenterX = targetBob.x + targetBob.width / 2;
                int bobCenterY = targetBob.y + targetBob.height / 2;
                double distance = Math.hypot(polisiCenterX - bobCenterX, polisiCenterY - bobCenterY);
                if (distance < 40) { // Atur threshold sesuai kebutuhan
                    if (onBobCaught != null) onBobCaught.run();
                }
            }
        }
        return;
    }

    // === MOVE TO TARGET (CCTV) ===
    if (isMovingToTarget) {
        int dx = targetX - x;
        int dy = targetY - y;

        int nextX = x, nextY = y;
        if (Math.abs(dx) > 0) {
            int step = Math.min(speed, Math.abs(dx));
            nextX += (dx > 0) ? step : -step;
            arah = dx > 0 ? "kanan" : "kiri";
        } else if (Math.abs(dy) > 0) {
            int step = Math.min(speed, Math.abs(dy));
            nextY += (dy > 0) ? step : -step;
            arah = dy > 0 ? "bawah" : "atas";
        }

        // Cek tembok
        if (isWalkable(nextX + width / 2, nextY + height / 2)) {
            x = nextX;
            y = nextY;
        } else if (isWalkable(nextX + width / 2, y + height / 2)) {
            x = nextX;
        } else if (isWalkable(x + width / 2, nextY + height / 2)) {
            y = nextY;
        }

        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            x = targetX;
            y = targetY;
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

    // === PATROL MODE ===
    if (!isTeleporting && (teleportDelayTimer == null)) {
        if (patrolPoints == null || patrolPoints.length == 0) return;
        int targetX = patrolPoints[patrolIndex][0];
        int targetY = patrolPoints[patrolIndex][1];
        int dx = targetX - x;
        int dy = targetY - y;

        int nextX = x, nextY = y;
        if (Math.abs(dx) > 0) {
            int step = Math.min(speed, Math.abs(dx));
            nextX += (dx > 0) ? step : -step;
            arah = dx > 0 ? "kanan" : "kiri";
        } else if (Math.abs(dy) > 0) {
            int step = Math.min(speed, Math.abs(dy));
            nextY += (dy > 0) ? step : -step;
            arah = dy > 0 ? "bawah" : "atas";
        }

        // Cek tembok
        if (isWalkable(nextX + width / 2, nextY + height / 2)) {
            x = nextX;
            y = nextY;
        } else if (isWalkable(nextX + width / 2, y + height / 2)) {
            x = nextX;
        } else if (isWalkable(x + width / 2, nextY + height / 2)) {
            y = nextY;
        }

        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            x = targetX;
            y = targetY;
            patrolIndex = (patrolIndex + 1) % patrolPoints.length;
        }
    }
}

    protected void startTeleport() {
        isTeleporting = true;
        teleportFrame = 0;
        teleportTimer = new Timer(80, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                teleportFrame++;
                if (teleportFrame >= teleportSprites.length) {
                    teleportTimer.stop();
                    teleportTimer = null;
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

    public void makeUnconscious() {
        if (isUnconscious) return;
        isUnconscious = true;
        isPlayingSmoke = true;
        smokeIndex = 0;
        if (smokeTimer == null) {
            smokeTimer = new Timer(100, e -> {
                smokeIndex++;
                if (smokeIndex >= 8) {
                    smokeIndex = 0;
                    isPlayingSmoke = false;
                    ((Timer)e.getSource()).stop();
                }
            });
        }
        if (smokeTimer.isRunning()) {
            smokeTimer.restart();
        } else {
            smokeTimer.start();
        }
        if (unconsciousTimer != null) {
            unconsciousTimer.stop();
        }
        unconsciousTimer = new Timer(unconsciousDuration, e -> {
            isUnconscious = false;
            ((Timer)e.getSource()).stop();
        });
        unconsciousTimer.setRepeats(false);
        unconsciousTimer.start();
    }

    public void draw(Graphics g) {
        // Gambar area deteksi (persegi panjang merah transparan)
        Graphics2D g2dRadius = (Graphics2D) g.create();
        g2dRadius.setColor(new Color(255, 0, 0, 60));
        int rectWidth = 300;
        int rectHeight = 250;
        int offset = 70;
        int rectX = x + width / 2 - offset;
        int rectY = y + (height / 2) - (rectHeight / 2);

        switch (arah) {
            case "kanan": rectX = x + width / 2 - offset; break;
            case "kiri": rectX = x + width / 2 - (rectWidth - offset); break;
            case "bawah":
                rectX = x + width / 2 - rectWidth / 2;
                rectY = y + height / 2 - offset;
                break;
            case "atas":
                rectX = x + width / 2 - rectWidth / 2;
                rectY = y + height / 2 - (rectHeight - offset);
                break;
        }
        g2dRadius.fillRect(rectX, rectY, rectWidth, rectHeight);
        g2dRadius.dispose();
        Graphics2D g2dCone = (Graphics2D) g.create();
        
        // Don't draw detection cone if unconscious
        if (!isUnconscious) {
            g2dCone.setColor(new Color(255, 0, 0, 60));
            int centerX = x + width / 2, centerY = y + height / 2, coneLength = 120;
            double coneAngle = Math.toRadians(40);
            
            // Rotasi sesuai arah hadap polisi
            double angle = 0;
            switch (arah) {
                case "kanan": angle = 0; break;
                case "kiri": angle = Math.PI; break;
                case "atas": angle = -Math.PI / 2; break;
                case "bawah": angle = Math.PI / 2; break;
            }
            int[] px = new int[3];
            int[] py = new int[3];
            
            px[0] = centerX;
            py[0] = centerY;
            px[1] = centerX + (int)(coneLength * Math.cos(angle - coneAngle / 2));
            py[1] = centerY + (int)(coneLength * Math.sin(angle - coneAngle / 2));
            px[2] = centerX + (int)(coneLength * Math.cos(angle + coneAngle / 2));
            py[2] = centerY + (int)(coneLength * Math.sin(angle + coneAngle / 2));
            g2dCone.fillPolygon(px, py, 3);
        }
        g2dCone.dispose();
        // Gambar polisi/penjaga seperti biasa
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

    // Getter & Setter
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setArah(String arah) { this.arah = arah; }
    public String getArah() { return arah; }
}