package ivko.lana.neurotone.wave_generator.sounds.simple;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.Util;

/**
 * @author Lana Ivko
 */
public class SimpleClearSamplesCreator
{
    protected double angle_;

    public short[] createClearSamples(int durationMs, double frequency, double amplitude)
    {
        return createClearSamples(durationMs, frequency, amplitude, 0, true);
    }

    public short[] createClearSamples(int durationMs, double frequency, double amplitude, boolean isLined)
    {
        return createClearSamples(durationMs, frequency, amplitude, 0, isLined);
    }

    public short[] createClearSamples(int durationMs, double frequency, double amplitude, double phaseShift)
    {
        return createClearSamples(durationMs, frequency, amplitude, phaseShift, true);
    }

    public short[] createClearSamples(int durationMs, double frequency, double amplitude, double phaseShift, boolean isLined)
    {
        angle_ = phaseShift;
        int constantSamplesLength = Util.convertMsToSampleLength(durationMs);
        short[] constantWave = createConstantWave(frequency, constantSamplesLength, amplitude);
        int fadeOutSamplesLength = Util.convertMsToSampleLength(Constants.FadeOutDurationMs_);
        short[] fadeOutWave = createFadeOutWave(frequency, amplitude, fadeOutSamplesLength, isLined);
        short[] combinedSignal = new short[constantSamplesLength + fadeOutSamplesLength];

        System.arraycopy(constantWave, 0, combinedSignal, 0, constantSamplesLength);
        System.arraycopy(fadeOutWave, 0, combinedSignal, constantSamplesLength, fadeOutSamplesLength);

        return combinedSignal;
    }

    public short[] createConstantWave(double frequency, int constantSamplesLength, double amplitude)
    {
        if (constantSamplesLength == 0)
        {
            return new short[0];
        }
        double angleIncrement = (2.0 * Math.PI * frequency / Constants.SAMPLE_RATE);
        short[] output = new short[constantSamplesLength];

        for (int i = 0; i < constantSamplesLength; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            short value = (short) (Math.sin(angle_) * amplitude * Short.MAX_VALUE);
            output[i] = value;
        }

        return output;
    }

    public short[] createFadeOutWave(double frequency, double amplitude, int fadeOutSamplesLength, boolean isLined)
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

    protected double incrementAngle(double angleIncrement)
    {
        return (angle_ + angleIncrement) % (2.0 * Math.PI);
    }
}
