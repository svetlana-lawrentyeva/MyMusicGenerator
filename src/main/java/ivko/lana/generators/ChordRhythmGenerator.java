package ivko.lana.generators;

import ivko.lana.entities.IScale;
import ivko.lana.musicentities.Chord;
import ivko.lana.musicentities.Note;
import ivko.lana.musicentities.ISound;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    protected ISound createNewSound(int tone, int duration, Integer accent)
    {
        IScale scale = initializer_.getScale();
        int[] chordNotes = scale.findChord(tone - IScale.BASE_NOTE, scale.getChords());
        List<Note> chordNodes = IntStream.of(chordNotes)
                .mapToObj(note -> new Note(note, duration, accent))
                .collect(Collectors.toList());
        return new Chord(chordNodes, initializer_.isChordSequeced());
    }
}
