package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.wave_generator.melody.MelodyNotesDistributor;
import ivko.lana.neurotone.wave_generator.solfege.SolfegeNotesDistributor;

/**
 * @author Lana Ivko
 */
public enum WaveType
{
    MELODY(new MelodyNotesDistributor()),
    SOLFEGE(new SolfegeNotesDistributor());

    public INotesDistributor getNoteDistributor()
    {
        return notesDistributor_;
    }

    WaveType(INotesDistributor notesDistributor)
    {
        notesDistributor_ = notesDistributor;
    }
    private INotesDistributor notesDistributor_;
}
