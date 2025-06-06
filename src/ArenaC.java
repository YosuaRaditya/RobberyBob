import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class ArenaC extends Arena {
    public ArenaC(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaC.jpg", 
            "RobberyBob/Assets/collisionArenaC.jpg", 
            500, 575, 
            parentFrame,
            getBarangArenaC()  // harus static method supaya bisa dipanggil di sini
        );
    }
    @Override
    protected void setupCCTVandPenjaga() {
        
    }
    public static List<Item> getBarangArenaC() {
        List<Item> items = new ArrayList<>();
        items.add(new Uang(763, 45));
        return items;
    }
}

