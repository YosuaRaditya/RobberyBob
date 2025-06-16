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
    public static List<Item> getBarangArenaB() {
        List<Item> items = new ArrayList<>();
        items.add(new Kunci(946, 394));
        items.add(new Tas(390,270));
        items.add(new Lampu(864, 268));
        items.add(new Handphone(466,478));
        items.add(new Buku(749, 78));
        return items;
    }
}

