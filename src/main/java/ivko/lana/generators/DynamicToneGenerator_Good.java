package ivko.lana.generators;/**
 * @author Lana Ivko
 */
import javax.sound.sampled.*;
import java.util.Arrays;

public class DynamicToneGenerator_Good
{

    public static void main(String[] args) throws LineUnavailableException {
        // Настройка аудиоформата
        float sampleRate = 44100;
        byte[] buffer = new byte[2];
        AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();

        // Основные частоты для объёмного аккорда

        double baseFrequency = 220;

        double[] baseFrequencies =
                {
                        baseFrequency * 13081 / 22000,
                        baseFrequency * 17461 / 22000,
                        baseFrequency * 22000 / 22000,
                        baseFrequency * 26163 / 22000,
                        baseFrequency * 32963 / 22000,
                        baseFrequency * 39200 / 22000,
                        baseFrequency * 52325 / 22000,
                };
        double[] harmonyFrequencies0 = Arrays.stream(baseFrequencies)
                .boxed()
                .map(frequency -> frequency * 2)
                .mapToDouble(Double::doubleValue)
                .toArray();
        double amplitude = 0.02;  // Низкая громкость для каждого слоя
        double harmonyAmplitude = 0.01;  // Ещё ниже для гармоник, чтобы не перекрывали основные ноты
        double duration = 100;     // Продолжительность воспроизведения (10 секунд)
        double sampleDuration = 1.0 / sampleRate;
        double time = 0;  // Текущее время

        for (int i = 0; i < sampleRate * duration; i++)
        {
            double signal = 0;  // Итоговый сигнал

            // Основные частоты с плавной модуляцией
            for (double baseFreq : baseFrequencies)
            {
                double modulatedFreq = baseFreq + 0.01 * Math.sin(time * 0.1);  // Плавное изменение частоты
                double angle = i / (sampleRate / modulatedFreq) * 2.0 * Math.PI;
                signal += Math.sin(angle) * amplitude;
            }

            // Добавляем гармоники для многослойности
            for (double harmonyFreq : harmonyFrequencies0)
            {
                double modulatedHarmonyFreq = harmonyFreq + 0.01 * Math.sin(time * 0.15);  // Очень мягкая модуляция гармоник
                double harmonyAngle = i / (sampleRate / modulatedHarmonyFreq) * 2.0 * Math.PI;
                signal += Math.sin(harmonyAngle) * harmonyAmplitude;  // Более низкая амплитуда для гармоник
            }


            // Преобразуем сигнал в байты для вывода
            short a = (short) (signal * 32767);
            buffer[0] = (byte) (a & 0xFF);
            buffer[1] = (byte) (a >> 8);
            sdl.write(buffer, 0, 2);

            // Обновляем текущее время
            time += sampleDuration;
        }

        sdl.drain();
        sdl.stop();
    }
}

