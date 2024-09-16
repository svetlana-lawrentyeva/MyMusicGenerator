package ivko.lana.neurotone.wave_generator.solfege;

import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.wave_generator.INotesDistributor;
import ivko.lana.neurotone.wave_generator.sounds.tibetan.TibetanHitSamplesCreator;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class SolfegeNotesDistributor implements INotesDistributor
{
    private static final Logger logger = CustomLogger.getLogger(SolfegeNotesDistributor.class.getName());
    private static final int[] SOLFEGE_FREQUENCIES = {174, 285, 396, 417, 528, 639, 741, 852, 963};
    public static final int MIN_DURATION = 2000;
    public static final int MIN_NUMBER = 3;
    private static int BATCH_NUMBER = 10;

    private Random random_;

    private int[] activeFrequencies_;

    public SolfegeNotesDistributor()
    {
        random_ = new Random();
        int length = random_.nextInt(SOLFEGE_FREQUENCIES.length - MIN_NUMBER) + MIN_NUMBER;
        activeFrequencies_ = new int[length];
        int startIndex = random_.nextInt(SOLFEGE_FREQUENCIES.length - length);
        System.arraycopy(SOLFEGE_FREQUENCIES, startIndex, activeFrequencies_, 0, length);
        String frequencies = Arrays.stream(activeFrequencies_).mapToObj(String::valueOf).collect(Collectors.joining("_"));
        logger.info(String.format("Frequencies: %s", frequencies));
        AudioSaver.setFileName(frequencies + ".wav");
    }

    @Override
    public double[][] getLastNotes()
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
            int random = random_.nextInt(10000);
            int frequencyDuration = 1000 * ((random) / 1000) + MIN_DURATION * 4;
            while (frequencyDuration > MIN_DURATION)
            {
                int randomDuration = random_.nextInt(frequencyDuration - MIN_DURATION);
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
