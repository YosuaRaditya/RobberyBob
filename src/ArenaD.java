import javax.swing.JFrame;

public class ArenaD extends Arena {
    public ArenaD(JFrame parentFrame) {
        super(
            "Assets/mapArenaD.jpg", 
            "Assets/collisionArenaD.jpg", 
            50, 500,  // Posisi awal RobberyBob di ArenaB (ubah sesuai kebutuhan)
            parentFrame
        );
    }
}
