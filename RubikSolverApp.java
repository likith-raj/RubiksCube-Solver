import javax.swing.*;

public class RubikSolverApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🧩 Rubik's Cube Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 650);
            frame.setResizable(false);
            frame.add(new MainPanel());
            frame.setVisible(true);
        });
    }
}
