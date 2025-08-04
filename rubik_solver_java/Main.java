import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🧩 RUBIK'S CUBE");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(720, 600);
            frame.setLocationRelativeTo(null); // center on screen
            frame.setContentPane(new MainPanel());

            // Set custom icon
            ImageIcon icon = new ImageIcon("rubik_icon.png");
            frame.setIconImage(icon.getImage());

            frame.setVisible(true);
        });
    }
}
