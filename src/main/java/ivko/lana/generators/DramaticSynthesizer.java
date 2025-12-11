package ivko.lana.generators;

/**
 * @author Lana Ivko
 */
import javax.sound.sampled.*;

public class DramaticSynthesizer {

    public static void main(String[] args) throws LineUnavailableException {
        double frequency = 660;

        // Частота дискретизации (например, стандартная для аудио 44.1 кГц)
        float sampleRate = 44100;

        // Длительность в секундах
        int durationInSeconds = 5;  // Установим длительность 5 секунд для теста

        // Амплитуда звука (уменьшена для чистого звучания)
        double amplitude = 0.1;  // 10% от максимальной амплитуды для минимизации клиппинга

        // Количество сэмплов
        int numSamples = durationInSeconds * (int) sampleRate;

        // Создаем массив для хранения звуковых сэмплов
        byte[] buffer = new byte[numSamples * 2]; // 2 байта на каждый сэмпл (16-битный звук)

        // Генерируем чистую синусоидальную волну
        for (int i = 0; i < numSamples; i++) {
            double time = i / sampleRate; // Время для текущего сэмпла

            // Чистая синусоидальная волна
            double sineWave = Math.sin(2 * Math.PI * frequency * time);

            // Преобразуем сигнал в 16-битный сэмпл
            short sample = (short) (sineWave * amplitude * Short.MAX_VALUE);

            // Преобразуем в два байта (младший и старший байт)
            buffer[2 * i] = (byte) (sample & 0xFF);  // Младший байт
            buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);  // Старший байт
        }

        // Настройка аудиолинии
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format, buffer.length);
        line.start();

        // Выводим сгенерированные сэмплы в аудиопоток
        line.write(buffer, 0, buffer.length);

        // Останавливаем и закрываем линию после воспроизведения
        line.drain();
        line.stop();
        line.close();
    }
}



