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

        // Суммирование элементов с одинаковыми индексами
        for (int i = 0; i < minLength; i++)
        {
            combined[i] = (short) (Constants.UnitizationDividerFactor_ * Util.getLimitedValue((int) (base[i] + Constants.UnitizationDividerFactor_  * effect[i])));
//            combined[i] = Util.getLimitedValue((int) (Constants.UnitizationDividerFactor_ * base[i] + Constants.UnitizationDividerFactor_  * effect[i]));
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

    public static void logSamplesDetails(short[]samples, String invoker)
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

    public static int convertMsToSampleLength(int ms)
    {
        return (int) ((ms / 1000.0) * Constants.SAMPLE_RATE);
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
}
