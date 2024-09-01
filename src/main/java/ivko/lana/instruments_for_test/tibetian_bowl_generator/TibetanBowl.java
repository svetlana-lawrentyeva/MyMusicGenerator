package ivko.lana.instruments_for_test.tibetian_bowl_generator;

/**
 * @author Lana Ivko
 */
import javax.sound.sampled.*;
import java.util.Arrays;

public class TibetanBowl {
    private static final int SAMPLE_RATE = 44100; // Sample rate in Hz
    private static final double FREQUENCY = 188.0; // Fundamental frequency of the bowl in Hz

    public static void main(String[] args) {
        TibetanBowl bowl = new TibetanBowl();
        bowl.playSound();
    }

    public void playSound() {
        byte[] buffer = new byte[SAMPLE_RATE * 10];
        for (int i = 0; i < buffer.length; i++) {
            // Generate a sine wave for the Tibetan bowl sound
            buffer[i] = (byte) (Math.sin(2 * Math.PI * i / (SAMPLE_RATE / FREQUENCY)) * 127);
        }

        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
