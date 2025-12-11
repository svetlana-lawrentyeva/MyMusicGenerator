package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.config.ConfiguredMelody;
import ivko.lana.neurotone.wave_generator.config.ConfiguredMelodyLoader;
import ivko.lana.neurotone.wave_generator.config.ConfiguredNote;
import ivko.lana.neurotone.wave_generator.melody.NoteGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Note distributor that reads fixed melodies from a YAML configuration file.
 */
public class ConfigFileNotesDistributor implements INotesDistributor
{
    private final ConfiguredMelody melody_;
    private final double[][] preparedNotes_;
    private boolean emittedOnce_;

    public ConfigFileNotesDistributor()
    {
        this(new ConfiguredMelodyLoader().load());
    }

    public ConfigFileNotesDistributor(ConfiguredMelody melody)
    {
        melody_ = melody;
        double beatDuration = melody_.getBeatDurationMs() != null
                ? melody_.getBeatDurationMs()
                : Constants.BeatDurationMs_;
        Constants.setBeatDurationMs(beatDuration);
        preparedNotes_ = prepareNotes(beatDuration);
    }

    @Override
    public String generateFileName()
    {
        String melodyName = melody_.getName() == null || melody_.getName().isBlank()
                ? "configured_melody"
                : melody_.getName();
        return melodyName + ".wav";
    }

    @Override
    public double[][] getLastNotes()
    {
        return copyNotes();
    }

    @Override
    public double[][] getNotesForLeftChannel()
    {
        return getPreparedSequence();
    }

    @Override
    public double[][] getNotesForRightChannel()
    {
        return getPreparedSequence();
    }

    @Override
    public double[][] getNotes()
    {
        return getPreparedSequence();
    }

    @Override
    public double getFrequency(int degree)
    {
        return degree == 0 ? 0 : NoteGenerator.getFrequency(degree);
    }

    private double[][] prepareNotes(double beatDuration)
    {
        List<double[]> notes = new ArrayList<>();
        List<ConfiguredNote> configuredNotes = melody_.getNotes() == null
                ? Collections.emptyList()
                : melody_.getNotes();
        for (ConfiguredNote note : configuredNotes)
        {
            double duration = beatDuration * note.getBeats();
            double scaleDegree = note.isPause() ? 0 : note.getDegree();
            notes.add(new double[]{scaleDegree, duration});
        }
        return notes.toArray(new double[0][]);
    }

    private double[][] copyNotes()
    {
        double[][] copy = new double[preparedNotes_.length][];
        for (int i = 0; i < preparedNotes_.length; i++)
        {
            copy[i] = preparedNotes_[i].clone();
        }
        return copy;
    }

    private double[][] getPreparedSequence()
    {
        if (!melody_.isLoop())
        {
            if (emittedOnce_)
            {
                return new double[0][];
            }
            emittedOnce_ = true;
        }
        return copyNotes();
    }
}
