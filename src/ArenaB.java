import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Item> getBarangArenaB() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(500, 300));
        return items;
    }
}

