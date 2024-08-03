package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.Rhythm;
import ivko.lana.musicentities.SoundChord;
import ivko.lana.yaml.RhythmDetails;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class ChordRhythmGenerator extends AccompanimentRhythmGenerator
{
    private static int ChordChannelCounter_ = 1;
    private int chordDuration_;
    private int currentBitIndex_ = 0;

    public ChordRhythmGenerator(Initializer initializer, int channel)
    {
        super(initializer, channel);
        chordDuration_ = (int) (initializer.getChordPrimaryRhythmDetails().getBaseDuration() / Math.pow(2, ChordChannelCounter_));
        chordDuration_ = Math.min(chordDuration_, 2);
    }

    @Override
    protected RhythmDetails getRhythmDetails()
    {
        return initializer_.getChordPrimaryRhythmDetails();
    }

    @Override

    protected int getNextTone(int accentIndex)
    {
        int nextTone = melodyBits_[currentBitIndex_];
        currentBitIndex_ += chordDuration_;
        return nextTone;
    }

    @Override
    public void setMelodyRhythm(Rhythm rhythm)
    {
        super.setMelodyRhythm(rhythm);
        currentBitIndex_ = 0;
    }

    @Override
    protected int generateSimpleDuration()
    {
        return chordDuration_;
    }

    @Override
    protected int generateLastDuration(int availableMeasure)
    {
        return chordDuration_;
    }

    @Override
    protected boolean needPause(int availableMeasure)
    {
        return false;
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex, int channel)
    {
        IScale scale = initializer_.getScale();
        Integer[] chordNotes = scale.findChord(tone, scale.getChords(tone));
        boolean isChordSequeced = initializer_.isChordSequeced();
        List<Note> chordNodes;
        int cordDuration = isChordSequeced ? duration / 2 : duration;
        chordNodes = Stream.of(chordNotes)
                .map(note -> new Note(note, cordDuration, accents_.get(accentIndex) - 10 * ChordChannelCounter_, getChannel(), initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()))
                .collect(Collectors.toList());

        return new SoundChord(chordNodes, isChordSequeced, channel);
    }
}
