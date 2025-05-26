import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class Arena extends JPanel {
    protected BufferedImage mapImage, collisionMap;
    protected RobberyBob bob;
    protected List<Item> itemList;
    protected JButton pauseButton;  
    protected boolean isPaused = false;
    private PauseMenuPanel pauseMenuPanel;  

    public Arena(String mapPath, String collisionPath, int startX, int startY, JFrame parentFrame, List<Item> itemList) {
        this.itemList = itemList;

        setLayout(null);

        try {
            mapImage = ImageIO.read(new File(mapPath));
            collisionMap = ImageIO.read(new File(collisionPath));
        } catch (IOException e) {
            System.out.println("Error loading map: " + e.getMessage());
        }

        bob = new RobberyBob(startX, startY);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                bob.handleKeyPressed(e.getKeyCode(), collisionMap, getWidth(), getHeight());
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                bob.handleKeyReleased(e.getKeyCode());
                repaint();
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
        
        add(pauseMenuPanel, JLayeredPane.POPUP_LAYER);
        pauseMenuPanel.setVisible(true);
        repaint();
    }

    private void resumeGame() {
        if (pauseMenuPanel != null) {
            pauseMenuPanel.setVisible(false);
            remove(pauseMenuPanel);
        }
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

        // Cek collision dengan item
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (bob.getDetectionCircle().intersects(item.getBounds())) {
                bob.addPendingGold(item.getGoldValue()); // Tambahin, bukan set ulang

                if ("Extra".equals(item.getJenis())) {
                    bob.setHasExtraItem(true);
                }
                itemList.remove(i); // HAPUS item-nya langsung
                i--;
            }
        }

        if (bob.hasNambahGold() && bob.hasPendingGold()) {
            GameData.gold += bob.getPendingGold();
            System.out.println("Gold ditambah: " + bob.getPendingGold());

            bob.setHasExtraItem(false);
            bob.setHasNambahGold(false);
            bob.resetPendingGold(); // Tambahkan ini biar gak terus nambah
        }
    }
}
