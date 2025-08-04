import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class RubiksCube {
    public enum Face {
        U, D, F, B, L, R
    }

    public enum Color {
        WHITE, YELLOW, RED, ORANGE, GREEN, BLUE
    }

    private Color[][][] cube = new Color[6][3][3];

    public RubiksCube() {
        reset();
    }

    public void reset() {
        Face[] faces = Face.values();
        Color[] colors = Color.values();
        for (int i = 0; i < 6; i++) {
            for (int r = 0; r < 3; r++)
                Arrays.fill(cube[i][r], colors[i]);
        }
    }

    public Color[][][] getCube() {
        return cube;
    }

    public boolean isSolved() {
        for (int i = 0; i < 6; i++) {
            Color expected = cube[i][0][0];
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    if (cube[i][r][c] != expected)
                        return false;
        }
        return true;
    }

    public List<String> scramble(int length) {
        List<String> moves = new ArrayList<>();
        String[] allMoves = { "U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'" };
        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            String move = allMoves[rand.nextInt(allMoves.length)];
            // REMOVE THIS 👇
            // performMove(move);
            moves.add(move);
        }

        return moves;
    }

    // ⬇️ ADD THIS HELPER HERE
    private boolean cubeHasWhiteEdgeOn(String face) {
        int faceIndex = switch (face) {
            case "F" -> 2;
            case "B" -> 3;
            case "L" -> 4;
            case "R" -> 5;
            default -> -1;
        };
        if (faceIndex == -1)
            return false;

        return cube[faceIndex][0][1] == Color.WHITE ||
                cube[faceIndex][1][0] == Color.WHITE ||
                cube[faceIndex][1][2] == Color.WHITE ||
                cube[faceIndex][2][1] == Color.WHITE;
    }

    public void solveCFOP(List<String> scramble, List<String> solutionMoves, Consumer<String> stepCallback) {
        stepCallback.accept("Cross");
        solveCross(solutionMoves);

        stepCallback.accept("F2L");
        solveF2L(solutionMoves);

        stepCallback.accept("OLL");
        solveOLL(solutionMoves);

        stepCallback.accept("PLL");
        solvePLL(solutionMoves);
    }

    public List<String> getInverseScramble(List<String> scrambleMoves) {
        List<String> inverse = new ArrayList<>();
        for (int i = scrambleMoves.size() - 1; i >= 0; i--) {
            String move = scrambleMoves.get(i);
            if (move.endsWith("'"))
                inverse.add(move.substring(0, 1));
            else
                inverse.add(move + "'");
        }
        return inverse;
    }

    public void performMove(String move) {
        switch (move) {
            case "U":
                rotateFace(0);
                rotateSides(0);
                break;
            case "U'":
                rotateFaceCounter(0);
                rotateSidesCounter(0);
                break;
            case "D":
                rotateFace(1);
                rotateSides(1);
                break;
            case "D'":
                rotateFaceCounter(1);
                rotateSidesCounter(1);
                break;
            case "F":
                rotateFace(2);
                rotateSides(2);
                break;
            case "F'":
                rotateFaceCounter(2);
                rotateSidesCounter(2);
                break;
            case "B":
                rotateFace(3);
                rotateSides(3);
                break;
            case "B'":
                rotateFaceCounter(3);
                rotateSidesCounter(3);
                break;
            case "L":
                rotateFace(4);
                rotateSides(4);
                break;
            case "L'":
                rotateFaceCounter(4);
                rotateSidesCounter(4);
                break;
            case "R":
                rotateFace(5);
                rotateSides(5);
                break;
            case "R'":
                rotateFaceCounter(5);
                rotateSidesCounter(5);
                break;
        }
    }

    private void rotateFace(int face) {
        Color[][] f = cube[face];
        Color temp = f[0][0];
        f[0][0] = f[2][0];
        f[2][0] = f[2][2];
        f[2][2] = f[0][2];
        f[0][2] = temp;

        temp = f[0][1];
        f[0][1] = f[1][0];
        f[1][0] = f[2][1];
        f[2][1] = f[1][2];
        f[1][2] = temp;
    }

    private void rotateFaceCounter(int face) {
        rotateFace(face);
        rotateFace(face);
        rotateFace(face);
    }

    private void rotateSides(int face) {
        Color[] temp = new Color[3];
        switch (face) {
            case 0: // U
                for (int i = 0; i < 3; i++)
                    temp[i] = cube[2][0][i];
                for (int i = 0; i < 3; i++) {
                    cube[2][0][i] = cube[5][0][i];
                    cube[5][0][i] = cube[3][0][i];
                    cube[3][0][i] = cube[4][0][i];
                    cube[4][0][i] = temp[i];
                }
                break;
            case 1: // D
                for (int i = 0; i < 3; i++)
                    temp[i] = cube[2][2][i];
                for (int i = 0; i < 3; i++) {
                    cube[2][2][i] = cube[4][2][i];
                    cube[4][2][i] = cube[3][2][i];
                    cube[3][2][i] = cube[5][2][i];
                    cube[5][2][i] = temp[i];
                }
                break;
            case 2: // F
                for (int i = 0; i < 3; i++)
                    temp[i] = cube[0][2][i];
                for (int i = 0; i < 3; i++) {
                    cube[0][2][i] = cube[4][2 - i][2];
                    cube[4][2 - i][2] = cube[1][0][2 - i];
                    cube[1][0][2 - i] = cube[5][i][0];
                    cube[5][i][0] = temp[i];
                }
                break;
            case 3: // B
                for (int i = 0; i < 3; i++)
                    temp[i] = cube[0][0][i];
                for (int i = 0; i < 3; i++) {
                    cube[0][0][i] = cube[5][i][2];
                    cube[5][i][2] = cube[1][2][2 - i];
                    cube[1][2][2 - i] = cube[4][2 - i][0];
                    cube[4][2 - i][0] = temp[i];
                }
                break;
            case 4: // L
                for (int i = 0; i < 3; i++)
                    temp[i] = cube[0][i][0];
                for (int i = 0; i < 3; i++) {
                    cube[0][i][0] = cube[3][2 - i][2];
                    cube[3][2 - i][2] = cube[1][i][0];
                    cube[1][i][0] = cube[2][i][0];
                    cube[2][i][0] = temp[i];
                }
                break;
            case 5: // R
                for (int i = 0; i < 3; i++)
                    temp[i] = cube[0][i][2];
                for (int i = 0; i < 3; i++) {
                    cube[0][i][2] = cube[2][i][2];
                    cube[2][i][2] = cube[1][i][2];
                    cube[1][i][2] = cube[3][2 - i][0];
                    cube[3][2 - i][0] = temp[i];
                }
                break;
        }
    }

    private void rotateSidesCounter(int face) {
        rotateSides(face);
        rotateSides(face);
        rotateSides(face);
    }

    public Map<Face, Color[][]> getFaces() {
        Map<Face, Color[][]> faces = new EnumMap<>(Face.class);
        for (Face face : Face.values()) {
            Color[][] copy = new Color[3][3];
            for (int r = 0; r < 3; r++)
                System.arraycopy(cube[face.ordinal()][r], 0, copy[r], 0, 3);
            faces.put(face, copy);
        }
        return faces;
    }

    public void saveToFile(String filename) {
        try (PrintWriter out = new PrintWriter(filename)) {
            for (Face face : Face.values()) {
                Color[][] faceColors = cube[face.ordinal()];

                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        out.println(face + " " + i + " " + j + " " + faceColors[i][j]);
            }
        } catch (IOException e) {
            System.err.println("Error saving cube: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ");
                Face face = Face.valueOf(parts[0]);
                int i = Integer.parseInt(parts[1]);
                int j = Integer.parseInt(parts[2]);
                Color color = Color.valueOf(parts[3]);
                cube[face.ordinal()][i][j] = color;

            }
        } catch (IOException e) {
            System.err.println("Error loading cube: " + e.getMessage());
        }
    }

    // --- BEGIN CFOP STEP STUBS ---
    private void solveCross(List<String> moves) {
        String[] edges = { "F", "R", "B", "L" };
        for (String face : edges) {
            for (int i = 0; i < 4; i++) {
                if (cubeHasWhiteEdgeOn(face)) {
                    // Bring it down (basic version, one move each)
                    performMove(face);
                    moves.add(face);

                    performMove("D");
                    moves.add("D");

                    break;
                } else {
                    performMove("U");
                    moves.add("U");
                }
            }
        }

        // Now rotate D layer to align with matching centers
        for (int i = 0; i < 4; i++) {
            performMove("D");
            moves.add("D");
        }
    }

    private void solveF2L(List<String> moves) {
        String[][] f2lPairs = {
                { "WHITE", "RED", "BLUE" },
                { "WHITE", "BLUE", "ORANGE" },
                { "WHITE", "ORANGE", "GREEN" },
                { "WHITE", "GREEN", "RED" }
        };

        for (String[] pair : f2lPairs) {
            solveOneF2LPair(pair[1], pair[2], moves);
        }
    }

    private void solveOneF2LPair(String color1, String color2, List<String> moves) {
        // Very simplified simulation: assume we know where the pair is
        moves.add("U");
        performMove("U");
        moves.add("R");
        performMove("R");
        moves.add("U'");
        performMove("U'");
        moves.add("R'");
        performMove("R'");
        moves.add("U'");
        performMove("U'");
        moves.add("F'");
        performMove("F'");
        moves.add("U");
        performMove("U");
        moves.add("F");
        performMove("F");
    }

    private void solveOLL(List<String> moves) {
        // Step 1: Detect OLL Case
        int yellowEdges = 0;
        if (cube[0][0][1] == Color.YELLOW)
            yellowEdges++;
        if (cube[0][1][0] == Color.YELLOW)
            yellowEdges++;
        if (cube[0][1][2] == Color.YELLOW)
            yellowEdges++;
        if (cube[0][2][1] == Color.YELLOW)
            yellowEdges++;

        // Step 2: Apply appropriate algorithm based on edge count
        if (yellowEdges == 0) {
            // Dot case: apply twice
            List<String> dotCase = List.of("F", "R", "U", "R'", "U'", "F'");
            for (int i = 0; i < 2; i++) {
                for (String move : dotCase) {
                    performMove(move);
                    moves.add(move);
                }
            }
        } else if (yellowEdges == 2) {
            // Could be L or line
            boolean isLine = cube[0][1][0] == Color.YELLOW && cube[0][1][2] == Color.YELLOW;
            if (isLine) {
                List<String> line = List.of("F", "R", "U", "R'", "U'", "F'");
                for (String move : line) {
                    performMove(move);
                    moves.add(move);
                }
            } else {
                // L-shape
                List<String> lShape = List.of("F", "U", "R", "U'", "R'", "F'");
                for (String move : lShape) {
                    performMove(move);
                    moves.add(move);
                }
            }
        } else if (yellowEdges == 4) {
            // Already have yellow cross, do nothing
            return;
        }
    }

    private void solvePLL(List<String> moves) {
        // Step 1: Detect if corners are already solved (simplified)
        boolean cornersSolved = true;
        Color c00 = cube[0][0][0]; // U face corner
        for (int i = 0; i < 4; i++) {
            if (cube[0][0][0] != c00) {
                cornersSolved = false;
                break;
            }
        }

        // Step 2: Apply a PLL pattern based on simplified assumption
        if (cornersSolved) {
            // Ua Perm: one edge cycle clockwise
            List<String> ua = List.of("R", "U'", "R", "U", "R", "U", "R", "U'", "R'", "U'", "R2");
            for (String move : ua) {
                performMove(move);
                moves.add(move);
            }
        } else {
            // Simple T Perm (default): swaps two corners and edges
            List<String> tPerm = List.of("R", "U", "R'", "U'", "R'", "F", "R2", "U'", "R'", "U'", "R", "U", "R'", "F'");
            for (String move : tPerm) {
                performMove(move);
                moves.add(move);

            }
        }
    }

    public Map<Face, java.awt.Color[][]> getFacesAsAWT() {
        Map<Face, java.awt.Color[][]> map = new EnumMap<>(Face.class);
        for (Face face : Face.values()) {
            java.awt.Color[][] converted = new java.awt.Color[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    converted[i][j] = switch (cube[face.ordinal()][i][j]) {
                        case WHITE -> java.awt.Color.WHITE;
                        case YELLOW -> java.awt.Color.YELLOW;
                        case RED -> java.awt.Color.RED;
                        case ORANGE -> new java.awt.Color(255, 140, 0);
                        case GREEN -> java.awt.Color.GREEN;
                        case BLUE -> java.awt.Color.BLUE;
                    };
                }
            }
            map.put(face, converted);
        }
        return map;
    }

    private java.awt.Color convertToAWTColor(Color custom) {
        return switch (custom) {
            case WHITE -> java.awt.Color.WHITE;
            case YELLOW -> java.awt.Color.YELLOW;
            case RED -> java.awt.Color.RED;
            case ORANGE -> new java.awt.Color(255, 140, 0); // custom orange
            case GREEN -> java.awt.Color.GREEN;
            case BLUE -> java.awt.Color.BLUE;
        };
    }

    public void applyMoves(List<String> moves) {
        for (String move : moves) {
            performMove(move);
        }
    }

}
