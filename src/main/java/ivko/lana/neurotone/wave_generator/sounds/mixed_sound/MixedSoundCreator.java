package ivko.lana.neurotone.wave_generator.sounds.mixed_sound;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.ISamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanHitSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.violin.Chord;
import ivko.lana.neurotone.wave_generator.sounds.violin.ChordDetail;
import ivko.lana.neurotone.wave_generator.sounds.violin.ViolinSamplesCreator;
import ivko.lana.util.Pair;
import org.bytedeco.javacpp.annotation.Const;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

;

/**
 * @author Lana Ivko
 */
public class MixedSoundCreator implements ISamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(MixedSoundCreator.class.getName());

    private ViolinSamplesCreator violinSamplesCreator_;
    private TibetanHitSamplesCreator tibetanSamplesCreator_;
    private Creator creator_;
    private Random random_;

    public MixedSoundCreator()
    {
        violinSamplesCreator_ = new ViolinSamplesCreator();
        tibetanSamplesCreator_ = new TibetanHitSamplesCreator();
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
            violinSamplesCreator_.setAdditionalSamplesConverter(this::createTibetanSamples);
            return violinSamplesCreator_.createSamples(durationMs, frequency, amplitude, isLeft, phaseMultiplier, overtoneIndex);
        }

        private short[] createTibetanSamples(ChordDetail chordDetail)
        {
            Chord chord = chordDetail.getChord();
            int beats = chordDetail.getBeats();
            Pair<Integer, Integer>[] changeFactors = chord.getChangeFactors();
//            int totalBeatsCounter = random_.nextInt(beats) + 1;
            int totalBeatsCounter = beats;
//            int tibetanFrequenciesCounter = random_.nextInt(Math.min(totalBeatsCounter, changeFactors.length)) + 1;
            int tibetanFrequenciesCounter = changeFactors.length;
            int oneBeatDurationMs = durationMs_ / totalBeatsCounter;
//            int oneBeatDurationMs = durationMs_ / changeFactors.length;
            logger.info(String.format("starting divideIntoRandomParts with totalBeatsCounter: %s, tibetanFrequenciesCounter: %s", totalBeatsCounter, tibetanFrequenciesCounter));
//            int[] beatsPerFrequency = divideIntoRandomParts(totalBeatsCounter, tibetanFrequenciesCounter);
            int[] beatsPerFrequency = new int[totalBeatsCounter];
            Arrays.fill(beatsPerFrequency, 1);

            List<Pair<short[], Integer>> samplesAndTailSizes = createSamplesAndTailSizes(
                    tibetanFrequenciesCounter, changeFactors, beatsPerFrequency, oneBeatDurationMs);

            short[] fullSamples = concatAllSamples(samplesAndTailSizes);

            return fullSamples;
        }

        private short[] concatAllSamples(List<Pair<short[], Integer>> samplesAndTailSizes)
        {
            logger.info(String.format("starting concat all samples: %s", samplesAndTailSizes.stream().map(pair -> String.format("{%s : %s}", pair.getFirst().length, pair.getSecond())).collect(Collectors.joining(", "))));
            int totalLength = samplesAndTailSizes.stream()
                    .mapToInt(pair -> pair.getFirst().length - pair.getSecond())
                    .sum() + samplesAndTailSizes.get(samplesAndTailSizes.size() - 1).getSecond();
            logger.info(String.format("total length: %s", totalLength));

            int lastStartFadeOutIndex = 0;
            int lastTailLength = 0;
            short[] fullSamples = new short[totalLength];
            for (int i = 0; i < samplesAndTailSizes.size(); i++)
            {
                Pair<short[], Integer> pair = samplesAndTailSizes.get(i);
                short[] samples = pair.getFirst();
                logger.info(String.format("took pair: {%s :%s}", samples.length, pair.getSecond()));

                if (i == 0)
                {
                    logger.info(String.format("when i == 0 we copy from samples from 0 index, to fullSamples to %s index, length: %s. now fullSamples is full till %s index",
                            lastStartFadeOutIndex, samples.length, lastStartFadeOutIndex + samples.length));
                    System.arraycopy(samples, 0, fullSamples, lastStartFadeOutIndex, samples.length);
                    lastStartFadeOutIndex = samples.length - pair.getSecond();
                    logger.info(String.format("lastStartFadeOutIndex: %s", lastStartFadeOutIndex));
                    lastTailLength = pair.getSecond();
                    logger.info(String.format("lastTailLength: %s", lastTailLength));
                }
                else
                {
                    logger.info(String.format("when i > 0 we concat common part after fadeout. start from fullSamples index %s, length: %s. now fullSamples is full till %s index",
                            lastStartFadeOutIndex, lastTailLength, lastStartFadeOutIndex + lastTailLength));
                    for (int j = 0; j < lastTailLength; j++)
                    {
                        short previousValue = fullSamples[lastStartFadeOutIndex + j];
                        short currentValue = samples[j];
                        fullSamples[lastStartFadeOutIndex + j] = Util.getLimitedValue(previousValue + currentValue);
                    }

                    logger.info(String.format("and finally we copy the rest of samples from index %s to fullSamples from index %s. length: %s. now fullSamples is full till %s index",
                            lastTailLength, lastStartFadeOutIndex + lastTailLength, samples.length - lastTailLength, lastStartFadeOutIndex + lastTailLength + (samples.length - lastTailLength)));
                    System.arraycopy(samples, lastTailLength, fullSamples, lastStartFadeOutIndex + lastTailLength, samples.length - lastTailLength);
                    lastStartFadeOutIndex = lastStartFadeOutIndex + samples.length - pair.getSecond();
                    logger.info(String.format("lastStartFadeOutIndex: %s", lastStartFadeOutIndex));
                    lastTailLength = pair.getSecond();
                    logger.info(String.format("lastTailLength: %s", lastTailLength));
                }
            }
            return fullSamples;
        }

        private List<Pair<short[], Integer>> createSamplesAndTailSizes(int tibetanFrequenciesCounter, Pair<Integer,
                Integer>[] changeFactors, int[] beatsPerFrequency, int oneBeatDurationMs)
        {
            logger.info(String.format("createSamplesAndTailSizes with beatsPerFrequency: %s", Arrays.toString(beatsPerFrequency)));
            int startFrequencyChooserIndex = random_.nextInt(changeFactors.length + 1 - tibetanFrequenciesCounter);
            List<Pair<short[], Integer>> samplesAndTailSizes = new ArrayList<>();
//            for (int i = 0; i < tibetanFrequenciesCounter; ++i)
//            {
//                Pair<Integer, Integer> changeFactor = changeFactors[startFrequencyChooserIndex + i];
//                double currentFrequency = 2 * (changeFactor.getFirst() * frequency_ / changeFactor.getSecond());
//                int currentCounter = beatsPerFrequency[i];
//                logger.info(String.format("currentCounter: %s", currentCounter));
//                int currentSoundQty = random_.nextInt(currentCounter) + 1;
//                int shift = (random_.nextInt(currentCounter / currentSoundQty) + 1) * oneBeatDurationMs;
//                short[] tibetanBaseSamples = tibetanSamplesCreator_.createHitSamples(currentFrequency, amplitude_);
//                short[] totalSoundSamples = new short[tibetanBaseSamples.length + shift * (currentSoundQty - 1)];
//                int currentShift = 0;
//                for (int j = 0; j < currentSoundQty; ++j)
//                {
//                    if (j == 0)
//                    {
//                        System.arraycopy(tibetanBaseSamples, 0, totalSoundSamples, currentShift, tibetanBaseSamples.length);
//                    }
//                    else
//                    {
//                        for (int k = 0; k < tibetanBaseSamples.length - shift; ++k)
//                        {
//                            short previousValue = totalSoundSamples[k + currentShift];
//                            short currentValue = tibetanBaseSamples[k];
//                            totalSoundSamples[k + currentShift] = Util.getLimitedValue(previousValue + currentValue);
//                        }
//                        System.arraycopy(tibetanBaseSamples, tibetanBaseSamples.length - shift, totalSoundSamples, tibetanBaseSamples.length + currentShift - shift, shift);
//                    }
//                    currentShift += shift;
//                }
//                int expectedSoundDurationMs = currentCounter * oneBeatDurationMs;
//                int currentSoundDurationMs = (int) (1000 * totalSoundSamples.length / Constants.SAMPLE_RATE);
//                int tail = (int) ((Math.max(currentSoundDurationMs - expectedSoundDurationMs, 0) * Constants.SAMPLE_RATE) / 1000.0);
//                samplesAndTailSizes.add(new Pair<>(totalSoundSamples, tail));
//            }
            Random random = new Random();
            int index = random.nextInt(changeFactors.length);
            int number = random.nextInt(changeFactors.length - 1) + 1;
            for (int i = 0; i < number; ++i)
            {
                Pair<Integer, Integer> changeFactor = changeFactors[startFrequencyChooserIndex + index];
                double currentFrequency = 2 * (changeFactor.getFirst() * frequency_ / changeFactor.getSecond());
                int currentCounter = beatsPerFrequency[index];
                logger.info(String.format("currentCounter: %s", currentCounter));
                int currentSoundQty = random_.nextInt(currentCounter) + 1;
                int shift = (random_.nextInt(currentCounter / currentSoundQty) + 1) * oneBeatDurationMs;
                short[] tibetanBaseSamples = tibetanSamplesCreator_.createHitSamples(currentFrequency, amplitude_);
                short[] totalSoundSamples = new short[tibetanBaseSamples.length + shift * (currentSoundQty - 1)];
                int currentShift = 0;
                for (int j = 0; j < currentSoundQty; ++j)
                {
                    if (j == 0)
                    {
                        System.arraycopy(tibetanBaseSamples, 0, totalSoundSamples, currentShift, tibetanBaseSamples.length);
                    }
                    else
                    {
                        for (int k = 0; k < tibetanBaseSamples.length - shift; ++k)
                        {
                            short previousValue = totalSoundSamples[k + currentShift];
                            short currentValue = tibetanBaseSamples[k];
                            totalSoundSamples[k + currentShift] = Util.getLimitedValue(previousValue + currentValue);
                        }
                        System.arraycopy(tibetanBaseSamples, tibetanBaseSamples.length - shift, totalSoundSamples, tibetanBaseSamples.length + currentShift - shift, shift);
                    }
                    currentShift += shift;
                }
                int expectedSoundDurationMs = currentCounter * oneBeatDurationMs;
                int currentSoundDurationMs = (int) (1000 * totalSoundSamples.length / Constants.SAMPLE_RATE);
                int tail = (int) ((Math.max(currentSoundDurationMs - expectedSoundDurationMs, 0) * Constants.SAMPLE_RATE) / 1000.0);
                samplesAndTailSizes.add(new Pair<>(totalSoundSamples, tail));
            }
            return samplesAndTailSizes;
        }

