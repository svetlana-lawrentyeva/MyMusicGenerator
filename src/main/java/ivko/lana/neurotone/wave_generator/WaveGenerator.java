package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.IWaveGenerator;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.processing.NotesSerializer;
import ivko.lana.neurotone.util.CustomLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class WaveGenerator implements IWaveGenerator
{
    private static final Logger logger = CustomLogger.getLogger(WaveGenerator.class.getName());
    private FrequencyConverter frequencyConverter_;
    private NotesDistributor notesDistributor_;
    private volatile int seconds_;

    public WaveGenerator(int minutes)
    {
        seconds_ = minutes * 60;
        notesDistributor_ = new NotesDistributor();
        frequencyConverter_ = new FrequencyConverter();
    }

    public boolean generateMusic()
    {
        boolean result = false;
        if (seconds_ > 0)
        {
            int tailDefiner = seconds_ % 10;
            String tail = tailDefiner == 1 && (seconds_ < 10 || seconds_ > 20)
                    ? ""
                    : tailDefiner > 1 && tailDefiner < 5 && (seconds_ < 10 || seconds_ > 20)
                    ? "а"
                    : "ов";
            logger.info(String.format("Осталось сгенерировать %s фрагмент%s", seconds_, tail));

            double[][] notes = notesDistributor_.getNotes();
            seconds_ -= calculateNotesDuration(notes);
            if (seconds_ < 0)
            {
                notes = addLastNotes(notes);
            }
            logger.info(getLogMessage(notes));
            try
            {
                NotesSerializer.getInstance().serializeToCSV(notes);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            frequencyConverter_.convert(notes);
            result = true;
        }
        return result;
    }

    private double[][] addLastNotes(double[][] notes)
    {
        double[][] lastNotes = notesDistributor_.getLastNotes();
        double[][] result = new double[lastNotes.length + notes.length][];

        System.arraycopy(notes, 0, result, 0, notes.length);
        System.arraycopy(lastNotes, 0, result, notes.length, lastNotes.length);

        return result;
    }

    private int calculateNotesDuration(double[][] notes)
    {
        int duration = 0;
        for (double[] note : notes)
        {
            duration += (int) (note[1] / 1000.0);
        }
        return duration;
    }

    private String getLogMessage(double[][] notes)
    {
        StringBuilder result = new StringBuilder("{\n");
        for (int i = 0; i < notes.length; i++)
        {
            result.append("    {");
            for (int j = 0; j < notes[i].length; j++)
            {
                result.append(notes[i][j]);
                if (j < notes[i].length - 1)
                {
                    result.append(", ");
                }
            }
            result.append("}");
            if (i < notes.length - 1)
            {
                result.append(",\n");
            }
            else
            {
                result.append("\n");
            }
        }
        result.append("}");
        return result.toString();
    }

    public WaveDetail getLeftChannel()
    {
        return frequencyConverter_.getLeftChannel();
    }

    public WaveDetail getRightChannel()
    {
        return frequencyConverter_.getRightChannel();
    }

    // Метод распределения аккордов по ритму
    private double[][] distributeAccordsToRhythm(TriadSequenceGenerator.TriadSequence triadSequence, int[] rhythms)
    {
        // Длительность одного удара в миллисекундах
        double beatDurationMs = Constants.BEAT_DURATION_MS;
        // Создаем список для хранения результата
        List<double[]> result = new ArrayList<>();

        Triad[] triads = triadSequence.getSequences();
        int noteIndex = 0;
        int rhythmIndex = 0;

        while (noteIndex < triads.length && rhythmIndex < rhythms.length)
        {
            Triad triad = triads[noteIndex++];
            int beatCount = rhythms[rhythmIndex++];

            boolean hasMoreAccords = noteIndex < triads.length;
            boolean hasMoreBeats = rhythmIndex < rhythms.length;

            List<Double> frequencySequence = triad.getFrequencySequence();
            double[][] notes = new double[frequencySequence.size()][2];

            if (hasMoreAccords && hasMoreBeats)
            {
                // Оба массива имеют элементы
                double noteDuration = (beatCount * beatDurationMs) / frequencySequence.size();
                for (int i = 0; i < frequencySequence.size(); ++i)
                {
                    Double frequency = frequencySequence.get(i);
                    notes[i][0] = frequency;
                    notes[i][1] = noteDuration;
                }
                result.addAll(Arrays.asList(notes));
            }
            else if (!hasMoreAccords && hasMoreBeats)
            {
                // Остались биты, но закончились аккорды
                double noteDuration = (beatCount * beatDurationMs) / frequencySequence.size();
                for (int i = 0; i < frequencySequence.size(); ++i)
                {
                    Double frequency = frequencySequence.get(i);
                    notes[i][0] = frequency;
                    notes[i][1] = noteDuration;
                }
                result.addAll(Arrays.asList(notes));
                noteIndex--;  // Отступаем назад для следующей итерации, чтобы повторно использовать последний аккорд
            }
            else if (hasMoreAccords && !hasMoreBeats)
            {
                // Остались аккорды, но закончились биты
                int remainingNotes = triads.length - noteIndex + 1;
                int currentBeatCount = 1;
                int leftBeatCount = beatCount - currentBeatCount;
                double oneNoteBeat = currentBeatCount / (double) remainingNotes;

                do
                {
                    double pinchedDuration = (oneNoteBeat * beatDurationMs) / frequencySequence.size();
                    double leftDuration = leftBeatCount * beatDurationMs;
                    boolean isLastNote = noteIndex == triads.length;

                    for (int i = 0; i < frequencySequence.size(); ++i)
                    {
                        Double frequency = frequencySequence.get(i);
                        notes[i][0] = frequency;
                        notes[i][1] = isLastNote && i == frequencySequence.size() - 1
                                ? pinchedDuration + leftDuration
                                : pinchedDuration;
                    }
                    result.addAll(Arrays.asList(notes));
                    triad = noteIndex < triads.length ? triads[noteIndex++] : null;
                } while (triad != null);
            }
        }

        // Преобразуем список в массив и возвращаем
        return result.toArray(new double[0][]);
    }
}
