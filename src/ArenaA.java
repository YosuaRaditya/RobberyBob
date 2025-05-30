import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Timer;

public class ArenaA extends Arena {
    private CCTV cctv;
    private double cctvAngle = Math.PI / 2;
    private boolean cctvRight = true;
    private Polisi polisi;
    private int[][] polisiPatrolPoints = {
        {487, 175},
        {875, 175}
    };
    private int polisiTargetIndex = 1, polisiSpeed = 2;

    public ArenaA(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaA.jpg", 
            "RobberyBob/Assets/collisionArenaA.jpg", 
            120, 370, 
            parentFrame,
            getBarangArenaA()  // harus static method supaya bisa dipanggil di sini
        );
        cctv = new CCTV(975, 64, 100, 64, "RobberyBob/Assets/cctv.png");
        polisi = new Polisi(polisiPatrolPoints[0][0], polisiPatrolPoints[0][1], 100, 64, "RobberyBob/Assets/polisiKanan.png");
        Timer cctvTimer = new Timer(50, e -> {
            double minAngle = Math.PI / 4, maxAngle = 3 * Math.PI / 4, speed = Math.toRadians(1.5);

            if (cctvRight){
                cctvAngle += speed;
            }else{
                cctvAngle -= speed;   
            }

            if (cctvAngle > maxAngle) {
                cctvAngle = maxAngle;
                cctvRight = false;
            } else if (cctvAngle < minAngle) {
                cctvAngle = minAngle;
                cctvRight = true;
            }
            repaint();
        });
        cctvTimer.start();
        Timer patrolTimer = new Timer(20, e -> {
            int targetX = polisiPatrolPoints[polisiTargetIndex][0], targetY = polisiPatrolPoints[polisiTargetIndex][1], dx = targetX - polisi.getX(), dy = targetY - polisi.getY(), dist = (int)Math.sqrt(dx * dx + dy * dy);
            if (dist > polisiSpeed) {
                polisi.setX(polisi.getX() + polisiSpeed * dx / dist);
                polisi.setY(polisi.getY() + polisiSpeed * dy / dist);
            } else {
                polisi.setX(targetX);
                polisi.setY(targetY);
                int nextIndex = (polisiTargetIndex + 1) % polisiPatrolPoints.length, nextX = polisiPatrolPoints[nextIndex][0];
                if (nextX < targetX) {
                    polisi.setImage("RobberyBob/Assets/polisiKiri.png");
                } else if (nextX > targetX) {
                    polisi.setImage("RobberyBob/Assets/polisiKanan.png");
                }
                polisiTargetIndex = nextIndex;
            }
            repaint();
        });
        patrolTimer.start();
    }

    private boolean isPointInTriangle(int px, int py, int[] x, int[] y) {
        double d1 = (px - x[1]) * (y[0] - y[1]) - (x[0] - x[1]) * (py - y[1]);
        double d2 = (px - x[2]) * (y[1] - y[2]) - (x[1] - x[2]) * (py - y[2]);
        double d3 = (px - x[0]) * (y[2] - y[0]) - (x[2] - x[0]) * (py - y[0]);
        boolean has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0), has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        return !(has_neg && has_pos);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(cctv != null){
            cctv.draw(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int cctvx = 975 + 50, cctvy = 64 + 32, visionLength = 500;
            double sudut = Math.toRadians(60);
            int x1 = cctvx + (int)(visionLength * Math.cos(cctvAngle - sudut / 2)), y1 = cctvy + (int)(visionLength * Math.sin(cctvAngle - sudut / 2)), x2 = cctvx + (int)(visionLength * Math.cos(cctvAngle + sudut / 2)), y2 = cctvy + (int)(visionLength * Math.sin(cctvAngle + sudut / 2));
            
            int[] xPoints = {cctvx, x1, x2};
            int[] yPoints = {cctvy, y1, y2};
            int bobX = bob.x + bob.width / 2, bobY = bob.y + bob.height / 2;
            boolean bobInCone = isPointInTriangle(bobX, bobY, xPoints, yPoints);

            if (bobInCone) {
                g2d.setColor(new Color(255, 0, 0, 80));
            } else {
                g2d.setColor(new Color(0, 225, 0, 80));
            }
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.dispose();
        }
        if(polisi != null){
            polisi.draw(g);
        }
    }

    public static List<Item> getBarangArenaA() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(600, 275));
        items.add(new Kalung(800, 543));
        items.add(new Emas(126, 360));
        items.add(new Uang(310, 275));
        return items;
    }
}

