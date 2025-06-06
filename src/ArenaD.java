import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class ArenaD extends Arena {

    public ArenaD(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaD.jpg", 
            "RobberyBob/Assets/collisionArenaD.jpg", 
            37, 525, 
            parentFrame,
            getBarangArenaD()  // harus static method supaya bisa dipanggil di sini
        );
    }
    @Override
    protected void setupCCTVandPenjaga() {
        
    }
    public static List<Item> getBarangArenaD() {
        List<Item> items = new ArrayList<>();
        items.add(new Uang(1007, 290));
        return items;
    }
}

