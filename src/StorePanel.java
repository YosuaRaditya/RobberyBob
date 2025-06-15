import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StorePanel extends JPanel{
    private JFrame parentFrame;
    private BufferedImage background;
    private int hoveredAbility = -1;
    private final Rectangle[] abilityButtons = new Rectangle[5];
    private final String[] abilityDescriptions = {
        "RobberyBob/Assets/StaminaDescription.png",
        "RobberyBob/Assets/GrabDescription.png",
        "RobberyBob/Assets/SpeedDescription.png",
        "RobberyBob/Assets/RottenDonutDescription.png",
        "RobberyBob/Assets/InvisibilityPotionDescription.png"
    };
    private final int[] abilityPrices = {100, 150, 200, 30, 100};
    private RobberyBob bob;
    private BufferedImage playIcon;
    private Rectangle playButton = new Rectangle(20, 20, 50, 80);

    public StorePanel(JFrame frame, RobberyBob bob) {
        this.parentFrame = frame;
        this.bob = bob;
        try{
            background = ImageIO.read(new File("RobberyBob/Assets/bg.jpg"));
        }catch (IOException e) {
            System.out.println("Gagal memuat gambar: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            playIcon = ImageIO.read(new File("RobberyBob/Assets/Play.png"));
        } catch (IOException e) {
            System.out.println("Gagal memuat Play icon: " + e.getMessage());
        }
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int prev = hoveredAbility;
                hoveredAbility = -1;
                for (int i = 0; i < abilityButtons.length; i++) {
                    if (abilityButtons[i] != null && abilityButtons[i].contains(e.getPoint())) {
                        hoveredAbility = i;
                        break;
                    }
                }
                if (prev != hoveredAbility) repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (playButton.contains(e.getPoint())) {
                    parentFrame.setContentPane(new HomePanel(parentFrame));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                    return;
                }
                for (int i = 0; i < abilityButtons.length; i++) {
                    if (abilityButtons[i] != null && abilityButtons[i].contains(e.getPoint())) {
                        if (GameData.gold >= abilityPrices[i]) {
                            GameData.gold -= abilityPrices[i];
                            if (bob != null) {
                                switch (i) {
                                    case 0:
                                        bob.increaseMaxStamina(20);
                                        break;
                                    case 1:
                                        bob.increaseGrabAbility(1);
                                        break;
                                    case 2:
                                        bob.increaseSpeed(0.5f);
                                        break;
                                    case 3:
                                        bob.setHasRottenDonut(true);
                                        break;
                                    case 4:
                                        bob.setHasInvisibilityPotion(true);
                                        break;
                                }
                            }
                            repaint();
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
        int storeX = getWidth() / 3, storeW = getWidth() * 2 / 3;

        g.setColor(Color.ORANGE);
        g.fillRect(storeX, 0, storeW, getHeight());
        try {
            BufferedImage coin = ImageIO.read(new File("RobberyBob/Assets/CoinIcon.png"));
            g.drawImage(coin, storeX + storeW - 240, 19, 225, 40, null);
        } catch (IOException e) {
            System.out.println("Gagal memuat gambar CoinIcon: " + e.getMessage());
            e.printStackTrace();
        }
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString(" " + GameData.gold, 1065, 52);
        try {
            BufferedImage storeImg = ImageIO.read(new File("RobberyBob/Assets/StoreInventoryTXT.png"));
            g.drawImage(storeImg, storeX + 300, 30, 180, 50, null);
        } catch (IOException e) {
            System.out.println("Gagal memuat gambar StoreTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage staminaIcon = ImageIO.read(new File("RobberyBob/Assets/StaminaIcon.png"));
            g.drawImage(staminaIcon, storeX, 90, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar StaminaIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage staminaTXT = ImageIO.read(new File("RobberyBob/Assets/StaminaTXT.png"));
            g.drawImage(staminaTXT, storeX + 100, 90, 50, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar StaminaTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage grabIcon = ImageIO.read(new File("RobberyBob/Assets/GrabIcon.png"));
            g.drawImage(grabIcon, storeX, 190, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar GrabIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage grabTXT = ImageIO.read(new File("RobberyBob/Assets/GrabTXT.png"));
            g.drawImage(grabTXT, storeX + 100, 190, 100, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar GrabTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage speedIcon = ImageIO.read(new File("RobberyBob/Assets/SpeedIcon.png"));
            g.drawImage(speedIcon, storeX, 290, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar SpeedIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage speedTXT = ImageIO.read(new File("RobberyBob/Assets/SpeedTXT.png"));
            g.drawImage(speedTXT, storeX + 100, 290, 50, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar SpeedTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage rottenDonutIcon = ImageIO.read(new File("RobberyBob/Assets/RottenDonutIcon.png"));
            g.drawImage(rottenDonutIcon, storeX, 390, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar RottenDonutIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage rottenDonutTXT = ImageIO.read(new File("RobberyBob/Assets/RottenDonutTXT.png"));
            g.drawImage(rottenDonutTXT, storeX + 100, 390, 100, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar RottenDonutTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage invisibilityPotionIcon = ImageIO.read(new File("RobberyBob/Assets/InvisibilityPotionIcon.png"));
            g.drawImage(invisibilityPotionIcon, storeX, 490, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar InvisibilityPotionIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage invisibilityPotionTXT = ImageIO.read(new File("RobberyBob/Assets/InvisibilityPotionTXT.png"));
            g.drawImage(invisibilityPotionTXT, storeX + 100, 490, 100, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar InvisibilityPotionTXT: " + e.getMessage());
            e.printStackTrace();
        }
        int[] yPos = {90, 190, 290, 390, 490};

        for (int i = 0; i < 5; i++) {
            abilityButtons[i] = new Rectangle(storeX, yPos[i], 100, 100);
            if (hoveredAbility == i) {
                g.setColor(Color.BLUE);
                g.fillRoundRect(storeX, yPos[i], getWidth() - storeX, 100, 20, 20);
            }
            try {
                BufferedImage icon = ImageIO.read(new File(
                    i == 0 ? "RobberyBob/Assets/StaminaIcon.png" :
                    i == 1 ? "RobberyBob/Assets/GrabIcon.png" :
                    i == 2 ? "RobberyBob/Assets/SpeedIcon.png" :
                    i == 3 ? "RobberyBob/Assets/RottenDonutIcon.png" :
                    "RobberyBob/Assets/InvisibilityPotionIcon.png"
                ));
                g.drawImage(icon, storeX, yPos[i], 100, 100, null);
            } catch (Exception e) {}
            try {
                BufferedImage coinIcon = ImageIO.read(new File("RobberyBob/Assets/Harga.png"));
                g.drawImage(coinIcon, storeX + 95, yPos[i] + 28, 100, 50, null);
            } catch (Exception e) {}
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString(String.valueOf(abilityPrices[i]), storeX + 115, yPos[i] + 65);
        }
        try {
            BufferedImage staminaIcon = ImageIO.read(new File("RobberyBob/Assets/StaminaIcon.png"));
            g.drawImage(staminaIcon, storeX, 90, 100, 100, null); 
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar StaminaIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage staminaTXT = ImageIO.read(new File("RobberyBob/Assets/StaminaTXT.png"));
            g.drawImage(staminaTXT, storeX + 100, 90, 50, 50, null); 
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar StaminaTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage grabIcon = ImageIO.read(new File("RobberyBob/Assets/GrabIcon.png"));
            g.drawImage(grabIcon, storeX, 190, 100, 100, null); 
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar GrabIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage grabTXT = ImageIO.read(new File("RobberyBob/Assets/GrabTXT.png"));
            g.drawImage(grabTXT, storeX + 100, 190, 100, 50, null); 
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar GrabTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage speedIcon = ImageIO.read(new File("RobberyBob/Assets/SpeedIcon.png"));
            g.drawImage(speedIcon, storeX, 290, 100, 100, null); 
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar SpeedIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage speedTXT = ImageIO.read(new File("RobberyBob/Assets/SpeedTXT.png"));
            g.drawImage(speedTXT, storeX + 100, 290, 50, 50, null); 
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar SpeedTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage rottenDonutIcon = ImageIO.read(new File("RobberyBob/Assets/RottenDonutIcon.png"));
            g.drawImage(rottenDonutIcon, storeX, 390, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar RottenDonutIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage rottenDonutTXT = ImageIO.read(new File("RobberyBob/Assets/RottenDonutTXT.png"));
            g.drawImage(rottenDonutTXT, storeX + 100, 390, 100, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar RottenDonutTXT: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage invisibilityPotionIcon = ImageIO.read(new File("RobberyBob/Assets/InvisibilityPotionIcon.png"));
            g.drawImage(invisibilityPotionIcon, storeX, 490, 100, 100, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar InvisibilityPotionIcon: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            BufferedImage invisibilityPotionTXT = ImageIO.read(new File("RobberyBob/Assets/InvisibilityPotionTXT.png"));
            g.drawImage(invisibilityPotionTXT, storeX + 100, 490, 100, 50, null); 
        } catch (Exception e) {
             System.out.println("Gagal memuat gambar InvisibilityPotionTXT: " + e.getMessage());
            e.printStackTrace();
        }
        if (hoveredAbility != -1) {
            try {
                BufferedImage descImg = ImageIO.read(new File(abilityDescriptions[hoveredAbility]));
                g.drawImage(descImg, storeX - 375, 285, 350, 400, null);
            } catch (IOException e) {
                System.out.println("Gagal memuat gambar Description: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (playIcon != null) {
            g.drawImage(playIcon, playButton.x, playButton.y, playButton.width, playButton.height, null);
        }
    }
}
