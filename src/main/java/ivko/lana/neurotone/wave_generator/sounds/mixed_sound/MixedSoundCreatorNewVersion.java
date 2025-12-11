package ivko.lana.neurotone.wave_generator.sounds.mixed_sound;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.ISamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanHitSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.violin.Chord;
import ivko.lana.neurotone.wave_generator.sounds.violin.ChordDetail;
import ivko.lana.neurotone.wave_generator.sounds.violin.ViolinSamplesCreator;
import ivko.lana.util.Pair;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

;

/**
 * @author Lana Ivko
 */
public class MixedSoundCreatorNewVersion implements ISamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(MixedSoundCreatorNewVersion.class.getName());

    private ViolinSamplesCreator violinSamplesCreator_;
    private TibetanHitSamplesCreator hitSamplesCreator_;
    private Creator creator_;
    private Random random_;

    public MixedSoundCreatorNewVersion()
    {
        violinSamplesCreator_ = new ViolinSamplesCreator();
        hitSamplesCreator_ = new TibetanHitSamplesCreator();
        creator_ = new Creator();
        random_ = new Random();
    }

    @Override
    public short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, double phaseMultiplier, int overtoneIndex)
    {
        return creator_.createSamples(durationMs, frequency, amplitude, isLeft, phaseMultiplier, overtoneIndex);
    }

    private class Creator
    {
        private int durationMs_;
        private double frequency_;
        private double amplitude_;
        private boolean isLeft_;
        private double phaseMultiplier_;
        private int overtoneIndex_;

        short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, double phaseMultiplier, int overtoneIndex)
        {
            durationMs_ = durationMs;
            frequency_ = frequency;
            amplitude_ = amplitude;
            isLeft_ = isLeft;
            phaseMultiplier_ = phaseMultiplier;
            overtoneIndex_ = overtoneIndex;
            violinSamplesCreator_.setAdditionalSamplesConverter(this::createHitSamples);
            return violinSamplesCreator_.createSamples(durationMs, frequency, amplitude, isLeft, phaseMultiplier, overtoneIndex);
        }

        private short[] createHitSamples(ChordDetail chordDetail)
        {
            Chord chord = chordDetail.getChord();
            int beats = chordDetail.getBeats();
            Pair<Integer, Integer>[] changeFactors = chord.getChangeFactors();
            double[] hitFrequencies = new double[beats];
            for (int i = 0; i < beats; ++i)
            {
                boolean needPause = random_.nextBoolean();
                if (needPause)
                {
                    hitFrequencies[i] = 0;
                }
                else
                {
                    int frequencyIndex = random_.nextInt(changeFactors.length);
                    Pair<Integer, Integer> changeFactor = changeFactors[frequencyIndex];
                    hitFrequencies[i] = (changeFactor.getFirst() * frequency_ / (double) changeFactor.getSecond()) * 2;
                }
            }
            logger.info(String.format("For chordDetail %s created hit: %s", chordDetail, Arrays.toString(hitFrequencies)));
            int beatMs = durationMs_ / beats;
            int samplePerBeat = (int) ((beatMs / 1000) * Constants.SAMPLE_RATE);

            Map<Double, short[]> hitSamplesByFrequency = new HashMap<>();
            List<short[]> beatsSounds = new ArrayList<>();
            for (int i = 0; i < hitFrequencies.length; ++i)
            {
                double hitFrequency = hitFrequencies[i];
                if (hitFrequency != 0)
                {
                    short[] hitSamples = hitSamplesByFrequency.get(hitFrequency);
                    if (hitSamples == null)
                    {
                        hitSamples = hitSamplesCreator_.createHitSamples(hitFrequency, amplitude_);
                        hitSamplesByFrequency.put(hitFrequency, hitSamples);
                    }
                    beatsSounds.add(hitSamples);
                }
                else
                {
                    beatsSounds.add(null);
                }
            }

            int totalSamplesLength  = (int) ((durationMs_ / 1000) * Constants.SAMPLE_RATE);
            for (int i = 0; i < beatsSounds.size(); ++i)
            {
                short[] samples = beatsSounds.get(i);
                if (samples != null)
                {
                    totalSamplesLength = Math.max(totalSamplesLength, (i * samplePerBeat) + samples.length);
                }
            }

            int currentShift = 0;
            int lastTailLength = 0;
            short[] fullSamples = new short[totalSamplesLength];
            for (int i = 0; i < beatsSounds.size(); ++i)
            {
                short[] samples = beatsSounds.get(i);
                if (samples != null)
                {
                    if (lastTailLength == 0)
                    {
                        System.arraycopy(samples, 0, fullSamples, currentShift, samples.length);
                    }
                    else
                    {
                        for (int j = 0; j < lastTailLength; ++j)
                        {
                            short previousValue = fullSamples[j + currentShift];
                            short currentValue = samples[j];
                            fullSamples[j + currentShift] = Util.getLimitedValue(previousValue + currentValue);
                        }
                        System.arraycopy(samples, lastTailLength, fullSamples, lastTailLength + currentShift, samples.length  - lastTailLength);
                    }
                    lastTailLength = Math.max(samples.length - samplePerBeat, 0);
                }
                currentShift += samplePerBeat;
            }
            return fullSamples;
        }
    }

    public static void main(String[] args) throws LineUnavailableException
    {
        MixedSoundCreatorNewVersion mixedSoundCreator = new MixedSoundCreatorNewVersion();
        short[] fullSamples = mixedSoundCreator.createSamples(16000, 174, 0.5, true, 0, 0);

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
