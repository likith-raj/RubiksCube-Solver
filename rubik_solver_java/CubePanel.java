import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CubePanel extends JPanel {
    private static final int SIZE = 40; // cubelet size
    private Color[][][] faces = new Color[6][3][3];
    private int skin = 0;

    public void setSkin(int skin) {
        this.skin = skin;
    }

    public CubePanel() {
        setPreferredSize(new Dimension(600, 400)); // small window support
        setBackground(Color.BLACK);
        initializeCube();
    }

    public void setCube(Map<RubiksCube.Face, Color[][]> cubeData) {
        for (RubiksCube.Face face : cubeData.keySet()) {
            faces[face.ordinal()] = cubeData.get(face);
        }
        repaint();
    }

    private void initializeCube() {
        Color[] colors = {
                Color.WHITE, // U
                Color.YELLOW, // D
                Color.RED, // F
                new Color(255, 140, 0), // B (Orange)
                Color.BLUE, // L
                Color.GREEN // R
        };

        for (int f = 0; f < 6; f++) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    faces[f][r][c] = colors[f];
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int baseX = 80; // moved slightly left
        int baseY = getHeight() / 2 - SIZE * 3;

        int[][] offsets = {
                { SIZE * 3, 0 }, // U (top center)
                { SIZE * 3, SIZE * 6 }, // D (bottom center)
                { SIZE * 3, SIZE * 3 }, // F (center)
                { SIZE * 6, SIZE * 3 }, // B (right)
                { 0, SIZE * 3 }, // L (left)
                { SIZE * 9, SIZE * 3 } // R (very right)
        };

        for (int f = 0; f < 6; f++) {
            drawFace(g2, faces[f], baseX + offsets[f][0], baseY + offsets[f][1]);
        }
    }

    private Color parseColor(RubiksCube.Color color) {
        return switch (skin) {
            case 1 -> switch (color) {
                case WHITE -> new Color(255, 255, 255);
                case YELLOW -> new Color(255, 255, 0);
                case RED -> new Color(255, 64, 64);
                case ORANGE -> new Color(255, 140, 0);
                case GREEN -> new Color(0, 255, 128);
                case BLUE -> new Color(0, 128, 255);
            };
            case 2 -> switch (color) {
                case WHITE -> new Color(200, 200, 200);
                case YELLOW -> new Color(189, 183, 107);
                case RED -> new Color(139, 0, 0);
                case ORANGE -> new Color(205, 133, 63);
                case GREEN -> new Color(34, 139, 34);
                case BLUE -> new Color(30, 144, 255);
            };
            case 3 -> switch (color) {
                case WHITE -> new Color(230, 230, 255, 180);
                case YELLOW -> new Color(255, 255, 100, 180);
                case RED -> new Color(255, 100, 100, 180);
                case ORANGE -> new Color(255, 160, 70, 180);
                case GREEN -> new Color(100, 255, 150, 180);
                case BLUE -> new Color(100, 200, 255, 180);
            };
            case 4 -> switch (color) {
                case WHITE -> new Color(200, 200, 200);
                case YELLOW -> new Color(255, 235, 59);
                case RED -> new Color(233, 30, 99);
                case ORANGE -> new Color(255, 87, 34);
                case GREEN -> new Color(76, 175, 80);
                case BLUE -> new Color(33, 150, 243);
            };
            default -> switch (color) {
                case WHITE -> Color.WHITE;
                case YELLOW -> Color.YELLOW;
                case RED -> Color.RED;
                case ORANGE -> new Color(255, 140, 0);
                case GREEN -> Color.GREEN;
                case BLUE -> Color.BLUE;
            };
        };
    }

    private void drawFace(Graphics2D g2, Color[][] face, int x0, int y0) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Color fill = face[r][c];

                // Optional glow effect for visual appeal
                g2.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 180));
                g2.fillRoundRect(x0 + c * SIZE - 1, y0 + r * SIZE - 1, SIZE + 2, SIZE + 2, 10, 10);

                // Actual sticker fill
                g2.setColor(fill);
                g2.fillRoundRect(x0 + c * SIZE, y0 + r * SIZE, SIZE, SIZE, 10, 10);

                // Border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x0 + c * SIZE, y0 + r * SIZE, SIZE, SIZE, 10, 10);
            }
        }
    }
}
