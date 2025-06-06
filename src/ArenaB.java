import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

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
        
    }
    public static List<Item> getBarangArenaB() {
        List<Item> items = new ArrayList<>();
        items.add(new Uang(890, 629));
        return items;
    }
}

