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
            getBarangArenaA()  // harus static method supaya bisa dipanggil di sini
        );
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

