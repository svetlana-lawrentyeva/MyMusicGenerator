package ivko.lana.instruments_for_test.tibetian_bowl_generator;

/**
 * @author Lana Ivko
 */
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class TibetanBowlSoundGenerator {

    public static void main(String[] args) {
        // Частоты и амплитуды, основанные на квадратах
        double[] frequencies = {61.79069017422413, 247.16276069689653, 556.1162115680172, 988.6510427875861, 1544.7672543556032};
        double[] amplitudes = {1.0, 0.8, 0.6, 0.4, 0.2}; // Пример амплитуд для более естественного звука
        int sampleRate = 44100; // Частота дискретизации
        double duration = 3.0; // Длительность в секундах

        byte[] audioData = generateSound(frequencies, amplitudes, sampleRate, duration);

        try {
            // Создаем аудиофайл
            File outputFile = new File("tibetan_bowl_sound_improved.wav");
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
            System.out.println("Файл успешно создан: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] generateSound(double[] frequencies, double[] amplitudes, int sampleRate, double duration) {
        int numSamples = (int) (duration * sampleRate);
        byte[] data = new byte[2 * numSamples]; // 16-битное аудио (2 байта на выборку)

        for (int i = 0; i < numSamples; i++) {
            double time = i / (double) sampleRate;
            double sampleValue = 0.0;

            // Суммируем синусоиды для каждой частоты и амплитуды
            for (int j = 0; j < frequencies.length; j++) {
                sampleValue += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * time);
            }

            // Нормализуем амплитуду для предотвращения клиппинга
            sampleValue = Math.max(-1.0, Math.min(1.0, sampleValue));

            // Конвертируем в 16-битное значение
            short sampleShort = (short) (sampleValue * Short.MAX_VALUE);
            data[2 * i] = (byte) (sampleShort & 0xff);
            data[2 * i + 1] = (byte) ((sampleShort >> 8) & 0xff);
        }

        return data;
    }
}

