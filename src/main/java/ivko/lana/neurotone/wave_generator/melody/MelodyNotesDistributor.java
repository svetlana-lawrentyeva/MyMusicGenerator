package ivko.lana.neurotone.wave_generator.melody;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.INotesDistributor;
import ivko.lana.neurotone.wave_generator.RhythmGenerator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class MelodyNotesDistributor implements INotesDistributor
{
    private ITriadSequenceGenerator triadSequenceGenerator_;
    private Triad previousTriad_;
    private Random random_;

    private RhythmGenerator rhythmGenerator_;
    private Map<Integer, List<List<Integer>>> availableRhythmsByNoteNumber_ = new HashMap<>();

    public MelodyNotesDistributor()
    {
        random_ = new Random();
        rhythmGenerator_ = new RhythmGenerator();
        triadSequenceGenerator_ = ITriadSequenceGenerator.getTriadSequenceGenerator();
        previousTriad_ = null;

    }

    private boolean validate(List<RhythmGenerator.Rhythm> rhythmPatterns, Triad[] triads)
    {
        int rhythmPatternsCounter = 0;
        for (RhythmGenerator.Rhythm rhythm : rhythmPatterns)
        {
            rhythmPatternsCounter += rhythm.getLength();
        }
        int noteCounter = triads.length * Triad.TRIAD_SIZE;

        return rhythmPatternsCounter == noteCounter;
    }

    @Override
    public double[][] getLastNotes()
    {
        return getNotesImpl(triadSequenceGenerator_.generateLast(previousTriad_));
    }

    @Override
    public double[][] getNotes()
    {
        return getNotesImpl(triadSequenceGenerator_.generateNext(previousTriad_));
    }

    @Override
    public double getFrequency(int degree)
    {
        return NoteGenerator.getFrequency(degree);
    }

    private double[][] getNotesImpl(Triad[] triads)
    {
        int sequenceSize = triads.length;
        List<List<Integer>> availableRhythmLengths = availableRhythmsByNoteNumber_.get(sequenceSize);
        if (availableRhythmLengths == null)
        {
            availableRhythmLengths = findCombinations(rhythmGenerator_.getAvailableRhythmLengths(), sequenceSize * Triad.TRIAD_SIZE);
            availableRhythmsByNoteNumber_.put(sequenceSize, availableRhythmLengths);
        }

        int availableRhythmLengthsIndex = random_.nextInt(availableRhythmLengths.size());
        List<Integer> rhythmsLengths = availableRhythmLengths.get(availableRhythmLengthsIndex);

        List<RhythmGenerator.Rhythm> rhythmPatterns = rhythmGenerator_.getRhythmPatterns(rhythmsLengths);
        List<double[]> notes = new ArrayList<>();

        List<Double> scaleDegrees = Stream.of(triads)
                .flatMap(triad -> triad.getScaleDegrees().stream())
                .collect(Collectors.toList());
        Iterator<Double> scaleDegreesIterator = scaleDegrees.iterator();
        if (validate(rhythmPatterns, triads))
        {
            rhythmPatterns.stream()
                    .map(RhythmGenerator.Rhythm::getPattern)
                    .flatMapToInt(Arrays::stream)
                    .forEach(duration -> notes.add(new double[]{
                            duration > 0
                                    ? Constants.OneTone_
                                    ? 1
                                    : scaleDegreesIterator.next()
                                    : 0, Constants.BeatDurationMs_}));
        }
        else
        {
            throw new IllegalStateException(String.format("Calculated triad sequence [%s] is not compatible with rhythmPatterns [%s]", triads, rhythmPatterns));
        }

        previousTriad_ = triads[triads.length - 1];
        return notes.toArray(new double[0][]);
    }

    static List<List<Integer>> findCombinations(Set<Integer> numbers, int target)
    {
        List<List<Integer>> result = new ArrayList<>();
        findCombinationsRecursive(numbers, target, new ArrayList<>(), result);
        return result;
    }

    private static void findCombinationsRecursive(Set<Integer> numbers, int target, List<Integer> currentCombination, List<List<Integer>> result)
    {
        if (target == 0)
        {
            result.add(new ArrayList<>(currentCombination));
            return;
        }

        if (target < 0)
        {
            return;
        }

        for (Integer number : numbers)
        {
            currentCombination.add(number);
            findCombinationsRecursive(numbers, target - number, currentCombination, result);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

}
