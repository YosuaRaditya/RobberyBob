import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Arena extends JPanel {
    protected BufferedImage mapImage, collisionMap;
    protected JButton backButton;
    protected RobberyBob bob;

    public Arena(String mapPath, String collisionPath, int startX, int startY, JFrame parentFrame) {
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
        bob.draw(g, this);
    }
}
