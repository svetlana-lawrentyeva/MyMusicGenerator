package ivko.lana.neurotone.wave_generator.sounds.simple;

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.sounds.ISamplesCreator;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class SimpleSamplesCreator implements ISamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(SimpleSamplesCreator.class.getName());
    private double angle_;

    public SimpleSamplesCreator()
    {
        angle_ = 0.0;
    }

    public short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, boolean isBaseSound)
    {
        int constantSamplesLength = Util.convertMsToSampleLength(durationMs);
        short[] constantWave = createConstantWave(frequency, constantSamplesLength, amplitude);
        // Создаем затухание
        int fadeOutSamplesLength = Util.convertMsToSampleLength(Constants.FADE_OUT_DURATION_MS);
        short[] fadeOutWave = createFadeOutWave(frequency, amplitude, fadeOutSamplesLength, true);

        short[] combinedSignal = new short[constantSamplesLength + fadeOutSamplesLength];

        System.arraycopy(constantWave, 0, combinedSignal, 0, constantSamplesLength);
        System.arraycopy(fadeOutWave, 0, combinedSignal, constantSamplesLength, fadeOutSamplesLength);

        return combinedSignal;
    }

    protected short[] createConstantWave(double frequency, int constantSamplesLength, double amplitude)
    {
        if (constantSamplesLength == 0)
        {
            return new short[0];
        }
        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;
        short[] output = new short[constantSamplesLength];

        for (int i = 0; i < constantSamplesLength; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            short value = (short) (Math.sin(angle_) * amplitude * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    protected short[] createFadeOutWave(double frequency, double amplitude, int fadeOutSamplesLength, boolean isLined)
    {
        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;
        short[] output = new short[fadeOutSamplesLength];

        for (int i = 0; i < fadeOutSamplesLength; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            double decay = !isLined
                    ? (Math.pow(2, (i / (double) fadeOutSamplesLength)) - 1.0) * Math.pow(1.0 - (i / (double) fadeOutSamplesLength), 4)
                    : 1.0 - (i / (double) fadeOutSamplesLength); // Линейное затухание
            short value = (short) (Math.sin(angle_) * amplitude * decay * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    protected short[] combineSamples(short[] base, short[] effect)
    {
        int maxLength = Math.max(base.length, effect.length);
        short[] combined = new short[maxLength];

        int minLength = Math.min(base.length, effect.length);

        // Суммирование элементов с одинаковыми индексами
        for (int i = 0; i < minLength; i++)
        {
            combined[i] = Util.getLimitedValue(base[i] + effect[i]);
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

    protected double incrementAngle(double angleIncrement)
    {
        return (angle_ + angleIncrement) % (2.0 * Math.PI);
    }
}
