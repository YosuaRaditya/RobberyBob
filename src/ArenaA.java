import javax.swing.JFrame;

public class ArenaA extends Arena {
    public ArenaA(JFrame parentFrame) {
        super(
            "RobberyBob/Assets/mapArenaA.jpg", 
            "RobberyBob/Assets/collisionArenaA.jpg", 
            120, 370, // Posisi awal RobberyBob di ArenaA
            parentFrame
        );
    }
}
