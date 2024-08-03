package ivko.lana.neurotone.wave_generator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public enum Triad
{
    TONIC(1, 3, 5),
    SUPER_TONIC(2, 4, 6),
    MEDIANT(3, 5, 7),
    SUB_DOMINANT(4, 6, 1),
    DOMINANT(5, 7, 2),
    SUB_MEDIANT(6, 1, 3),
    DIMINISHED_CHORD(7, 2, 4);

    Triad(Integer... sequence)
    {
        scaleDegreeSequence_ = Arrays.asList(sequence);
    }

    public List<Double> getFrequencySequence()
    {
        List<Integer> scaleDegreeSequence = scaleDegreeSequence_;
        return scaleDegreeSequence.stream()
                .map(NoteGenerator::getScaleDegree)
                .collect(Collectors.toList());
    }

    private List<Integer> scaleDegreeSequence_;
    public static final int TRIAD_SIZE = 3;
}
