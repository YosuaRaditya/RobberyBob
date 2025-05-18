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
    protected JButton backButton;
    protected RobberyBob bob;
    protected List<Item> itemList;

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
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), null);
        }

        // Gambar barang (kotak merah)
        for (Item item : itemList) {
            item.draw(g, this);
        }


        // Gambar RobberyBob
        bob.draw(g, this);

        // Gambar area deteksi (lingkaran kuning transparan)
        bob.drawDetectionArea(g);

        // Cek apakah ada barang yang masuk area lingkaran
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (bob.getDetectionCircle().intersects(item.getBounds())) {
                GameData.gold += item.getGoldValue();
                itemList.remove(i);
                i--; // supaya gak skip item selanjutnya
            }
        }
    }
}
