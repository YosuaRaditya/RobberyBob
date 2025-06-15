import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ArenaB extends Arena {

    public ArenaB(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaB.jpg", 
            "RobberyBob/Assets/collisionArenaB.jpg", 
            170, 100, 
            parentFrame,
            getBarangArenaB()  // harus static method supaya bisa dipanggil di sini
        );
    }

    @Override
    protected void setupCCTVandPenjaga() {
        int[][] patrol = { {730, 300}, {730, 100}};
        Upin upin = new Upin(730, 300, 90, 90, "RobberyBob/Assets/Upin.png", patrol);
        upin.setTargetBob(bob); // <-- ini penting!

        // Tambahkan di sini
        upin.setOnBobCaught(() -> {
            stopTimer();
            SwingUtilities.invokeLater(() -> {
                PoliceInteractionPanel policePanel = new PoliceInteractionPanel(parentFrame, this);
                policePanel.setBounds(0, 0, getWidth(), getHeight());
                parentFrame.setContentPane(policePanel);
                parentFrame.revalidate();
                parentFrame.repaint();
            });
        });

        penjagaList.add(upin);

        // TUNDA setCollisionMap sampai panel sudah siap
        SwingUtilities.invokeLater(() -> {
            upin.setCollisionMap(collisionMap, getWidth(), getHeight());
        });
    }

    public static List<Item> getBarangArenaB() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(500, 300));
        return items;
    }
}

