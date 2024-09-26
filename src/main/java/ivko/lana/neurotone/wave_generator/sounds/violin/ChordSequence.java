package ivko.lana.neurotone.wave_generator.sounds.violin;

import java.util.*;

import static ivko.lana.neurotone.wave_generator.sounds.violin.Chord.*;

/**
 * @author Lana Ivko
 */
public class ChordSequence
{
    private List<ChordDetail[]> chordsSequences_;
    private Random random_;

    public ChordSequence()
    {
        random_ = new Random();
        chordsSequences_ = new ArrayList<>();
        chordsSequences_.add(new ChordDetail[]{new ChordDetail(Dm, 4), new ChordDetail(G7, 4), new ChordDetail(C, 4), new ChordDetail(Am, 4)});
    }

    public ChordDetail[] getChords()
    {
        int chordIndex = random_.nextInt(chordsSequences_.size());
        return chordsSequences_.get(chordIndex);
    }

    public static int getTotalBeatsQty(ChordDetail[] chords)
    {
        return Arrays.stream(chords)
                .mapToInt(ChordDetail::getBeats)
                .sum();
    }
}
