package ivko.lana.neurotone.wave_generator.sounds.violin;

import ivko.lana.generators.Bowed;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.ISamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanHitSamplesCreator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class ViolinSamplesCreator implements ISamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(ViolinSamplesCreator.class.getName());
    private ChordSequence chordSequence_;
    private Function<ChordDetail, short[]> additionalSamplesConverter_;

    public ViolinSamplesCreator()
    {
        chordSequence_ = new ChordSequence();
    }

    public void setAdditionalSamplesConverter(Function<ChordDetail, short[]> additionalSamplesConverter)
    {
        additionalSamplesConverter_ = additionalSamplesConverter;
    }

    @Override
    public short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, double phaseMultiplier, int overtoneIndex)
    {
        Bowed bowedInstrument = new Bowed();
        bowedInstrument.setBowPressure(0.7);
        bowedInstrument.setBowVelocity(0.5);
        bowedInstrument.startBowing(0.8);

        ChordDetail[] chords = chordSequence_.getChords();
        int totalBeatsQty = ChordSequence.getTotalBeatsQty(chords);
        ChordCreator chordCreator = new ChordCreator(bowedInstrument, frequency, amplitude, isLeft);
        int beatMs = durationMs / totalBeatsQty;
        List<short[]> samplesList = new ArrayList<>();
        for (int i = 0; i < chords.length; ++i)
        {
            ChordDetail chordDetail = chords[i];
            Chord chord = chordDetail.getChord();
            short[] additionalSamples = additionalSamplesConverter_ != null ? additionalSamplesConverter_.apply(chordDetail) : null;
            int additionalBeats = additionalSamples == null
                    ? 0
                    : (int) (((additionalSamples.length / Constants.SAMPLE_RATE) * 1000) / beatMs);
            int beats = Math.max(chordDetail.getBeats(), additionalBeats);
            short[] samples = chordCreator.createSamples(chord, beats * beatMs, phaseMultiplier, overtoneIndex);
            if (additionalSamples != null)
            {
                samples = Util.combineSamples(samples, additionalSamples);
//                samples = additionalSamples;
            }
            samples = applyFadeIn(samples);
            samples = applyFadeOut(samples);
            samplesList.add(samples);
        }

//        fullSamples = applyAmplitude(amplitude, fullSamples);

        int fadeOutLength = Util.convertMsToSampleLength(Constants.FadeOutDurationMs_);
        int totalLength = samplesList.stream()
                .mapToInt(samples -> samples.length - fadeOutLength)
                .sum() + fadeOutLength;

        short[] fullSamples = new short[totalLength];
        int lastEndIndex = 0;
        for (int i = 0; i < samplesList.size(); i++)
        {
            short[] samples = samplesList.get(i);

            if (i == 0)
            {
                System.arraycopy(samples, 0, fullSamples, lastEndIndex, samples.length);
                lastEndIndex += samples.length;
            }
            else
            {
                int startFadeOutIndex = lastEndIndex - fadeOutLength;

                for (int j = 0; j < fadeOutLength; j++)
                {
                    short previousValue = fullSamples[startFadeOutIndex + j];
                    short currentValue = samples[j];
                    fullSamples[startFadeOutIndex + j] = Util.getLimitedValue(previousValue + currentValue);
                }

                System.arraycopy(samples, fadeOutLength, fullSamples, lastEndIndex, samples.length - fadeOutLength);
                lastEndIndex += samples.length - fadeOutLength;
            }
        }

        return fullSamples;
    }

    private short[] applyFadeOut(short[] fullSamples)
    {
        int fadeOutSamples = Util.convertMsToSampleLength(Constants.FadeOutDurationMs_);
        int fadeOutStartIndex = fullSamples.length - fadeOutSamples;
        for (int i = 0; i < fadeOutSamples; ++i)
        {
            double fadeFactor = 1.0 - (double) i / fadeOutSamples;
            fullSamples[fadeOutStartIndex + i] = (short) (fullSamples[fadeOutStartIndex + i] * fadeFactor);
        }
        return fullSamples;
    }

    private short[] applyFadeIn(short[] fullSamples)
    {
        int fadeInSamples = Util.convertMsToSampleLength(Constants.FADE_IN_DURATION_MS);
        for (int i = 0; i < fadeInSamples; ++i)
        {
            double fadeFactor = (double) i / fadeInSamples;
            fullSamples[i] = (short) (fullSamples[i] * fadeFactor);
        }
        return fullSamples;
    }

    private short[] applyAmplitude(double amplitude, short[] fullSamples)
    {
        for (int i = 0; i < fullSamples.length; ++i)
        {
            fullSamples[i] = (short) (fullSamples[i] * amplitude);
        }
        return fullSamples;
    }

    public static void main(String[] args) throws LineUnavailableException
    {
        ViolinSamplesCreator violinSamplesCreator = new ViolinSamplesCreator();
        short[] fullSamples = violinSamplesCreator.createSamples(16000, 174, 0.5, true, 0, 0);

        float sampleRate = 44100;
        byte[] buffer = new byte[2];
        AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();

        for (int i = 0; i < fullSamples.length; i++)
        {
            buffer[0] = (byte) (fullSamples[i] & 0xFF);
            buffer[1] = (byte) (fullSamples[i] >> 8);
            sdl.write(buffer, 0, 2);
        }

        sdl.drain();
        sdl.stop();
    }
}
