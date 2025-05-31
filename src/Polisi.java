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

    public void setTargetBob(RobberyBob bob) {
        this.targetBob = bob;
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading polisi: " + e.getMessage());
            return null;
        }
    }

// ...existing code...
    @Override
public void update() {
    if (isTeleporting) return; // Jangan update saat teleport animasi

    if (targetBob != null) {
        int bobCenterX = targetBob.x + targetBob.width / 2;
        int bobCenterY = targetBob.y + targetBob.height / 2;

        // Area deteksi persegi panjang (harus sama dengan draw)
        int rectWidth = 300;
        int rectHeight = 250;
        int offset = 70; // harus sama dengan di draw
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
        double cos = Math.cos(-angle);
        double sin = Math.sin(-angle);
        int anchorX = rectX;
        int anchorY = rectY + rectHeight / 2;
        int relBobX = bobCenterX - anchorX;
        int relBobY = bobCenterY - anchorY;
        int localX = (int)(relBobX * cos - relBobY * sin);
        int localY = (int)(relBobX * sin + relBobY * cos);

        if (localX >= 0 && localX <= rectWidth && localY >= -rectHeight/2 && localY <= rectHeight/2 && !targetBob.isHiding()) {
            chasing = true;
            if (teleportDelayTimer != null) {
                teleportDelayTimer.stop();
                teleportDelayTimer = null;
            }
            if (teleportTimer != null) {
                teleportTimer.stop();
                teleportTimer = null;
            }
            // Tambahkan jejak Bob
            if (bobTrail.isEmpty() || !bobTrail.peek().equals(new Point(bobCenterX, bobCenterY))) {
                bobTrail.add(new Point(bobCenterX, bobCenterY));
                if (bobTrail.size() > 30) bobTrail.poll();
            }
        } else {
            if (chasing) {
                chasing = false;
                bobTrail.clear();
                // Mulai delay 2 detik sebelum teleport
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
        }
    }

    if (chasing && !bobTrail.isEmpty()) {
        Point next = bobTrail.peek();
        int targetX = next.x - width / 2;
        int targetY = next.y - height / 2;
        int dx = targetX - x;
        int dy = targetY - y;

        // Hanya gerak satu sumbu per update (prioritas X dulu)
        if (Math.abs(dx) > 0) {
            int step = Math.min(speed, Math.abs(dx));
            x += (dx > 0) ? step : -step;
            arah = dx > 0 ? "kanan" : "kiri";
        } else if (Math.abs(dy) > 0) {
            int step = Math.min(speed, Math.abs(dy));
            y += (dy > 0) ? step : -step;
            arah = dy > 0 ? "bawah" : "atas";
        }

        // Jika sudah sampai ke titik, ambil jejak berikutnya
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            x = targetX;
            y = targetY;
            bobTrail.poll();
        }
    } else if (!isTeleporting && (teleportDelayTimer == null)) {
        // Patrol normal jika tidak teleport dan tidak delay
        if (patrolPoints == null || patrolPoints.length == 0) return;
        int targetX = patrolPoints[patrolIndex][0];
        int targetY = patrolPoints[patrolIndex][1];
        int dx = targetX - x;
        int dy = targetY - y;

        // Hanya gerak satu sumbu per update (prioritas X dulu)
        if (Math.abs(dx) > 0) {
            int step = Math.min(speed, Math.abs(dx));
            x += (dx > 0) ? step : -step;
            arah = dx > 0 ? "kanan" : "kiri";
        } else if (Math.abs(dy) > 0) {
            int step = Math.min(speed, Math.abs(dy));
            y += (dy > 0) ? step : -step;
            arah = dy > 0 ? "bawah" : "atas";
        }

        // Jika sudah sampai ke titik patrol, lanjut ke berikutnya
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            x = targetX;
            y = targetY;
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