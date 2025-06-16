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
        items.add(new Tas(712, 138));
        items.add(new Kunci(1192, 388));
        items.add(new Emas(349, 514));  
        items.add(new Uang(369, 367));
        items.add(new Dompet(641, 433));
        items.add(new Buku(739, 612));
        items.add(new Kalung(42, 418));
        return items;
    }
}

