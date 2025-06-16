
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

public class OptionsPanel extends JPanel{
    private JFrame parentFrame;
    private BufferedImage background, playIcon, audioOnIcon, audioOffIcon;
    private MusicPlayer musicPlayer;
    private final String musicPath = "RobberyBob/Assets/Trouble Makers (Loopable).wav";
    private Rectangle playButton = new Rectangle(20, 20, 50, 80);
    private Rectangle audioButton;
    private boolean audioOn = true;

    public OptionsPanel(JFrame frame){
        this.parentFrame = frame;
        try{
            background = ImageIO.read(new File("RobberyBob/Assets/bg.jpg"));
        }catch (IOException e) {
            System.out.println("Gagal memuat gambar: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            playIcon = ImageIO.read(new File("RobberyBob/Assets/Play.png"));
            audioOnIcon = ImageIO.read(new File("RobberyBob/Assets/AudioOn.png"));
            audioOffIcon = ImageIO.read(new File("RobberyBob/Assets/AudioOff.png"));
        } catch (IOException e) {
            System.out.println("Gagal memuat Play icon: " + e.getMessage());
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (playButton.contains(e.getPoint())) {
                    parentFrame.setContentPane(new HomePanel(parentFrame));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                    return;
                }
                if (audioButton != null && audioButton.contains(e.getPoint())) {
                    audioOn = !audioOn; 
                    GameData.audioOn = audioOn;

                    if (audioOn) {
                        GameData.audioPlayer.play("RobberyBob/Assets/Trouble Makers (Loopable).wav");
                    } else {
                        GameData.audioPlayer.stop();
                    }
                    repaint();
                }
            }
        });
        this.audioOn = GameData.audioOn;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
        try {
            BufferedImage optionsTxt = ImageIO.read(new File("RobberyBob/Assets/OptionsTXT.png"));
            int txtW = 300, txtH = 70, txtX = (getWidth() - txtW) / 2, txtY = 60;
            g.drawImage(optionsTxt, txtX, txtY, txtW, txtH, null);
        } catch (IOException e) {
            System.out.println("Gagal memuat OptionsTXT: " + e.getMessage());
        }
        try {
            BufferedImage audioTxt = ImageIO.read(new File("RobberyBob/Assets/AudioTXT.png"));
            int iconW = 120, iconH = 120, iconX = (getWidth() - iconW) / 2, iconY = getHeight() / 2 - iconH / 2, txtW = 120, txtH = 40, txtX = (getWidth() - txtW) / 2, txtY = iconY + iconH + 15;

            audioButton = new Rectangle(iconX, iconY, iconW, iconH);
            if (audioOn && audioOnIcon != null) {
                g.drawImage(audioOnIcon, iconX, iconY, iconW, iconH, null);
            } else if (!audioOn && audioOffIcon != null) {
                g.drawImage(audioOffIcon, iconX, iconY, iconW, iconH, null);
            }
            g.drawImage(audioTxt, txtX + 35, txtY, txtW, txtH, null);
        } catch (IOException e) {
            System.out.println("Gagal memuat AudioIcon atau AudioTXT: " + e.getMessage());
        }
        int storeX = getWidth() / 3, storeW = getWidth() * 2 / 3;

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
        if (playIcon != null) {
            g.drawImage(playIcon, playButton.x, playButton.y, playButton.width, playButton.height, null);
        }
    }
}
