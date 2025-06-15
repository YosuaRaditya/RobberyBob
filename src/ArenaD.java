import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ArenaD extends Arena {

    public ArenaD(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaD.jpg", 
            "RobberyBob/Assets/collisionArenaD.jpg", 
            50, 500, 
            parentFrame,
            getBarangArenaD()  // harus static method supaya bisa dipanggil di sini
        );
    }

    @Override
    protected void setupCCTVandPenjaga() {
        int[][] patrol = { {500, 300}, {800, 300}};
        Polisi2 polisi2 = new Polisi2(500, 300, 70, 70, "RobberyBob/Assets/polisi2.png", patrol);
        polisi2.setTargetBob(bob); // <-- ini penting!

        // Tambahkan di sini
        polisi2.setOnBobCaught(() -> {
            stopTimer();
            SwingUtilities.invokeLater(() -> {
                PoliceInteractionPanel policePanel = new PoliceInteractionPanel(parentFrame, this);
                policePanel.setBounds(0, 0, getWidth(), getHeight());
                parentFrame.setContentPane(policePanel);
                parentFrame.revalidate();
                parentFrame.repaint();
            });
        });

        penjagaList.add(polisi2);

        // TUNDA setCollisionMap sampai panel sudah siap
        SwingUtilities.invokeLater(() -> {
            polisi2.setCollisionMap(collisionMap, getWidth(), getHeight());
        });
    }

    public static List<Item> getBarangArenaD() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(500, 300));
        return items;
    }
}

