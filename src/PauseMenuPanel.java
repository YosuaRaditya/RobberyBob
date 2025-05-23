import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PauseMenuPanel extends JPanel {
    private BufferedImage background;
    private JFrame parentFrame;
    private Arena arena;

    private JButton restartButton;
    private JButton homeButton;
    private JButton nextButton;

    private final String[] buttonLabels = { "Menu", "Coba Lagi", "Selanjutnya" };
    private int buttonWidth = 150;
    private int buttonHeight = 150;
    private int spacing = 20;
    private int centerY = 290;
    private int labelYOffset = 20;

    public PauseMenuPanel(JFrame parentFrame, Arena arena) {
        this.parentFrame = parentFrame;
        this.arena = arena;

        setLayout(null); // absolute layout

        try {
            background = ImageIO.read(new File("RobberyBob/Assets/pauseBackground.jpg"));
        } catch (IOException e) {
            System.out.println("Gagal load pause background: " + e.getMessage());
        }

        initButtons();
    }

    private void initButtons() {
        int totalWidth = (buttonWidth * 3) + (spacing * 2);
        int startX = (getPreferredSize().width - totalWidth) / 2 + 150;

        // Buat tombolnya
        homeButton = createIconButton("RobberyBob/Assets/exitIcon.png", 150);       // Menu
        restartButton = createIconButton("RobberyBob/Assets/restartIcon.png", 150); // Coba Lagi
        nextButton = createIconButton("RobberyBob/Assets/nextIcon.png", 150);       // Selanjutnya

        // Tambahkan tombol ke panel
        add(homeButton);
        add(restartButton);
        add(nextButton);

        // Atur posisi tombol sesuai urutan yang benar
        homeButton.setBounds(startX, centerY, buttonWidth, buttonHeight);
        restartButton.setBounds(startX + buttonWidth + spacing, centerY, buttonWidth, buttonHeight);
        nextButton.setBounds(startX + 2 * (buttonWidth + spacing), centerY, buttonWidth, buttonHeight);

        // Action listener untuk tombol home/menu
        homeButton.addActionListener(e -> {
            parentFrame.setContentPane(new HomePanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        // Action listener untuk tombol restart/coba lagi
        restartButton.addActionListener(e -> {
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

        // Action listener untuk tombol selanjutnya
        nextButton.addActionListener(e -> {
            Arena nextArena = null;

            if (arena instanceof ArenaA) {
                nextArena = new ArenaB(parentFrame); // Ganti dengan arena selanjutnya
            } else if (arena instanceof ArenaB) {
                nextArena = new ArenaC(parentFrame);
            } else if (arena instanceof ArenaC) {
                nextArena = new ArenaD(parentFrame);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Sudah di arena terakhir!");
            }

            if (nextArena != null) {
                parentFrame.setContentPane(nextArena);
                parentFrame.revalidate();
                parentFrame.repaint();
                nextArena.requestFocusInWindow();
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

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(new Font("Arial", Font.BOLD, 24));

        // Hitung posisi tengah tombol
        int totalWidth = (buttonWidth * 3) + (spacing * 2);
        int startX = (getWidth() - totalWidth) / 2;

        // Tulisan di bawah masing-masing tombol
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        for (int i = 0; i < 3; i++) {
            int x = startX + i * (buttonWidth + spacing) + (buttonWidth / 2);
            int y = centerY + buttonHeight + labelYOffset;

            String text = buttonLabels[i];
            int textWidth = g2d.getFontMetrics().stringWidth(text);

            g2d.drawString(text, x - textWidth / 2, y);
        }

        // ==== Tulisan "Permainan Dijeda" di atas tombol ====
        String title = "PERMAINAN DIJEDA";
        Font titleFont = new Font("Verdana", Font.BOLD, 25);
        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleYFinal = 250; // Lebih rendah dari sebelumnya

        // Stroke hitam tebal (gambar teks hitam di sekeliling teks putih)
        g2d.setColor(Color.BLACK);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    g2d.drawString(title, titleX + dx, titleYFinal + dy);
                }
            }
        }

        // Tulisan utama warna putih
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, titleX, titleYFinal);

        g2d.dispose();
    }

}
