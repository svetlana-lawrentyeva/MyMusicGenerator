package ivko.lana.tibetian;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SoundFileWriter {

    // Метод для записи звука в WAV-файл
    public static void writeSoundToFile(String filename, double[][] notes) {
        int totalDurationMs = 0;
        for (double[] note : notes) {
            totalDurationMs += (int) note[1];
        }

        int totalSamples = (int) ((totalDurationMs / 1000.0) * TibetanBowlSound.SAMPLE_RATE);
        short[] fullSound = new short[totalSamples];
        int offset = 0;

        for (double[] note : notes) {
            double frequency = note[0];
            int durationMs = (int) note[1];

            short[] sound = TibetanBowlSound.createHarmonicSignal(frequency, durationMs);
            System.arraycopy(sound, 0, fullSound, offset, sound.length);
            offset += sound.length;
        }

        try {
            AudioFormat format = new AudioFormat(TibetanBowlSound.SAMPLE_RATE, 16, 2, true, true);
            byte[] finalSound = new byte[fullSound.length * 4];

            for (int i = 0; i < fullSound.length; i++) {
                finalSound[i * 4] = (byte) (fullSound[i] >> 8);
                finalSound[i * 4 + 1] = (byte) fullSound[i];
                finalSound[i * 4 + 2] = (byte) (fullSound[i] >> 8);
                finalSound[i * 4 + 3] = (byte) fullSound[i];
            }

            AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(finalSound), format, fullSound.length);
            File wavFile = new File(filename);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);

            System.out.println("Аудиофайл записан: " + wavFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        double[][] notes = {
                {220.0, 3000}, // Частота и длительность в миллисекундах
                {440.0, 5000},
                {330.0, 2000}
        };

        TibetanBowlSound.setPulsationDepth(0.3f);
        TibetanBowlSound.setPulsationRateFactor(0.2f);
        for (double[] note : notes)
        {
            TibetanBowlSound.play(note[0], (int) note[1]);
        }
//        writeSoundToFile("output.wav", notes);
    }
}
