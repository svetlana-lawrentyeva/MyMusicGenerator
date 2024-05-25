package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.*;
import ivko.lana.yaml.RhythmPattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class ChordRhythmGenerator extends AccompanimentRhythmGenerator
{

    public ChordRhythmGenerator(Initializer initializer, int channel)
    {
        super(initializer, channel);
    }

    @Override
    protected RhythmPattern getRhythmPattern()
    {
        return initializer_.getChordRhythmPattern();
    }

    @Override

    protected int getNextTone(int accentIndex)
    {
        int bitPerAccent = melodyBits_.length / accents_.size();
        return melodyBits_[bitPerAccent * accentIndex];
    }

    @Override
    protected int generateDuration()
    {
        return initializer_.getChordRhythmPattern().getBaseDuration();
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex, int channel)
    {
        IScale scale = initializer_.getScale();
        Integer[] chordNotes = scale.findChord(tone, scale.getChords(tone));
        boolean isChordSequeced = initializer_.isChordSequeced();
        List<Note> chordNodes;
        if (isChordSequeced)
        {
            int sequencedDuration = duration / 2;
            chordNodes = new ArrayList<>();
            for (Integer chordNote : chordNotes)
            {
                chordNodes.add(new Note(chordNote, sequencedDuration, accents_.get(accentIndex) - 50, getChannel(), initializer_.getMelodyRhythmPattern().getBaseDurationMultiplier()));
            }
        }
        else
        {
            chordNodes = Stream.of(chordNotes)
                    .map(note -> new Note(note, duration, accents_.get(accentIndex) - 50, getChannel(), initializer_.getMelodyRhythmPattern().getBaseDurationMultiplier()))
                    .collect(Collectors.toList());

        }
        return new Chord(chordNodes, isChordSequeced, channel);
    }
}
