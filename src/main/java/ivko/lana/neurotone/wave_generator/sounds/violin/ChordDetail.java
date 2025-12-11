package ivko.lana.neurotone.wave_generator.sounds.violin;

/**
 * @author Lana Ivko
 */
public class ChordDetail
{
    private final Chord chord_;
    private final int beats_;

    public ChordDetail(Chord chord, int beats)
    {
        chord_ = chord;
        beats_ = beats;
    }

    public Chord getChord()
    {
        return chord_;
    }

    public int getBeats()
    {
        return beats_;
    }

    @Override
    public String toString()
    {
        return String.format("Chord: %s, beats: %s", chord_,beats_);
    }
}