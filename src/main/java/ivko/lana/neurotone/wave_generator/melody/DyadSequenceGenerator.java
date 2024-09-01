package ivko.lana.neurotone.wave_generator.melody;

import ivko.lana.neurotone.wave_generator.melody.ITriadSequenceGenerator;
import ivko.lana.neurotone.wave_generator.melody.Triad;

import java.util.*;

/**
 * @author Lana Ivko
 */
public class DyadSequenceGenerator implements ITriadSequenceGenerator
{

    private Random random_;
    private Map<Triad, List<Triad>> availableTriadsByPrevious_;

    public DyadSequenceGenerator()
    {
        random_ = new Random();
        init();
    }

    private void init()
    {
        availableTriadsByPrevious_ = new HashMap<>();

        List<Triad> tonicSequences = new ArrayList<>();
        tonicSequences.add(Triad.DIMINISHED_CHORD);
        tonicSequences.add(Triad.TONIC);
        tonicSequences.add(Triad.SUPER_TONIC);
        tonicSequences.add(Triad.MEDIANT);
        availableTriadsByPrevious_.put(Triad.TONIC, tonicSequences);

        List<Triad> superTonicSequences = new ArrayList<>();
        superTonicSequences.add(Triad.TONIC);
        superTonicSequences.add(Triad.SUPER_TONIC);
        superTonicSequences.add(Triad.MEDIANT);
        superTonicSequences.add(Triad.SUB_DOMINANT);
        availableTriadsByPrevious_.put(Triad.SUPER_TONIC, superTonicSequences);

        List<Triad> mediantSequences = new ArrayList<>();
        mediantSequences.add(Triad.SUPER_TONIC);
        mediantSequences.add(Triad.MEDIANT);
        mediantSequences.add(Triad.DOMINANT);
        availableTriadsByPrevious_.put(Triad.MEDIANT, mediantSequences);

        List<Triad> subDominantSequences = new ArrayList<>();
        subDominantSequences.add(Triad.SUB_MEDIANT);
        subDominantSequences.add(Triad.SUB_DOMINANT);
        subDominantSequences.add(Triad.DOMINANT);
        subDominantSequences.add(Triad.MEDIANT);
        availableTriadsByPrevious_.put(Triad.SUB_DOMINANT, subDominantSequences);

        List<Triad> dominantSequences = new ArrayList<>();
        dominantSequences.add(Triad.SUB_DOMINANT);
        dominantSequences.add(Triad.DOMINANT);
        dominantSequences.add(Triad.SUB_MEDIANT);
        dominantSequences.add(Triad.DIMINISHED_CHORD);
        availableTriadsByPrevious_.put(Triad.DOMINANT, dominantSequences);

        List<Triad> subMediantSequences = new ArrayList<>();
        subMediantSequences.add(Triad.DOMINANT);
        subMediantSequences.add(Triad.SUB_MEDIANT);
        subMediantSequences.add(Triad.DIMINISHED_CHORD);
        subMediantSequences.add(Triad.TONIC);
        availableTriadsByPrevious_.put(Triad.SUB_MEDIANT, subMediantSequences);

        List<Triad> diminishedChordSequences = new ArrayList<>();
        diminishedChordSequences.add(Triad.SUB_MEDIANT);
        diminishedChordSequences.add(Triad.TONIC);
        diminishedChordSequences.add(Triad.SUPER_TONIC);
        availableTriadsByPrevious_.put(Triad.DIMINISHED_CHORD, diminishedChordSequences);
    }

    public Triad getNextTriad(Triad previous)
    {
        List<Triad> triads = availableTriadsByPrevious_.get(previous);
        int randomIndex = random_.nextInt(triads.size());
        return triads.get(randomIndex);
    }

    public Triad[] getNext(Triad previous)
    {
        if (previous == null)
        {
            previous = Triad.TONIC;
            return new Triad[] {previous, getNextTriad(previous)};
        }
        return new Triad[] {getNextTriad(previous)};
    }

    @Override
    public Triad[] generateNext(Triad previous)
    {
        return getNext(previous);
    }

    @Override
    public Triad[] generateLast(Triad previous)
    {
        return getNext(previous);
    }
}

