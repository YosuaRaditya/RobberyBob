
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PoliceInteractionPanel extends JPanel{
    private BufferedImage background;
    private JFrame parentFrame;
    private Arena arena;
    private JButton menuButton, upgradesButton, retryButton;
    private JLabel retryLabel, upgradesLabel;
    private RobberyBob bob;

    public PoliceInteractionPanel(JFrame parentFrame, Arena arena) {
        this.parentFrame = parentFrame;
        this.arena = arena;
        this.bob = arena.bob;
        setLayout(null);
        try {
            background = ImageIO.read(new File("RobberyBob/Assets/PoliceInteractionPanel.png"));
        } catch (IOException e) {
            System.out.println("Gagal load police interaction background: " + e.getMessage());
        }
        iniButton();
    }
    public void iniButton(){
        menuButton = createIconButton("RobberyBob/Assets/MenuIcon.png", 116);
        upgradesButton = createIconButton("RobberyBob/Assets/UpgradesIcon.png", 100);
        retryButton = createIconButton("RobberyBob/Assets/restartIcon.png", 100);
        add(menuButton);
        add(upgradesButton);
        add(retryButton);
        menuButton.setBounds(0, 0, 116, 80);
        upgradesButton.setBounds(490, 285, 100, 100);
        retryButton.setBounds(675, 285, 100, 100);
        retryLabel = new JLabel();
        try {
            BufferedImage retryTxtImg = ImageIO.read(new File("RobberyBob/Assets/RetryTXT.png"));
            Image scaled = retryTxtImg.getScaledInstance(100, 30, Image.SCALE_SMOOTH);
            retryLabel.setIcon(new ImageIcon(scaled));
            retryLabel.setBounds(675, 385, 100, 30);
        } catch (IOException e) {
            System.out.println("Gagal load RetryTXT: " + e.getMessage());
        }
        add(retryLabel);
        upgradesLabel = new JLabel();
        try {
            BufferedImage upgradesTxtImg = ImageIO.read(new File("RobberyBob/Assets/UpgradesTXT.png"));
            Image scaled = upgradesTxtImg.getScaledInstance(100, 30, Image.SCALE_SMOOTH);
            upgradesLabel.setIcon(new ImageIcon(scaled));
            upgradesLabel.setBounds(490, 385, 100, 30);
        } catch (IOException e) {
            System.out.println("Gagal load UpgradesTXT: " + e.getMessage());
        }
        add(upgradesLabel);
        menuButton.addActionListener(e -> {
            parentFrame.setContentPane(new HomePanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        upgradesButton.addActionListener(e -> {
            parentFrame.setContentPane(new StorePanel(parentFrame, bob));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        retryButton.addActionListener(e -> {
            Arena newArena = null;

            if (arena instanceof ArenaA) {
                newArena = new ArenaA(parentFrame);
            } else if (arena instanceof ArenaB) {
                newArena = new ArenaB(parentFrame);
            } else if (arena instanceof ArenaC) {
                newArena = new ArenaC(parentFrame);
            } else if (arena instanceof ArenaD) {
                newArena = new ArenaD(parentFrame);
            }
            if (newArena != null) {
                parentFrame.setContentPane(newArena);
                parentFrame.revalidate();
                parentFrame.repaint();
                newArena.requestFocusInWindow();
            }
        });
    }
    private JButton createIconButton(String imagePath, int size) {
        JButton button = new JButton();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setPreferredSize(new Dimension(size, size));
        } catch (IOException e) {
            System.out.println("Gagal load icon: " + imagePath);
        }

        return button;
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(960, 540);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
