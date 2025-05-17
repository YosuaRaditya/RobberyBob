import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;

public class ArenaA extends Arena {

    public ArenaA(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaA.jpg", 
            "RobberyBob/Assets/collisionArenaA.jpg", 
            120, 370, 
            parentFrame,
            getBarangArenaA()  // harus static method supaya bisa dipanggil di sini
        );
    }

    public static List<Item> getBarangArenaA() {
        List<Item> items = new ArrayList<>();
        items.add(new Tas(500, 300));
        return items;
    }
}

