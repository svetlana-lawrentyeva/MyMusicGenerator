package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleClearSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleSamplesCreator;

/**
 * @author Lana Ivko
 */
public class TibetanClearSamplesCreator extends SimpleClearSamplesCreator
{
    private double durationFadeInFactor_;
    private double durationFadeOutFactor_;
    TibetanClearSamplesCreator()
    {
        super();
    }

    public void setDurationFadeInFactor(double durationFadeInFactor)
    {
        durationFadeInFactor_ = durationFadeInFactor;
    }

    public void setDurationFadeOutFactor(double durationFadeOutFactor)
    {
        durationFadeOutFactor_ = durationFadeOutFactor;
    }

    public short[] createClearSamples(int durationMs, double frequency, double amplitude, double phaseShift, boolean isLined)
    {
        int pauseLength = Util.convertMsToSampleLength(Constants.PAUSE_DURATION_MS);
        int smallAmplitudesLength = Util.convertMsToSampleLength(Constants.SMALL_AMPLITUDES_DURATION_MS);
        int introSamplesLength = pauseLength + smallAmplitudesLength;
        int fadeInSamplesLength = (int) ((Util.convertMsToSampleLength(Constants.FADE_IN_DURATION_MS) - introSamplesLength) * durationFadeInFactor_);
        int fadeOutSamplesLength = (int) (Util.convertMsToSampleLength(Constants.FadeOutDurationMs_) * durationFadeOutFactor_);

        // Создаем интро
        short[] introWave = getIntro(frequency, amplitude, pauseLength, smallAmplitudesLength);
        // Создаем нарастание
        short[] fadeInWave = createFadeInWave(frequency, amplitude, fadeInSamplesLength);

        int constantSamplesLength = Util.convertMsToSampleLength(durationMs) - introSamplesLength - fadeInSamplesLength;

        if (constantSamplesLength < 0)
        {
            throw new IllegalArgumentException(
                    String.format("Суммарная длительность паузы (%s), малой амплитуды (%s) и нарастающей части (%s) [%s] превышает общую длительность ноты (%s).",
                            pauseLength,
                            smallAmplitudesLength,
                            fadeInSamplesLength,
                            pauseLength + smallAmplitudesLength + fadeInSamplesLength,
                            Util.convertMsToSampleLength(durationMs)
                    ));
        }
        // Создаем основную часть
        short[] constantWave = createConstantWave(frequency, constantSamplesLength, amplitude);
        // Создаем затухание
        short[] fadeOutWave = createFadeOutWave(frequency, amplitude, fadeOutSamplesLength, isLined);

        short[] combinedSignal = new short[introSamplesLength + fadeInSamplesLength + constantSamplesLength + fadeOutSamplesLength];

        System.arraycopy(introWave, 0, combinedSignal, 0, introSamplesLength);
        System.arraycopy(fadeInWave, 0, combinedSignal, introSamplesLength, fadeInSamplesLength);
        System.arraycopy(constantWave, 0, combinedSignal, introSamplesLength + fadeInSamplesLength, constantSamplesLength);
        System.arraycopy(fadeOutWave, 0, combinedSignal, introSamplesLength + fadeInSamplesLength + constantSamplesLength, fadeOutSamplesLength);
        return combinedSignal;
    }

    private short[] getIntro(double frequency, double amplitude, int pauseLength, int smallAmplitudesLength)
    {
        // Пауза перед началом нарастания
        short[] pause = new short[pauseLength];

        // Генерация свип-сигнала с малыми амплитудами
        short[] smallAmplitudeWave = createSmallAmplitudeWave(frequency, amplitude * 0.005, smallAmplitudesLength);

        short[] output = new short[pauseLength + smallAmplitudeWave.length];
        System.arraycopy(pause, 0, output, 0, pauseLength);
        System.arraycopy(smallAmplitudeWave, 0, output, pauseLength, smallAmplitudeWave.length);

        return output;
    }

    // Метод для создания слабых колебаний с малыми амплитудами
    private short[] createSmallAmplitudeWave(double frequency, double amplitude, int length)
    {
        short[] smallWave = new short[length];
        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;

        for (int i = 0; i < length; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            smallWave[i] = (short) (Math.sin(angle_) * amplitude * Short.MAX_VALUE);
        }
        return smallWave;
    }

    public short[] createFadeInWave(double frequency, double amplitude, int fadeInSamplesLength)
    {
        boolean isLined = true;
        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;
        if (fadeInSamplesLength < 0)
        {
            int a = 0;
        }
        short[] output = new short[fadeInSamplesLength];

        for (int i = 0; i < fadeInSamplesLength; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            double fadeFactor = !isLined
                    ? Math.pow(i / (double) fadeInSamplesLength, (double) 1 / 6.0)
                    : i / (double) fadeInSamplesLength; // Линейное затухание
            output[i] = (short) (Math.sin(angle_) * amplitude * fadeFactor * Short.MAX_VALUE);
        }

        return output;
    }

    protected double incrementAngle(double angleIncrement)
    {
        return (angle_ + angleIncrement) % (2.0 * Math.PI);
    }
}
