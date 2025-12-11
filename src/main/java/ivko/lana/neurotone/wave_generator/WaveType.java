package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.wave_generator.ConfigFileNotesDistributor;
import ivko.lana.neurotone.wave_generator.config.ConfiguredMelodyLoader;
import ivko.lana.neurotone.wave_generator.melody.MelodyNotesDistributor;
import ivko.lana.neurotone.wave_generator.solfege.SolfegeNotesDistributor;

/**
 * @author Lana Ivko
 */
public enum WaveType
{
    MELODY(new MelodyNotesDistributor()),
    SOLFEGE(new SolfegeNotesDistributor()),
    CONFIGURED(new ConfigFileNotesDistributor(new ConfiguredMelodyLoader().load()));

    public INotesDistributor getNoteDistributor()
    {
        return notesDistributor_;
    }

    public String generateFileName()
    {
        return notesDistributor_.generateFileName();
    }

    WaveType(INotesDistributor notesDistributor)
    {
        notesDistributor_ = notesDistributor;
    }
    private INotesDistributor notesDistributor_;
}
