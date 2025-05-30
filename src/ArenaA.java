import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class ArenaA extends Arena {

    public ArenaA(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaA.jpg", 
            "RobberyBob/Assets/collisionArenaA.jpg", 
            120, 370, 
            parentFrame,
            getBarangArenaA()
        );
    }

    @Override
    protected void setupCCTVandPolisi() {
        // Tambahkan CCTV
        cctvs.add(new CCTV(975, 64, 100, 64, "RobberyBob/Assets/cctv.png"));
        cctvAngles.add(Math.PI / 2);
        cctvRights.add(true);

        // Tambahkan Polisi
        int[][] patrolPoints = {
            {487, 175},
            {875, 175}
        };
        polisis.add(new Polisi(patrolPoints[0][0], patrolPoints[0][1], 100, 64, "RobberyBob/Assets/polisiKanan.png"));
        polisiPatrolPointsList.add(patrolPoints);
        polisiTargetIndices.add(1);
        polisiSpeeds.add(2);
    }

    public static List<Item> getBarangArenaA() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(600, 275));
        items.add(new Kalung(800, 543));
        items.add(new Emas(126, 360));
        items.add(new Uang(310, 275));
        return items;
    }
}