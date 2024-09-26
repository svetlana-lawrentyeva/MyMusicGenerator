package ivko.lana.neurotone.util;

import ivko.lana.neurotone.processing.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class Util
{
    private static final Logger logger = CustomLogger.getLogger(Util.class.getName());

    public static short[] combineSamples(short[] base, short[] effect)
    {
        int maxLength = Math.max(base.length, effect.length);
        short[] combined = new short[maxLength];

        int minLength = Math.min(base.length, effect.length);

        // Временный массив для нормализации
        double[] tempSamples = new double[maxLength];

        // Суммирование элементов с одинаковыми индексами
        for (int i = 0; i < minLength; i++)
        {
            // Преобразование в диапазон [-1, 1] перед суммированием
            double sum = base[i] / 32767.0 + Constants.UnitizationDividerFactor_ * (effect[i] / 32767.0);
            tempSamples[i] = sum;
        }

        // Копирование оставшихся элементов из большего массива
        if (base.length > minLength)
        {
            System.arraycopy(base, minLength, combined, minLength, base.length - minLength);
        }
        else if (effect.length > minLength)
        {
            System.arraycopy(effect, minLength, combined, minLength, effect.length - minLength);
        }

        // Нормализация итогового массива
        double maxAmplitude = 0.0;
        for (int i = 0; i < minLength; i++)
        {
            if (Math.abs(tempSamples[i]) > maxAmplitude)
            {
                maxAmplitude = Math.abs(tempSamples[i]);
            }
        }

        // Если требуется нормализация
        if (maxAmplitude > 1.0)
        {
            for (int i = 0; i < minLength; i++)
            {
                tempSamples[i] /= maxAmplitude;  // Нормализуем значения
            }
        }

        // Преобразование нормализованных значений обратно в short
        for (int i = 0; i < minLength; i++)
        {
            combined[i] = getLimitedValue((int) (tempSamples[i] * 32767));  // Масштабируем обратно в short
        }

        return combined;
    }

    public static void writeShortArrayToFile(String fileName, short[] array)
    {
        deleteFileIfExists(fileName);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName)))
        {
            for (short s : array)
            {
                writer.print(s + " ");  // Запись каждого элемента массива через пробел
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFileIfExists(String fileName)
    {
        File file = new File(fileName);
        if (file.exists())
        {
            if (file.delete())
            {
                System.out.println("Существующий файл был удален: " + fileName);
            }
            else
            {
                System.out.println("Не удалось удалить файл: " + fileName);
            }
        }
    }

    public static void logSamplesDetails(short[] samples, String invoker)
    {

        int zeroCounter = 0;
        for (int i = 0; i < samples.length; ++i)
        {
            if (samples[i] == 0)
            {
                zeroCounter++;
            }
            else
            {
                break;
            }
        }
        logger.info(String.format("%s: First silence = %s samoles; %s seconds", invoker, zeroCounter, zeroCounter / Constants.SAMPLE_RATE));
//        logger.info(String.format("%s: Total = %s samoles; %s seconds", invoker, samples.length, samples.length / Constants.SAMPLE_RATE));
    }

    public static int convertMsToSampleLength(long ms)
    {
        return (int) ((ms / 1000.0) * Constants.SAMPLE_RATE);
    }

    public static long convertSampleLengthToMs(int sampleLength)
    {
        return (long) ((sampleLength / (double) Constants.SAMPLE_RATE) * 1000);
    }


    public static <T> List<T> concatLists(List<T> a, List<T> b)
    {
        return Stream.concat(a.stream(), b.stream())
                .collect(Collectors.toList());
    }

    public static short getLimitedValue(int originalValue)
    {
        return (short) Math.max(Math.min(originalValue, Short.MAX_VALUE), Short.MIN_VALUE);
    }


    public static short[] addPulsation(short[] samples, double basePulsationSpeed, double multiplier, double phaseShift)
    {
        int totalSamples = samples.length;
        short[] channel = new short[totalSamples];

        for (int i = 0; i < totalSamples; i++)
        {
            double panning = createPanning(basePulsationSpeed, multiplier, i, phaseShift);

            // Усиливаем влияние пульсации на переходах
            double enhancedPanning = 0.5 * (1.0 - Math.sin(Math.PI * panning)); // Используем синус для сглаживания

            // Дополнительно усиливаем амплитуду в точках перехода
            double transitionEnhancement = 1.0 + 0.2 * Math.abs(Math.sin(Math.PI * panning)); // Увеличение на 20% в точках перехода

            // Применение сглаженной пульсации с утолщением переходов
            channel[i] = (short) (samples[i] * (1.0 - enhancedPanning) * transitionEnhancement);

            // Ограничение значений
            channel[i] = Util.getLimitedValue(channel[i]);
        }

        return channel;
    }

    private static double createPanning(double basePulsationSpeed, double multiplier, int i, double phaseShift)
    {
        double sineValue = getSineValue(basePulsationSpeed, multiplier, i, phaseShift);

        // Применяем сглаживание только к области перехода
        double smoothingFactor = 0.8; // Чем выше значение, тем более плавный переход
        double smoothTransition = Math.signum(sineValue) * Math.pow(Math.abs(sineValue), smoothingFactor);

        // Применение сглаженной пульсации
        double panning = 1 - 1 * Constants.PulsationDepth_ * smoothTransition;
        return panning;
    }

    private static double getSineValue(double basePulsationSpeed, double multiplier, int i, double phaseShift)
    {
        double time = i / Constants.SAMPLE_RATE; // Текущее время в секундах

        // Скорость перехода для каналов
        double pulsation = Constants.PulsationSpeedFactor_ * (basePulsationSpeed * multiplier) * time * 0.5;

        // Управление пульсацией для канала (панорамирование)
        double shift = (Math.PI / 2) / multiplier;

        // Добавляем сдвиг по фазе
        double sineValue = Math.sin(Math.PI * pulsation - shift + phaseShift);
        return sineValue;
    }

    public static short[] applyFadeInOut(short[] samples, double fadeInDurationMs, double fadeOutDurationMs, double silenceDurationMs, double phaseShiftMs)
    {
        int totalSamples = samples.length;
        short[] output = new short[totalSamples];

        double sampleRate = Constants.SAMPLE_RATE;

        // Переводим миллисекунды в секунды
        double fadeInDuration = fadeInDurationMs / 1000.0;
        double fadeOutDuration = fadeOutDurationMs / 1000.0;
        double silenceDuration = silenceDurationMs / 1000.0;
        double phaseShift = phaseShiftMs / 1000.0;

        // Рассчитываем количество сэмплов для каждого периода
        int fadeInSamples = (int) (fadeInDuration * sampleRate);
        int fadeOutSamples = (int) (fadeOutDuration * sampleRate);
        int silenceSamples = (int) (silenceDuration * sampleRate);
        int phaseShiftSamples = (int) (phaseShift * sampleRate);  // Сдвиг по фазе в сэмплах

        for (int i = 0; i < totalSamples; i++)
        {
            // Добавляем сдвиг по фазе к текущему индексу
            int shiftedPosition = (i + phaseShiftSamples) % (fadeInSamples + fadeOutSamples + silenceSamples);

            if (shiftedPosition < fadeInSamples)
            {
                // Увеличиваем амплитуду (плавное появление)
                double fadeInFactor = (double) shiftedPosition / fadeInSamples;
                output[i] = (short) (samples[i] * fadeInFactor);
            }
            else if (shiftedPosition < fadeInSamples + fadeOutSamples)
            {
                // Уменьшаем амплитуду (плавное затухание)
                int fadeOutPosition = shiftedPosition - fadeInSamples;
                double fadeOutFactor = 1.0 - (double) fadeOutPosition / fadeOutSamples;
                output[i] = (short) (samples[i] * fadeOutFactor);
            }
            else
            {
                // Пауза (тишина)
                output[i] = 0;
            }

            // Ограничение значений для предотвращения перегрузки
            output[i] = Util.getLimitedValue(output[i]);
        }

        return output;
    }

    public static short[] changeLeftRightBalance(short[] samples, double leftVolume)
    {
        for (int i = 0; i < samples.length; i += 2)
        {
            samples[i] = (short) (samples[i] * leftVolume);  // Применяем коэффициент громкости для левого уха
            samples[i + 1] = (short) (samples[i + 1] * (1 - leftVolume));  // Применяем коэффициент громкости для правого уха
        }
        return samples;
    }
}
