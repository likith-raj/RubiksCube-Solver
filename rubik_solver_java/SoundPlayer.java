import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    private static Clip loopingClip;

    public static void play(String filename) {
        stop(); // Stop previous sound if any
        try {
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("Sound file not found: " + filename);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public static void loop(String filename) {
        stop(); // Stop any previous
        try {
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("Sound file not found: " + filename);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            loopingClip = AudioSystem.getClip();
            loopingClip.open(audioIn);
            loopingClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error looping sound: " + e.getMessage());
        }
    }

    public static void stop() {
        if (loopingClip != null && loopingClip.isRunning()) {
            loopingClip.stop();
            loopingClip.close();
        }
    }
}
