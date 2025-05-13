import javax.swing.JFrame;

public class ArenaD extends Arena {
    public ArenaD(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaB.jpg", 
            "RobberyBob/Assets/collisionArenaB.jpg", 
            170, 100,  // Posisi awal RobberyBob di ArenaB (ubah sesuai kebutuhan)
            parentFrame
        );
    }
}
