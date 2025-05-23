import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

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
    public static List<Item> getBarangArenaD() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(500, 300));
        return items;
    }
}

