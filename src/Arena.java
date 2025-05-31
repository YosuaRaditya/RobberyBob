import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ArrayList;

public class Arena extends JPanel {
    protected BufferedImage mapImage, collisionMap;
    protected RobberyBob bob;
    protected List<Item> itemList;
    protected JButton pauseButton;  
    protected boolean isPaused = false;
    private PauseMenuPanel pauseMenuPanel;
    private int mouseX = 0, mouseY = 0; // Track mouse position

    protected List<CCTV> cctvs = new ArrayList<>();
    protected List<Double> cctvAngles = new ArrayList<>();
    protected List<Boolean> cctvRights = new ArrayList<>();
    private boolean cctvTriggered = false;
    private long lastCCTVTriggerTime = 0;
    private final long CCTV_TRIGGER_COOLDOWN = 2000; // ms

    protected List<Penjaga> penjagaList = new ArrayList<>();

    private Timer gameTimer;
    private int elapsedSeconds = 0;
    private boolean isTimerRunning = false; 

    private int totalItemCount = 0;
    private int collectedItemCount = 0;
    private int goldCollectedThisArena = 0;

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void startTimer() {
        if (gameTimer == null) {
            gameTimer = new Timer(1000, e -> {
                elapsedSeconds++;
                repaint();
            });
        }
        if (!isTimerRunning) {
            gameTimer.start();
            isTimerRunning = true;
        }
    }

    public void pauseTimer() {
        if (gameTimer != null && isTimerRunning) {
            gameTimer.stop();
            isTimerRunning = false;
        }
    }

    public void resumeTimer() {
        if (gameTimer != null && !isTimerRunning) {
            gameTimer.start();
            isTimerRunning = true;
        }
    }

