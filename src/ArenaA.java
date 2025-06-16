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
    protected void setupCCTVandPenjaga() {
        cctvs.add(new CCTV(975, 64, 100, 64, "RobberyBob/Assets/cctv.png"));
        cctvAngles.add(Math.PI / 2);
        cctvRights.add(true);

        int[][] patrol = { {500, 170}, {700, 170}};
        Polisi polisi = new Polisi(500, 170, 70, 70, "RobberyBob/Assets/polisi.png", patrol);
        polisi.setTargetBob(bob); // <-- ini penting!
        penjagaList.add(polisi);
    }

    public static List<Item> getBarangArenaA() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(600, 275));
        items.add(new Kalung(800, 543));
        items.add(new Emas(126, 360));
        items.add(new Uang(310, 275));
        items.add(new Dompet(290, 110));
        return items;
    }
}