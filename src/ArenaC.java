import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class ArenaC extends Arena {

    public ArenaC(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaC.jpg", 
            "RobberyBob/Assets/collisionArenaC.jpg", 
            450, 500, 
            parentFrame,
            getBarangArenaC()  // harus static method supaya bisa dipanggil di sini
        );
    }
    public static List<Item> getBarangArenaC() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(500, 300));
        return items;
    }
}

