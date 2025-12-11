package ivko.lana.neurotone.wave_generator.solfege;

import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.processing.TibetanGenerator;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.wave_generator.INotesDistributor;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class SolfegeNotesDistributor implements INotesDistributor
{
    private static final Logger logger = CustomLogger.getLogger(SolfegeNotesDistributor.class.getName());
    private static final double[] SOLFEGE_FREQUENCIES = {174, 285, 396, 417, 528, 639, 741, 852, 963};
    public static final int MIN_DURATION = 12000;
    public static final int MAX_DURATION = 20000;
    public static final int MIN_NUMBER = 3;
    public static final int SLOWER_DURATION_FACTOR = 1;
    private static int BATCH_NUMBER = 10;

    private Random random_;

    private double[] activeFrequencies_;

    public SolfegeNotesDistributor()
    {
        random_ = new Random();
        if (Constants.OneTone_)
        {
            activeFrequencies_ = new double[]{Constants.BaseFrequency_};
        }
        else
        {
//            int length = TibetanGenerator.IS_MOCK ? SOLFEGE_FREQUENCIES.length : random_.nextInt(SOLFEGE_FREQUENCIES.length - MIN_NUMBER) + MIN_NUMBER;
            int length = MIN_NUMBER;
            activeFrequencies_ = new double[length];
            int startIndex = TibetanGenerator.IS_MOCK ? 0 : random_.nextInt(SOLFEGE_FREQUENCIES.length - length);
            System.arraycopy(SOLFEGE_FREQUENCIES, startIndex, activeFrequencies_, 0, length);
//            System.arraycopy(SOLFEGE_FREQUENCIES, 3, activeFrequencies_, 0, length);
        }
    }

    @Override
    public String generateFileName()
    {
        String frequencies = Arrays.stream(activeFrequencies_).mapToObj(String::valueOf).collect(Collectors.joining("_"));
        logger.info(String.format("Frequencies: %s", frequencies));
        return frequencies + ".wav";
    }

    @Override
    public double[][] getLastNotes()
    {
        return getNotes();
    }

    @Override
    public double[][] getNotesForLeftChannel()
    {
        return getNotes();
    }

    @Override
    public double[][] getNotesForRightChannel()
    {
        return getNotes();
    }

    @Override
    public double[][] getNotes()
    {
        double[][] notes = new double[BATCH_NUMBER][];
        for (int i = 0; i < BATCH_NUMBER; ++i)
        {
            notes[i] = new double[2];
            int frequencyIndex = random_.nextInt(activeFrequencies_.length);
            int random = random_.nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION;
            int frequencyDuration = 1000 * ((random) / 1000) * SLOWER_DURATION_FACTOR;
            while (frequencyDuration >= MIN_DURATION)
            {
                int randomDuration = frequencyDuration > MIN_DURATION ? random_.nextInt(frequencyDuration - MIN_DURATION) : frequencyDuration;
                int duration = frequencyDuration > MIN_DURATION + 1000 ? 1000 * (randomDuration / 1000) + MIN_DURATION : frequencyDuration;
                notes[i][0] = frequencyIndex + 1;
                notes[i][1] = frequencyDuration;
                frequencyDuration -= duration;
            }
        }
        return notes;
    }

    @Override
    public double getFrequency(int frequencyIndex)
    {
        return activeFrequencies_[frequencyIndex - 1];
    }
}
