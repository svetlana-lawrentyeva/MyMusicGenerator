package ivko.lana.instruments_for_test;

/**
 * @author Lana Ivko
 */

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavFileAnalyzer
{
    private static final String DIRECTORY = "/home/therioyo/IdeaProjects/MyMusicGenerator/genearated/";
    public static void main(String[] args)
    {
        String wavFilePath1 = DIRECTORY + "29052024-160144.wav";
        String wavFilePath2 = DIRECTORY + "29052024-160242.wav";

        try {
            analyzeWavFile(wavFilePath1);
            analyzeWavFile(wavFilePath2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void analyzeWavFile(String wavFilePath) throws UnsupportedAudioFileException, IOException {
        File wavFile = new File(wavFilePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();

        System.out.println("Analyzing file: " + wavFilePath);
        System.out.println("Sample Rate: " + format.getSampleRate());
        System.out.println("Sample Size in Bits: " + format.getSampleSizeInBits());
        System.out.println("Channels: " + format.getChannels());
        System.out.println("Frame Size: " + format.getFrameSize());
        System.out.println("Frame Rate: " + format.getFrameRate());
        System.out.println("Duration (seconds): " + durationInSeconds);

        byte[] audioBytes = readAllBytes(audioInputStream);
        double[] audioData = convertToDoubleArray(audioBytes, format);

        // Simple analysis: find if there's a significant amount of silence or abnormal spikes (wind noise)
        double threshold = 0.02; // Adjust this threshold based on your audio data characteristics
        int countSilent = 0;
        int countWindNoise = 0;

        for (double sample : audioData) {
            if (Math.abs(sample) < threshold) {
                countSilent++;
            } else if (Math.abs(sample) > 0.8) { // Adjust this threshold based on your audio data characteristics
                countWindNoise++;
            }
        }

        double silenceRatio = (double) countSilent / audioData.length;
        double windNoiseRatio = (double) countWindNoise / audioData.length;

        System.out.println("Silence ratio: " + silenceRatio);
        System.out.println("Wind noise ratio: " + windNoiseRatio);

        if (windNoiseRatio > 0.1) {
            System.out.println("Warning: Significant amount of wind noise detected in " + wavFilePath);
        }
    }

    private static byte[] readAllBytes(AudioInputStream audioInputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = audioInputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    private static double[] convertToDoubleArray(byte[] audioBytes, AudioFormat format) {
        int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
        int numSamples = audioBytes.length / sampleSizeInBytes;
        double[] audioData = new double[numSamples];

        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < numSamples; i++) {
            if (sampleSizeInBytes == 2) {
                audioData[i] = byteBuffer.getShort() / 32768.0;
            } else if (sampleSizeInBytes == 1) {
                audioData[i] = byteBuffer.get() / 128.0;
            }
        }

        return audioData;
    }
}