//        private short[] createTibetanSamples(Chord chord)
//        {
//            logger.info("-----------------------------------------------------------------------------------");
//            logger.info(String.format("Created samples based on %s", chord));
//            Pair<Integer, Integer>[] changeFactors = chord.getChangeFactors();
//            logger.info(String.format("Chord change factors: %s", Arrays.stream(changeFactors).map(Pair::toString).collect(Collectors.joining(", "))));
//            int soundsQty = random_.nextInt(changeFactors.length) + 1;
//            logger.info(String.format("soundsQty: %s (from %s)", soundsQty, changeFactors.length));
//            int soundsCounter = random_.nextInt(50) + 1;
//            logger.info(String.format("soundsCounter: %s (from %s)", soundsCounter, 50));
//            int oneCounterMs = durationMs_ / soundsCounter;
//            logger.info(String.format("oneCounterMs: %s", oneCounterMs));
//            int[] noteCounters = divideIntoRandomParts(soundsCounter, soundsQty);
//            logger.info(String.format("noteCounters: %s", Arrays.toString(noteCounters)));
//            int startSoundsIndex = random_.nextInt(changeFactors.length + 1 - soundsQty);
//            logger.info(String.format("startSoundsIndex: %s (from %s)", startSoundsIndex, changeFactors.length - soundsQty));
//
//            logger.info(String.format("I. Starting iterating from 0 to soundsQty (%s)", soundsQty));
//            List<Pair<short[], Integer>> samplesAndTailSizes = new ArrayList<>();
//            for (int i = 0; i < soundsQty; ++i)
//            {
//                logger.info(String.format("i: %s", i));
//                Pair<Integer, Integer> changeFactor = changeFactors[startSoundsIndex + i];
//                logger.info(String.format("changeFactor: %s", changeFactor));
//                double currentFrequency = 2 * (changeFactor.getFirst() * frequency_ / changeFactor.getSecond());
//                logger.info(String.format("currentFrequency: %s", currentFrequency));
//                int currentCounter = noteCounters[i];
//                logger.info(String.format("currentCounter: %s", currentCounter));
//                int currentSoundQty = random_.nextInt(currentCounter) + 1;
//                logger.info(String.format("currentSoundQty: %s (from %s)", currentSoundQty, currentCounter));
//                int shift = (random_.nextInt(currentCounter / currentSoundQty) + 1) * oneCounterMs;
//                logger.info(String.format("shift: %s", shift, currentCounter / currentSoundQty + 1));
//                short[] tibetanBaseSamples = tibetanSamplesCreator_.createSamples(2000, currentFrequency, amplitude_ * 2, isLeft_, phaseMultiplier_, overtoneIndex_);
//                logger.info(String.format("created base tibetan samples with length %s", tibetanBaseSamples.length));
//                short[] totalSoundSamples = new short[tibetanBaseSamples.length + shift * (currentSoundQty - 1)];
//                logger.info(String.format("created complicated total sound samples with length %s", totalSoundSamples.length));
//                int currentShift = 0;
//                logger.info(String.format("currentShift: %s", currentShift));
//                logger.info(String.format("J. Starting iterating from 0 to currentSoundQty (%s)", currentSoundQty));
//                for (int j = 0; j < currentSoundQty; ++j)
//                {
//                    logger.info(String.format("j: %s", j));
//                    if (j == 0)
//                    {
//                        logger.info(String.format("when j == 0 just copy from base tibetan samples to complicated total sound samples"));
//                        System.arraycopy(tibetanBaseSamples, 0, totalSoundSamples, currentShift, tibetanBaseSamples.length);
//                    }
//                    else
//                    {
//                        logger.info(String.format("when j > 0 we concat samples: total sound samples from %s and tibetan base samples from 0. total length = %s",
//                                currentShift, tibetanBaseSamples.length - shift));
//                        for (int k = 0; k < tibetanBaseSamples.length - shift; ++k)
//                        {
//                            short previousValue = totalSoundSamples[k + currentShift];
//                            short currentValue = tibetanBaseSamples[k];
//                            totalSoundSamples[k + currentShift] = Util.getLimitedValue(previousValue + currentValue);
//                        }
//                        logger.info(String.format("and left tibetan base samples from index %s we copy to total sound samples from index %s, total length: %s",
//                                tibetanBaseSamples.length - shift, tibetanBaseSamples.length + currentShift - shift, shift));
//                        System.arraycopy(tibetanBaseSamples, tibetanBaseSamples.length - shift, totalSoundSamples, tibetanBaseSamples.length + currentShift - shift, shift);
//                    }
//                    currentShift += shift;
//                    logger.info(String.format("currentShift: %s", currentShift));
//                }
//                logger.info(String.format("stop iterating J"));
//                int expectedSoundDurationMs = currentCounter * oneCounterMs;
//                logger.info(String.format("expectedSoundDurationMs: %s", expectedSoundDurationMs));
//                int currentSoundDurationMs = (int) (1000 * totalSoundSamples.length / Constants.SAMPLE_RATE);
//                logger.info(String.format("currentSoundDurationMs: %s", currentSoundDurationMs));
//                int tail = Math.max(currentSoundDurationMs - expectedSoundDurationMs, 0);
//                logger.info(String.format("tail: %s", tail));
//                samplesAndTailSizes.add(new Pair<>(totalSoundSamples, tail));
//                logger.info(String.format("added new samples and tail: {%s : %s}", totalSoundSamples.length, tail));
//            }
//            logger.info(String.format("stop iterating I"));
//
//            int totalLength = samplesAndTailSizes.stream()
//                    .mapToInt(pair -> pair.getFirst().length - pair.getSecond())
//                    .sum() + samplesAndTailSizes.get(samplesAndTailSizes.size() - 1).getSecond();
//
//            short[] fullSamples = new short[totalLength];
//            logger.info(String.format("fullSamples length: %s", fullSamples.length));
//            int lastStartFadeOutIndex = 0;
//            logger.info(String.format("lastStartFadeOutIndex: %s", lastStartFadeOutIndex));
//            int lastTailLength = 0;
//            logger.info(String.format("lastTailLength: %s", lastTailLength));
//            logger.info(String.format("I. Starting iterating from 0 to samplesAndTailSizes (%s)", samplesAndTailSizes.size()));
//            for (int i = 0; i < samplesAndTailSizes.size(); i++)
//            {
//                logger.info(String.format("i: %s", i));
//                Pair<short[], Integer> pair = samplesAndTailSizes.get(i);
//                short[] samples = pair.getFirst();
//                logger.info(String.format("current samples and tail: {%s : %s}", pair.getFirst().length, pair.getSecond()));
//
//                if (i == 0)
//                {
//                    logger.info(String.format("when i == 0 just copy from samples to fullSamples"));
//                    System.arraycopy(samples, 0, fullSamples, lastStartFadeOutIndex, samples.length);
//                    lastStartFadeOutIndex = samples.length - pair.getSecond();
//                    lastTailLength = pair.getSecond();
//                }
//                else
//                {
//                    logger.info(String.format("when i > 0 we concat samples: fullSamples from %s and samples from 0. total length = %s",
//                            lastStartFadeOutIndex, lastTailLength));
//                    for (int j = 0; j < lastTailLength; j++)
//                    {
//                        short previousValue = fullSamples[lastStartFadeOutIndex + j];
//                        short currentValue = samples[j];
//                        fullSamples[lastStartFadeOutIndex + j] = Util.getLimitedValue(previousValue + currentValue);
//                    }
//
//                    logger.info(String.format("and left samples from index %s we copy to fullSamples from index %s, total length: %s",
//                            lastTailLength, lastStartFadeOutIndex + lastTailLength, samples.length - lastTailLength));
//                    System.arraycopy(samples, lastTailLength, fullSamples, lastStartFadeOutIndex + lastTailLength, samples.length - lastTailLength);
//                    lastStartFadeOutIndex = lastStartFadeOutIndex + lastTailLength + samples.length - pair.getSecond();
//                    logger.info(String.format("lastStartFadeOutIndex: %s", lastStartFadeOutIndex));
//                    lastTailLength = pair.getSecond();
//                    logger.info(String.format("lastTailLength: %s", lastTailLength));
//                }
//            }
//            logger.info(String.format("stop iterating I"));
//            logger.info(String.format("result: %s length samples", fullSamples.length));
//
//            return fullSamples;
//        }

        public int[] divideIntoRandomParts(int total, int partsCount)
        {
            if (total < 1 || partsCount < 1 || total < partsCount)
            {
                StringBuilder errorMessageBuilder = new StringBuilder();
                if (total < 1)
                {
                    errorMessageBuilder.append("Total size cannot be less than 0");
                }
                if (partsCount < 1)
                {
                    if (errorMessageBuilder.length() > 0)
                    {
                        errorMessageBuilder.append(", ");
                    }
                    errorMessageBuilder.append("Parts counter cannot be less than 0");
                }
                if (total < partsCount)
                {
                    if (errorMessageBuilder.length() > 0)
                    {
                        errorMessageBuilder.append(", ");
                    }
                    errorMessageBuilder.append("Total size cannot be less than Parts counter");
                }
                throw new IllegalStateException(errorMessageBuilder.toString());
            }
            if ( partsCount == 1)
            {
                return new int[] {total}; // Возвращаем массив с нулями
            }

            Random random = new Random();
            int[] randomPoints = new int[partsCount - 1];

            // Генерация (N-1) случайных чисел в диапазоне от 1 до total - 1
            for (int i = 0; i < randomPoints.length; i++)
            {
                randomPoints[i] = random.nextInt(total - 1) + 1;
            }

            // Добавляем 0 и total в массив
            int[] allPoints = new int[partsCount + 1];
            allPoints[0] = 0;
            allPoints[partsCount] = total;

            // Копируем случайные числа в массив
            System.arraycopy(randomPoints, 0, allPoints, 1, randomPoints.length);

            // Сортируем точки
            Arrays.sort(allPoints);

            // Вычисляем разницы между соседними точками, чтобы получить части
            int[] parts = new int[partsCount];
            for (int i = 0; i < partsCount; i++)
            {
                parts[i] = allPoints[i + 1] - allPoints[i];
            }
            logger.info(String.format("divided info parts: %s", Arrays.toString(parts)));
            return parts;
        }
    }

    public static void main(String[] args) throws LineUnavailableException
    {
        MixedSoundCreator mixedSoundCreator = new MixedSoundCreator();
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
