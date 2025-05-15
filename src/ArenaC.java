import javax.swing.JFrame;

public class ArenaC extends Arena {
    public ArenaC(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaC.jpg", 
            "RobberyBob/Assets/collisionArenaC.jpg", 
            450, 500,  // Posisi awal RobberyBob di ArenaB (ubah sesuai kebutuhan)
            parentFrame
        );
    }
}
