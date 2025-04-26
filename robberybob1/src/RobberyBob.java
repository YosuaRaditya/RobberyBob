import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RobberyBob extends JPanel {
    int boardWidth = 1280;
    int boardHeight = 720;

    int gold = 1000;

    Image backgroundImage;
    Image penjaraMapImage, labMapImage, desaMapImage, kotaMapImage;

    JButton map1Button, map2Button, map3Button, map4Button;
    JButton shopButton, leaderboardButton, opsiButton;
    JLabel goldLabel;

    String currentScreen = "menu"; // "menu", "penjara", "lab", "desa", "kota"

    RobberyBob() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setLayout(null);

        backgroundImage = new ImageIcon("AssetsGambar/bgHome.jpg").getImage();
        penjaraMapImage = new ImageIcon("AssetsGambar/penjaraBesi.jpg").getImage();
        labMapImage = new ImageIcon("AssetsGambar/labRahasia.jpg").getImage();
        desaMapImage = new ImageIcon("AssetsGambar/pinggiranKota.jpg").getImage();
        kotaMapImage = new ImageIcon("AssetsGambar/pusatKota.jpg").getImage();

        goldLabel = new JLabel("      " + gold);
        goldLabel.setBounds(1050, 20, 200, 50);
        goldLabel.setForeground(Color.BLACK);
        goldLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(goldLabel);

        int mapWidth = 350;
        int mapHeight = 250;
        int spacing = 30;

        int startX = boardWidth - mapWidth * 2 - spacing - 100;
        int startY = boardHeight - mapHeight * 2 - spacing - 60;

        map1Button = createMapButton("AssetsGambar/penjaraBesi.png", startX, startY, mapWidth, mapHeight, "penjara");
        map2Button = createMapButton("AssetsGambar/labRahasia.png", startX + mapWidth + spacing, startY, mapWidth, mapHeight, "lab");
        map3Button = createMapButton("AssetsGambar/pinggiranKota.png", startX, startY + mapHeight + spacing, mapWidth, mapHeight, "desa");
        map4Button = createMapButton("AssetsGambar/pusatKota.png", startX + mapWidth + spacing, startY + mapHeight + spacing, mapWidth, mapHeight, "kota");

        add(map1Button);
        add(map2Button);
        add(map3Button);
        add(map4Button);

        int menuWidth = 300;
        int menuHeight = 80;
        int menuSpacing = 30;

        int menuStartX = 100;
        int menuStartY = 170;

        shopButton = createMenuButton("Shop", menuStartX, menuStartY, menuWidth, menuHeight);
        leaderboardButton = createMenuButton("Leaderboard", menuStartX, menuStartY + menuHeight + menuSpacing, menuWidth, menuHeight);
        opsiButton = createMenuButton("Opsi", menuStartX, menuStartY + (menuHeight + menuSpacing) * 2, menuWidth, menuHeight);

        add(shopButton);
        add(leaderboardButton);
        add(opsiButton);
    }

    private JButton createMapButton(String imagePath, int x, int y, int width, int height, String targetScreen) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setBounds(x, y, width, height);
        button.addActionListener(e -> {
            currentScreen = targetScreen;
            removeAll(); // Hapus semua komponen menu
            repaint();
        });

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        return button;
    }

    private JButton createMenuButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);

        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setContentAreaFilled(true);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (button.getModel().isPressed()) {
                    g2.setColor(button.getBackground().darker());
                } else {
                    g2.setColor(button.getBackground());
                }
                g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), button.getHeight(), button.getHeight());
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, button.getWidth()-1, button.getHeight()-1, button.getHeight(), button.getHeight());

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(button.getText());
                int stringHeight = fm.getAscent();
                g2.drawString(button.getText(), (button.getWidth() - stringWidth) / 2, (button.getHeight() + stringHeight) / 2 - 4);
                g2.dispose();
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (currentScreen.equals("menu")) {
            g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
        } else if (currentScreen.equals("penjara")) {
            g.drawImage(penjaraMapImage, 0, 0, boardWidth, boardHeight, null);
        } else if (currentScreen.equals("lab")) {
            g.drawImage(labMapImage, 0, 0, boardWidth, boardHeight, null);
        } else if (currentScreen.equals("desa")) {
            g.drawImage(desaMapImage, 0, 0, boardWidth, boardHeight, null);
        } else if (currentScreen.equals("kota")) {
            g.drawImage(kotaMapImage, 0, 0, boardWidth, boardHeight, null);
        }
    }
}
