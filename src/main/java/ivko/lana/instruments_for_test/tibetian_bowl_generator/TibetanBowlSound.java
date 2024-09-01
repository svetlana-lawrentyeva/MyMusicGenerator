package ivko.lana.instruments_for_test.tibetian_bowl_generator;

/**
 * @author Lana Ivko
 */

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.util.Random;

public class TibetanBowlSound
{
    public static float[] generateHitSound(float baseFrequency, float duration, int sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        float[] samples = new float[numSamples];
        float amplitude = 0.5f; // Volume level

        for (int i = 0; i < numSamples; i++) {
            float time = (float) i / sampleRate;
            float decay = (float) Math.exp(-5 * time); // Decay
            samples[i] = amplitude * decay * (float) Math.sin(2 * Math.PI * baseFrequency * time);
        }

        return samples;
    }

    public static float[] generateRubSound(float baseFrequency, float duration, int sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        float[] samples = new float[numSamples];
        float amplitude = 0.5f; // Volume level
        float modulationFrequency = baseFrequency * 2; // Modulation frequency

        for (int i = 0; i < numSamples; i++) {
            float time = (float) i / sampleRate;
            float decay = (float) Math.exp(-2 * time); // Decay
            samples[i] = amplitude * decay * (float) Math.sin(2 * Math.PI * baseFrequency * time) *
                    (1 + 0.5f * (float) Math.sin(2 * Math.PI * modulationFrequency * time));
        }

        return samples;
    }

    public static byte[] floatArrayToByteArray(float[] floatArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(floatArray.length * Float.BYTES);
        for (float f : floatArray) {
            byteBuffer.putFloat(f);
        }
        return byteBuffer.array();
    }

    public static void main(String[] args) throws LineUnavailableException
    {
        float baseFrequency = 440.0f; // Example base frequency (A4)
        float duration = 2.0f; // Duration in seconds
        int sampleRate = 44100; // Sample rate in Hz

        // Generate sound samples
        float[] hitSamples = generateHitSound(baseFrequency, duration, sampleRate);
        float[] rubSamples = generateRubSound(baseFrequency, duration, sampleRate);

        // Convert to byte arrays
        byte[] hitByteArray = floatArrayToByteArray(hitSamples);
        byte[] rubByteArray = floatArrayToByteArray(rubSamples);

        // Конфигурация аудио
        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format, rubByteArray.length);
        line.start();

        // Воспроизведение звука
        line.write(rubByteArray, 0, rubByteArray.length);
        line.drain();
        line.close();
    }
}

