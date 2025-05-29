import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Arena extends JPanel {
    protected BufferedImage mapImage, collisionMap;
    protected RobberyBob bob;
    protected List<Item> itemList;
    protected JButton pauseButton;  
    protected boolean isPaused = false;
    private PauseMenuPanel pauseMenuPanel;
    private int mouseX = 0, mouseY = 0; // Track mouse position

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
                // Tambahkan update counter di sini:
                collectedItemCount++;
                goldCollectedThisArena += item.getGoldValue();
                itemList.remove(i); // HAPUS item-nya langsung
                i--;
            }
        }

        }
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
