package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.Chord;
import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.ISound;
import ivko.lana.yaml.RhythmPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class ChordRhythmGenerator extends RhythmGenerator
{
    public ChordRhythmGenerator(Initializer initializer)
    {
        super(initializer);
    }

    @Override
    protected RhythmPattern getRhythmPattern()
    {
        return initializer_.getChordRhythmPattern();
    }

    @Override
    protected ISound createNewSound(int tone, int duration, int accentIndex)
    {
        IScale scale = initializer_.getScale();
        Integer[] chordNotes = scale.findChord(tone, scale.getChords(tone));
        boolean isChordSequeced = initializer_.isChordSequeced();
        List<Note> chordNodes;
        if (isChordSequeced)
        {
            int availableDuration = duration;
            int roughDuration = duration / chordNotes.length;
            chordNodes = new ArrayList<>();
            for (int i = 0; i < chordNotes.length; ++i)
            {
                int currentDuration = i < chordNotes.length - 1 ? roughDuration : availableDuration;
                chordNodes.add(new Note(chordNotes[i], currentDuration, accents_.get(accentIndex)));
                availableDuration -= currentDuration;
            }
        }
        else
        {
            chordNodes = Stream.of(chordNotes)
                    .map(note -> new Note(note, duration, accents_.get(accentIndex)))
                    .collect(Collectors.toList());

        }
        return new Chord(chordNodes, isChordSequeced);
    }
}
