package ivko.lana.instruments_for_test.samples_generators;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Lana Ivko
 */
public class Util
{
    public static void clearPreviousFile(String filePath)
    {
        File file = new File(filePath);
        if (file.exists())
        {
            Path path = Paths.get(file.getAbsolutePath());
            try
            {
                Files.deleteIfExists(path);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveWaveFile(byte[] audioData, int sampleRate, String filePath) {
        try {
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
