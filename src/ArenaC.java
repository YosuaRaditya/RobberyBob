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

    public static List<Item> getBarangArenaC() {
        List<Item> items = new ArrayList<>();
        items.add(new Laptop(280, 85));
        items.add(new Vas(766, 438));
        items.add(new Kalung(1078, 492));
        items.add(new Uang(197, 490));
        items.add(new Handphone(493, 166));
        items.add(new Buku(900, 110));
        return items;
    }
}

