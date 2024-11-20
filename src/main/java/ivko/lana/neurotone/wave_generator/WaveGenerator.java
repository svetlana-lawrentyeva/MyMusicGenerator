package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.IWaveGenerator;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.processing.NotesSerializer;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.wave_generator.melody.Triad;
import ivko.lana.neurotone.wave_generator.melody.TriadSequenceGenerator;

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
    private INotesDistributor notesDistributor_;
    private volatile int seconds_;

    public WaveGenerator(int minutes)
    {
        seconds_ = minutes * 60;
        WaveType waveType = Constants.WaveType_;
        notesDistributor_ = waveType.getNoteDistributor();
        frequencyConverter_ = new FrequencyConverter(waveType);
    }

    public boolean generateMusic()
    {
        boolean result = false;
        if (seconds_ > 0)
        {
            int tailDefiner = seconds_ % 10;
            String tail = tailDefiner == 1 && (seconds_ < 10 || seconds_ > 20)
                    ? "у"
                    : tailDefiner > 1 && tailDefiner < 5 && (seconds_ < 10 || seconds_ > 20)
                    ? "ы"
                    : "";
            logger.info(String.format("Осталось сгенерировать %s секунд%s", seconds_, tail));
            if (Constants.IsSeparatedChannels_)
            {
                double[][] notesForLeftChannel = notesDistributor_.getNotes();
                double[][] notesForRightChannel = notesDistributor_.getNotes();
                int leftSeconds = calculateNotesDuration(notesForLeftChannel);
                int rightSeconds = calculateNotesDuration(notesForRightChannel);
                seconds_ -= Math.min(leftSeconds, rightSeconds);
                if (seconds_ < 0)
                {
                    notesForLeftChannel = addLastNotes(notesForLeftChannel);
                    notesForRightChannel = addLastNotes(notesForRightChannel);
                }
                try
                {
                    NotesSerializer.getInstance().serializeToCSV(notesForLeftChannel);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                logger.info("Left channel: " + getLogMessage(notesForLeftChannel));
                logger.info("Right channel: " + getLogMessage(notesForRightChannel));
                frequencyConverter_.convert(notesForLeftChannel, notesForRightChannel);
            }
            else if (Constants.IsSeparatedMelodies_)
            {
                double[][] notesForLeftChannel = notesDistributor_.getNotesForLeftChannel();
                double[][] notesForRightChannel = notesDistributor_.getNotesForRightChannel();
                int leftSeconds = calculateNotesDuration(notesForLeftChannel);
                int rightSeconds = calculateNotesDuration(notesForRightChannel);
                seconds_ -= Math.min(leftSeconds, rightSeconds);
                if (seconds_ < 0)
                {
                    notesForLeftChannel = addLastNotes(notesForLeftChannel);
                    notesForRightChannel = addLastNotes(notesForRightChannel);
                }
                try
                {
                    NotesSerializer.getInstance().serializeToCSV(notesForLeftChannel);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                logger.info("Left channel: " + getLogMessage(notesForLeftChannel));
                logger.info("Right channel: " + getLogMessage(notesForRightChannel));
                frequencyConverter_.convert(notesForLeftChannel, notesForRightChannel);
            }
            else
            {
                double[][] notes = notesDistributor_.getNotes();
                seconds_ -= calculateNotesDuration(notes);
                if (seconds_ < 0)
                {
                    notes = addLastNotes(notes);
                }
                try
                {
                    NotesSerializer.getInstance().serializeToCSV(notes);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                logger.info(getLogMessage(notes));
                frequencyConverter_.convert(notes);
            }
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
        double beatDurationMs = Constants.BeatDurationMs_;
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

            List<Double> scaleDegrees = triad.getScaleDegrees();
            double[][] notes = new double[scaleDegrees.size()][2];

            if (hasMoreAccords && hasMoreBeats)
            {
                // Оба массива имеют элементы
                double noteDuration = (beatCount * beatDurationMs) / scaleDegrees.size();
                for (int i = 0; i < scaleDegrees.size(); ++i)
                {
                    Double scaleDegree = scaleDegrees.get(i);
                    notes[i][0] = scaleDegree;
                    notes[i][1] = noteDuration;
                }
                result.addAll(Arrays.asList(notes));
            }
            else if (!hasMoreAccords && hasMoreBeats)
            {
                // Остались биты, но закончились аккорды
                double noteDuration = (beatCount * beatDurationMs) / scaleDegrees.size();
                for (int i = 0; i < scaleDegrees.size(); ++i)
                {
                    Double scaleDegree = scaleDegrees.get(i);
                    notes[i][0] = scaleDegree;
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
                    double pinchedDuration = (oneNoteBeat * beatDurationMs) / scaleDegrees.size();
                    double leftDuration = leftBeatCount * beatDurationMs;
                    boolean isLastNote = noteIndex == triads.length;

                    for (int i = 0; i < scaleDegrees.size(); ++i)
                    {
                        Double scaleDegree = scaleDegrees.get(i);
                        notes[i][0] = scaleDegree;
                        notes[i][1] = isLastNote && i == scaleDegrees.size() - 1
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