    public void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
            isTimerRunning = false;
        }
    }


    public Arena(String mapPath, String collisionPath, int startX, int startY, JFrame parentFrame, List<Item> itemList) {
        this.itemList = itemList;
        this.totalItemCount = itemList.size();

        setLayout(null);

        try {
            mapImage = ImageIO.read(new File(mapPath));
            collisionMap = ImageIO.read(new File(collisionPath));
        } catch (IOException e) {
            System.out.println("Error loading map: " + e.getMessage());
        }

        bob = new RobberyBob(startX, startY);
        startTimer();

        bob.setOnFinish(() -> {
            GameData.gold += goldCollectedThisArena; // gunakan goldCollectedThisArena
            stopTimer();
            SwingUtilities.invokeLater(() -> {
                FinishMenuPanel finishPanel = new FinishMenuPanel(parentFrame, this);
                finishPanel.setBounds(0, 0, getWidth(), getHeight());
                parentFrame.setContentPane(finishPanel);
                parentFrame.revalidate();
                parentFrame.repaint();
            });
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_I && bob.isInHidingArea()) {
                    // Toggle hiding state when 'I' is pressed and in hiding area
                    bob.setHiding(!bob.isHiding());
                } else {
                    bob.handleKeyPressed(e.getKeyCode(), collisionMap, getWidth(), getHeight());
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                bob.handleKeyReleased(e.getKeyCode());
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint(); // Repaint to update mouse coordinates display
            }
        });

        setFocusable(true);
        // Tambahkan di akhir constructor Arena (setelah setFocusable)
        Timer shineTimer = new Timer(40, e -> {
            for (Item item : itemList) {
                item.updateShine(); // update frame animasi shine di setiap item
            }
            repaint(); // render ulang
        });
        shineTimer.start();
        requestFocusInWindow(); 

        initPauseButton(parentFrame);
        setupCCTVandPenjaga();

        Timer cctvTimer = new Timer(50, e -> {
            for (int i = 0; i < cctvs.size(); i++) {
                double minAngle = Math.PI / 4, maxAngle = 3 * Math.PI / 4, speed = Math.toRadians(1.5);
                if (cctvRights.get(i)) {
                    cctvAngles.set(i, cctvAngles.get(i) + speed);
                } else {
                    cctvAngles.set(i, cctvAngles.get(i) - speed);
                }
                if (cctvAngles.get(i) > maxAngle) {
                    cctvAngles.set(i, maxAngle);
                    cctvRights.set(i, false);
                } else if (cctvAngles.get(i) < minAngle) {
                    cctvAngles.set(i, minAngle);
                    cctvRights.set(i, true);
                }
            }
            repaint();
        });
        cctvTimer.start();

        Timer penjagaTimer = new Timer(20, e -> {
            for (Penjaga penjaga : penjagaList) {
                penjaga.update();
            }
            repaint();
        });
        penjagaTimer.start();
    }

    protected void setupCCTVandPenjaga() {
        // To be overridden by child class
    }

    private void initPauseButton(JFrame parentFrame) {
        try {
            pauseButton = new JButton();
            pauseButton.setBounds(20, 20, 60, 60);
            pauseButton.setContentAreaFilled(false);
            pauseButton.setBorderPainted(false);
            pauseButton.setFocusable(false);
            pauseButton.addActionListener(e -> togglePause(parentFrame));
            
            // Set ikon awal
            updatePauseButtonIcon();
            
            add(pauseButton);
        } catch (Exception e) {
            System.out.println("Gagal inisialisasi pause button: " + e.getMessage());
        }
    }

    private void togglePause(JFrame parentFrame) {
        isPaused = !isPaused;
        
        // Update ikon tombol
        updatePauseButtonIcon();
        
        if (isPaused) {
            showPauseMenu(parentFrame);
        } else {
            resumeGame();
        }
    }

    private void updatePauseButtonIcon() {
        try {
            String iconPath = isPaused ? "RobberyBob/Assets/pauseIcon.png" : "RobberyBob/Assets/playIcon.png";
            BufferedImage originalImage = ImageIO.read(new File(iconPath));
            int newWidth = 60;
            int newHeight = 60;
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            
            pauseButton.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            System.out.println("Gagal load pause/play icon: " + e.getMessage());
        }
    }

    private void showPauseMenu(JFrame parentFrame) {
        // Buat pause menu jika belum ada
        if (pauseMenuPanel == null) {
            pauseMenuPanel = new PauseMenuPanel(parentFrame, this);
            pauseMenuPanel.setBounds(0, 0, getWidth(), getHeight());
        }

        pauseTimer();
        
        add(pauseMenuPanel, JLayeredPane.POPUP_LAYER);
        pauseMenuPanel.setVisible(true);
        repaint();
    }

    private void resumeGame() {
        if (pauseMenuPanel != null) {
            pauseMenuPanel.setVisible(false);
            remove(pauseMenuPanel);
        }
        resumeTimer();
        requestFocus(); // Kembalikan fokus ke game
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
        }

        // Gambar barang
        for (Item item : itemList) {
            item.draw(g, this);
        }
        
        // Gambar RobberyBob
        bob.draw(g, this);
        
        // Only draw visibility overlay if player is not hiding
        if (!bob.isHiding()) {
            bob.drawVisibilityOverlay(g, getWidth(), getHeight());
        }

        // Display mouse coordinates and hiding status
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Mouse: (" + mouseX + ", " + mouseY + ")", 10, 20);

        if (bob.isInHidingArea()) {
            g.setColor(Color.GREEN);
            g.drawString("Press 'I' to hide/unhide", 10, 40);
        }

        // Draw stamina bar
        bob.drawStaminaBar(g);

        // Running indicator
        if (bob.isRunning()) {
            g.setColor(Color.WHITE);
            g.drawString("RUNNING", 230, 82);
        }

        // Cek collision dengan item - hanya saat tidak bersembunyi
        if (!bob.isHiding()) {
             // Cek collision dengan item
            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                if (bob.getDetectionCircle().intersects(item.getBounds())) {
                    if ("Extra".equals(item.getJenis())) {
                        bob.setHasExtraItem(true);
                    }
                    collectedItemCount++;
                    goldCollectedThisArena += item.getGoldValue();
                    itemList.remove(i);
                    break; // <-- tambahkan ini!
                }
            }
        }

        for (Penjaga penjaga : penjagaList) {
            penjaga.draw(g);
        }

        // Draw CCTV vision cone
        boolean bobDetectedByCCTV = false;
        for (int i = 0; i < cctvs.size(); i++) {
            CCTV cctv = cctvs.get(i);
            double angle = cctvAngles.get(i);
            cctv.draw(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int cctvx = cctv.getX() + cctv.getWidth() / 2;
            int cctvy = cctv.getY() + cctv.getHeight() / 2;
            int visionLength = 500;
            double sudut = Math.toRadians(60);
            int x1 = cctvx + (int)(visionLength * Math.cos(angle - sudut / 2));
            int y1 = cctvy + (int)(visionLength * Math.sin(angle - sudut / 2));
            int x2 = cctvx + (int)(visionLength * Math.cos(angle + sudut / 2));
            int y2 = cctvy + (int)(visionLength * Math.sin(angle + sudut / 2));
            int[] xPoints = {cctvx, x1, x2};
            int[] yPoints = {cctvy, y1, y2};
            int bobX = bob.x + bob.width / 2, bobY = bob.y + bob.height / 2;
            boolean bobInCone = isPointInTriangle(bobX, bobY, xPoints, yPoints);
            if (bobInCone) {
                g2d.setColor(new Color(255, 0, 0, 80));
                bobDetectedByCCTV = true;
            } else {
                g2d.setColor(new Color(0, 225, 0, 80));
            }
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.dispose();
        }

        if (bobDetectedByCCTV && !cctvTriggered && !penjagaList.isEmpty()) {
    long now = System.currentTimeMillis();
    if (now - lastCCTVTriggerTime > CCTV_TRIGGER_COOLDOWN) {
        cctvTriggered = true;
        lastCCTVTriggerTime = now;
        // Trigger polisi pertama di list ke koordinat yang kamu mau
        Penjaga penjaga = penjagaList.get(0);
        if (penjaga instanceof Polisi) {
            ((Polisi)penjaga).moveTo(800, 400); // <-- GANTI KOORDINAT DI SINI SESUAI KEINGINAN
        }
        // Reset trigger setelah delay
        Timer resetTimer = new Timer((int)CCTV_TRIGGER_COOLDOWN, e -> cctvTriggered = false);
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
}
    }

    protected boolean isPointInTriangle(int px, int py, int[] x, int[] y) {
        double d1 = (px - x[1]) * (y[0] - y[1]) - (x[0] - x[1]) * (py - y[1]);
        double d2 = (px - x[2]) * (y[1] - y[2]) - (x[1] - x[2]) * (py - y[2]);
        double d3 = (px - x[0]) * (y[2] - y[0]) - (x[2] - x[0]) * (py - y[0]);
        boolean has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0), has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        return !(has_neg && has_pos);
    }


    public int getTotalItemCount() {
        return totalItemCount; // simpan jumlah item awal di field ini
    }
    public int getCollectedItemCount() {
        return collectedItemCount; // simpan jumlah item yang sudah diambil di field ini
    }
    public int getGoldCollectedThisArena() {
        return goldCollectedThisArena; // simpan gold yang didapat di arena ini
    }
}
