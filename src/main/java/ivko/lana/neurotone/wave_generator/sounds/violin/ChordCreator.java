package ivko.lana.neurotone.wave_generator.sounds.violin;

import ivko.lana.generators.Bowed;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.Util;
import ivko.lana.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Lana Ivko
 */
public class ChordCreator
{

    private static final double LEFT_MULTIPLIER = 1;
    private static final double RIGHT_MULTIPLIER = -1;
    private static final Map<Chord, Consumer<Chord>> chordConsumerMap_ = new HashMap<>();
    private final ivko.lana.generators.Bowed bowedInstrument_;
    private final double frequency_;
    private final double amplitude_;
    private final boolean isLeft_;


    ChordCreator(ivko.lana.generators.Bowed bowedInstrument, double frequency, double amplitude, boolean isLeft)
    {
        bowedInstrument_ = bowedInstrument;
        frequency_ = frequency;
        amplitude_ = amplitude;
        isLeft_ = isLeft;
    }

    public short[] createSamples(Chord chord, int durationMs, double phaseMultiplier, int overtoneIndex)
    {
        Pair<Integer, Integer>[] changeFactors = chord.getChangeFactors();
        short[] fullSamples = null;
        for (Pair<Integer, Integer> changeFactor : changeFactors)
        {
            Integer multiplier = changeFactor.getFirst();
            Integer divider = changeFactor.getSecond();
            short[] samples = getSimpleBody(durationMs, multiplier * frequency_ / divider, amplitude_, bowedInstrument_, isLeft_, phaseMultiplier, overtoneIndex);
            if (fullSamples == null)
            {
                fullSamples = samples;
            }
            else
            {
                Util.combineSamples(fullSamples, samples);
            }
        }
        return fullSamples;
    }

    private static short[] getSimpleBody(int durationMs, double frequency, double amplitude, Bowed bowedInstrument, boolean isLeft, double phaseMultiplier, int overtoneIndex)
    {
        durationMs += Constants.FadeOutDurationMs_;

        double multiplier2 = isLeft ? 0.01 : 0.02;
        double multiplier3 = isLeft ? 0.015 : 0.03;
        short[] samples1 = bowedInstrument.generateSamples(frequency, durationMs / 1000.0);
        double basePulsationSpeed = Constants.BASE_PULSATION_SPEED;
        double multiplier = (isLeft ? LEFT_MULTIPLIER : RIGHT_MULTIPLIER) * phaseMultiplier ;
        double time = 1.0 / Constants.SAMPLE_RATE; // Время на один семпл
        double pulsation = Constants.PulsationSpeedFactor_ * (basePulsationSpeed * multiplier) * time * 0.5;
        pulsation /= 0.25;
        if (overtoneIndex < 2)
        {
            samples1 = Util.addPulsation(samples1, pulsation, multiplier, 0);
        }
        short[] samples2 = bowedInstrument.generateSamples(frequency + frequency * multiplier2, durationMs / 1000.0);
        if (overtoneIndex < 2)
        {
            samples2 = Util.addPulsation(samples2, pulsation, multiplier, 0);
        }
        short[] samples3 = bowedInstrument.generateSamples(frequency + frequency * multiplier3, durationMs / 1000.0);
        if (overtoneIndex < 2)
        {
            samples3 = Util.addPulsation(samples3, pulsation, multiplier, 0);
        }
        short[] samples4 = bowedInstrument.generateSamples(frequency + frequency * multiplier3, durationMs / 1000.0);
        if (overtoneIndex < 2)
        {
            samples4 = Util.addPulsation(samples4, pulsation, multiplier, 0);
        }

//        samples3 = Util.applyFadeInOut(samples3, 350, 350, 1200, isLeft ? 500 : 0);
//        samples4 = Util.applyFadeInOut(samples4, 200, 200, 800, isLeft ? 250 : 750);

        int shift = (int) (samples1.length * 0.02);
        short[] extendedSamples2 = new short[(int) (samples1.length + shift)];
        short[] extendedSamples3 = new short[(int) (samples1.length + shift + shift)];
        short[] extendedSamples4 = new short[(int) (samples1.length + shift + shift + shift)];

        System.arraycopy(samples2, 0, extendedSamples2, shift, samples2.length);
        System.arraycopy(samples3, 0, extendedSamples3, shift + shift, samples3.length);
        System.arraycopy(samples4, 0, extendedSamples4, shift + shift + shift, samples3.length);

        short[] combinedSamples = Util.combineSamples(samples1, extendedSamples2);
        combinedSamples = Util.combineSamples(combinedSamples, extendedSamples3);
        combinedSamples = Util.combineSamples(combinedSamples, extendedSamples4);

        short[] fullSamples = new short[combinedSamples.length - shift * 6];
        System.arraycopy(combinedSamples, shift * 3, fullSamples, 0, fullSamples.length);

        for (int i = 0; i < fullSamples.length; ++i)
        {
            fullSamples[i] = (short) (fullSamples[i] * amplitude);
        }

        int fadeInSamples =  Util.convertMsToSampleLength(Constants.FADE_IN_DURATION_MS);
        int fadeOutSamples =  Util.convertMsToSampleLength(Constants.FadeOutDurationMs_);

        for (int i = 0; i < fadeInSamples; ++i)
        {
            double fadeFactor = (double) i / fadeInSamples;
            fullSamples[i] = (short) (fullSamples[i] * fadeFactor);
        }
        int fadeOutStartIndex = fullSamples.length - fadeOutSamples;
        for (int i = 0; i < fadeOutSamples; ++i)
        {
            double fadeFactor = 1.0 - (double) i / fadeOutSamples;
            fullSamples[fadeOutStartIndex + i] = (short) (fullSamples[fadeOutStartIndex + i] * fadeFactor);
        }
        return fullSamples;

    }
}
