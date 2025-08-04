import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class MainPanel extends JPanel {
    private RubiksCube cube = new RubiksCube();
    private CubePanel cubePanel = new CubePanel();

    private JTextArea moveArea = new JTextArea(20, 25);
    private JTextArea moveDisplay = new JTextArea(5, 40);
    private JTextArea infoArea = new JTextArea(20, 25); // 🔹 CFOP Info Area
    private JLabel statusLabel = new JLabel("Status:");

    private JButton scrambleBtn, solveBtn, timerBtn, superBtn;
    private boolean superMode = false;
    private Timer solveTimer;
    private int currentSkin = 0;

    private List<String> lastScramble = new ArrayList<>();
    private List<String> solutionMoves = new ArrayList<>();
    private long solveStartTime;

    public MainPanel() {
        // ✅ Show retro animated heading at top
        SwingUtilities.invokeLater(() -> {
            infoArea.setText(""); // Clear any existing text

            printRetro("""
                    ░█▀▀█ ░█─░█ ░█▀▀▀█ ░█─── ─█▀▀█ ░█▄─░█ ░█▀▀█ ▀▀█▀▀
                    ░█─── ░█─░█ ─▀▀▀▄▄ ░█─── ░█▄▄█ ░█░█░█ ░█─── ─░█──
                    ░█▄▄█ ─▀▄▄▀ ░█▄▄▄█ ░█▄▄█ ░█─░█ ░█──▀█ ░█▄▄█ ─░█──

                      🚀 R U B I K ' S   C U B E   S O L V E R 💡
                    ---------------------------------------------
                    """, 5); // 🔹 Faster typing (5ms per char)
        });

        Font retroFont = new Font("Courier New", Font.BOLD, 14);
        UIManager.put("Button.font", retroFont);
        UIManager.put("Label.font", retroFont);
        UIManager.put("TextArea.font", retroFont);

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        moveArea.setEditable(false);
        moveArea.setBackground(Color.BLACK);
        moveArea.setForeground(Color.GREEN);
        moveArea.setFont(new Font("Courier New", Font.PLAIN, 12));

        moveDisplay.setEditable(false);
        moveDisplay.setBackground(Color.BLACK);
        moveDisplay.setForeground(Color.GREEN);
        moveDisplay.setFont(new Font("Courier New", Font.PLAIN, 13));

        infoArea.setEditable(false);
        infoArea.setBackground(Color.BLACK);
        infoArea.setForeground(Color.GREEN);
        infoArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane infoScroll = new JScrollPane(infoArea);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.BLACK);
        leftPanel.add(infoScroll, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST); // ◀️ CFOP info goes left
        scrambleBtn = new JButton("Scramble");
        JButton skinBtn = new JButton("Change Cube Skin");

        solveBtn = new JButton("Solve");
        timerBtn = new JButton("Solve Time");
        superBtn = new JButton("Cube Supermode ON");
        JButton aboutBtn = new JButton("About"); // added

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.BLACK);

        for (JButton b : List.of(scrambleBtn, solveBtn, timerBtn, superBtn, skinBtn, aboutBtn)) {

            b.setFont(retroFont);
            b.setBackground(Color.BLACK);
            b.setForeground(Color.GREEN);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            rightPanel.add(b);
            rightPanel.add(Box.createVerticalStrut(10));
        }

        statusLabel.setForeground(Color.GREEN);
        statusLabel.setFont(new Font("Courier New", Font.BOLD, 12));

        ImageIcon originalIcon = new ImageIcon("rubik.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        ImageIcon cubeIcon = new ImageIcon(scaledImage);
        JLabel cubeImageLabel = new JLabel(cubeIcon);
        cubeImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(cubeImageLabel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(new JScrollPane(moveDisplay), BorderLayout.CENTER); // ✅ Moves at bottom

        add(cubePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
        scrambleBtn.addActionListener(e -> {
            lastScramble = cube.scramble(25); // Just generate scramble
            moveDisplay.setText("Scramble: ");
            statusLabel.setText("Scrambling...");

            SoundPlayer.loop("scramble.wav"); // 🔁 play sound

            animateMoves(lastScramble, () -> {
                SoundPlayer.stop(); // ✅ stop when done
                statusLabel.setText("Scrambled ✅");
            });
        });

        solveBtn.addActionListener(e -> solveCube());
        timerBtn.addActionListener(e -> showSolveTime());
        superBtn.addActionListener(e -> toggleSuperMode());
        aboutBtn.addActionListener(e -> showAbout()); // <-- add here
        skinBtn.addActionListener(e -> {
            currentSkin = (currentSkin + 1) % 5;
            cubePanel.setSkin(currentSkin); // 🔄 update cube skin
            cubePanel.repaint(); // 🖌 refresh view
            statusLabel.setText("Skin changed: " + getSkinName(currentSkin));
        });

        // KEYBOARD CONTROLS
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String move = switch (e.getKeyCode()) {
                    case KeyEvent.VK_U -> "U";
                    case KeyEvent.VK_D -> "D";
                    case KeyEvent.VK_L -> "L";
                    case KeyEvent.VK_R -> "R";
                    case KeyEvent.VK_F -> "F";
                    case KeyEvent.VK_B -> "B";
                    case KeyEvent.VK_W -> "U'";
                    case KeyEvent.VK_S -> "D'";
                    case KeyEvent.VK_A -> "L'";
                    case KeyEvent.VK_E -> "B'";
                    case KeyEvent.VK_Q -> "F'";
                    case KeyEvent.VK_Z -> "R'";
                    default -> null;
                };
                if (move != null) {
                    cube.performMove(move);

                    cubePanel.repaint();

                    moveArea.append(move + " ");
                }
            }
        });

        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                requestFocusInWindow();
            }

            public void ancestorRemoved(AncestorEvent event) {
            }

            public void ancestorMoved(AncestorEvent event) {
            }
        });

    }

    private void solveCube() {
        if (lastScramble.isEmpty()) {
            statusLabel.setText("Status: Scramble first!");
            return;
        }

        solutionMoves = cube.getInverseScramble(lastScramble);
        solveStartTime = System.currentTimeMillis();
        statusLabel.setText("Solving...");
        moveDisplay.setText("Solve: ");

        solveTimer = new Timer(100, e -> {
            long elapsed = System.currentTimeMillis() - solveStartTime;
            statusLabel.setText(String.format("Solving... %.1fs", elapsed / 1000.0));
        });
        solveTimer.start();

        SoundPlayer.loop("scramble.wav"); // 👈 add here

        animateMoves(solutionMoves, () -> {
            solveTimer.stop();
            SoundPlayer.stop(); // 👈 stop when done
            statusLabel.setText("Solved! ✅");
            SoundPlayer.play("solved.wav"); // 🔊 solved sound

            // ✨ Blinking effect
            new Thread(() -> {
                try {
                    for (int i = 0; i < 6; i++) {
                        statusLabel.setForeground(i % 2 == 0 ? Color.CYAN : Color.WHITE);
                        Thread.sleep(300);
                    }
                    statusLabel.setForeground(Color.GREEN);
                } catch (InterruptedException ignored) {
                }
            }).start();

            // 🎮 Retro typing beside cube
            infoArea.setText(""); // ✅ clear old text before printing new one

            String info = """
                        🎉 Cube Solved Successfully!

                        🔍 Algorithm Used: CFOP (Cross, F2L, OLL, PLL)
                        🧠 Time Complexity: O(1)
                        💾 Space Complexity: O(1)

                        📘 Explanation:
                        CFOP stands for:
                          C - Cross: Create a white cross on one face
                          F - F2L: Solve the first two layers using corner+edge pairs
                          O - OLL: Orient all pieces on the last layer
                          P - PLL: Permute last layer pieces to finish the cube

                        ✅ Efficient and real-time suitable algorithm for animation-based solving.
                        --------------------------------------------------------
                    """;

            printRetro(info, 30); // 30ms per character

        });
    }

    private void printRetro(String text, int delay) {
        new Thread(() -> {
            for (char c : text.toCharArray()) {
                SwingUtilities.invokeLater(() -> {
                    infoArea.append(String.valueOf(c));
                    infoArea.setCaretPosition(infoArea.getDocument().getLength());
                });
                try {
                    Thread.sleep(delay); // Lower = faster
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void animateMoves(List<String> moves, Runnable onComplete) {
        moveDisplay.append("\n");
        StringBuilder moveLine = new StringBuilder();

        new javax.swing.Timer(superMode ? 50 : 250, new ActionListener() {
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < moves.size()) {
                    String move = moves.get(index++);
                    cube.performMove(move);
                    cubePanel.setCube(cube.getFacesAsAWT());

                    cubePanel.repaint();

                    moveLine.append(move).append(" ");
                    moveDisplay.setText(moveDisplay.getText().replaceAll("(Scramble:|Solve:).*", "") +
                            (moveDisplay.getText().contains("Scramble:") ? "Scramble: " : "Solve: ") +
                            moveLine.toString());
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    if (onComplete != null)
                        onComplete.run();
                }
            }
        }).start();
    }

    private void showSolveTime() {
        long start = System.currentTimeMillis();
        cube.solveCFOP(lastScramble, solutionMoves, step -> {
            statusLabel.setText("Step: " + step);
        });
        long end = System.currentTimeMillis();
        long time = end - start;

        JOptionPane.showMessageDialog(this, "Solve Time: " + time + " ms", "Timer", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleSuperMode() {
        superMode = !superMode;
        superBtn.setText(superMode ? "Cube Supermode ON" : "Cube Supermode OFF");
    };

    private void showAbout() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.GREEN);
        textPane.setFont(new Font("Courier New", Font.PLAIN, 13));
        textPane.setText("""
                ╔══════════════════════════════════════════════════╗
                ║               🧊 RUBIK'S CUBE SOLVER             ║
                ║          "Logic. Speed. Precision. Retro."       ║
                ╠══════════════════════════════════════════════════╣
                ║  ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄      ║
                ║  █   █ █▀▄▀█ █▀▀ █▄█ █▀█ █ █ █▄█ ▀█▀ █▀█ █        ║
                ║  █ ▀▄█ █ ▀ █ █▀▀  █  █▀█ █ █  █   █  █▀▀ █        ║
                ║  █▄▄▄█ ▀   ▀ ▀▀▀ ▀▀▀ ▀ ▀ ▀▀▀ ▀▀▀ ▀▀▀ ▀   ▀        ║
                ║                                                  ║
                ║ 👨‍💻 Developer: Sunny                             ║
                ║ 🌐 GitHub: github.com/sunny            ║
                ║ 🎯 Algorithm: CFOP (Cross, F2L, OLL, PLL)        ║
                ║ 🚀 Features:                                     ║
                ║    • Animated solving step-by-step               ║
                ║    • Solve timer + Supermode                     ║
                ║    • Retro move tracker with typing effect       ║
                ║    • Blinking 'Solved' status feedback           ║
                ║    • Manual keyboard control                     ║
                ║                                                  ║
                ║ 🎮 Keyboard Shortcuts:                           ║
                ║    U D L R F B   →  Face Rotations               ║
                ║    W S A Q E Z   →  Counter-clockwise Rotations ║
                ║                                                  ║
                ║ ✅ Tip: Press Supermode for lightning-fast solving║
                ╚══════════════════════════════════════════════════╝
                    """);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "🔍 About Rubik's Cube Solver",
                JOptionPane.PLAIN_MESSAGE);
    }

    private String getSkinName(int skin) {
        return switch (skin) {
            case 1 -> "Neon Glow";
            case 2 -> "Minecraft";
            case 3 -> "Glass Style";
            case 4 -> "Retro VHS";
            default -> "Classic";
        };

    }
    // ✅ Show intro heading animation

}
