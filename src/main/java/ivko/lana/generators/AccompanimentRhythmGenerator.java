package ivko.lana.generators;

import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Rhythm;
import ivko.lana.yaml.RhythmDetails;

import java.util.List;

/**
 * @author Lana Ivko
 */
public abstract class AccompanimentRhythmGenerator extends RhythmGenerator
{
    protected int[] melodyBits_;

    public AccompanimentRhythmGenerator(Initializer initializer, int channel)
    {
        super(initializer, channel);
    }

    public void setMelodyRhythm(Rhythm rhythm)
    {
        RhythmDetails chordRhythmDetails = initializer_.getChordPrimaryRhythmDetails();
        int baseDuration = chordRhythmDetails.getBaseDuration();
        int accentSize = chordRhythmDetails.getAccents().size();
        melodyBits_ = new int[accentSize * baseDuration];
        List<ISound> melodySounds = rhythm.getSounds();
        int currentBitIndex = 0;
        for (ISound sound : melodySounds)
        {
            int soundDuration = sound.getDuration();
            for (int i = 0; i < soundDuration; ++i)
            {
                melodyBits_[currentBitIndex++] = sound.getTone();
            }
        }
    }
}
