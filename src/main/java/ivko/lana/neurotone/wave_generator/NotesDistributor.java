package ivko.lana.neurotone.wave_generator;

import ivko.lana.neurotone.processing.Constants;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lana Ivko
 */
public class NotesDistributor
{
    private TriadSequenceGenerator triadSequenceGenerator_;
    private TriadSequenceGenerator.TriadSequence previousTriadSequence_;
    private Random random_;

    private RhythmGenerator rhythmGenerator_;
    private Map<Integer, List<List<Integer>>> availableRhythmsByNoteNumber_ = new HashMap<>();

    NotesDistributor()
    {
        random_ = new Random();
        rhythmGenerator_ = new RhythmGenerator();
        triadSequenceGenerator_ = new TriadSequenceGenerator();
        previousTriadSequence_ = null;

    }

    private boolean validate(List<RhythmGenerator.Rhythm> rhythmPatterns, TriadSequenceGenerator.TriadSequence currentSequence)
    {
        int rhythmPatternsCounter = 0;
        for (RhythmGenerator.Rhythm rhythm : rhythmPatterns)
        {
            rhythmPatternsCounter += rhythm.length_;
        }
        int noteCounter = currentSequence.getSize() * Triad.TRIAD_SIZE;

        return rhythmPatternsCounter == noteCounter;
    }

    public double[][] getLastNotes()
    {
        double[][] notes = getNotesImpl(triadSequenceGenerator_.generateLast(previousTriadSequence_));
//        if (notes.length >= 3)
//        {
//            double[] tmp = notes[notes.length - 3];
//            notes[notes.length - 3] = notes[notes.length -1];
//            notes[notes.length - 1] = tmp;
//        }
        return notes;
    }

    public double[][] getNotes()
    {
        return getNotesImpl(triadSequenceGenerator_.generateNext(previousTriadSequence_));
    }

    private double[][] getNotesImpl(TriadSequenceGenerator.TriadSequence currentSequence)
    {
        int sequenceSize = currentSequence.getSize();
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

        List<Double> frequences = Stream.of(currentSequence.getSequences())
                .flatMap(triad -> triad.getFrequencySequence().stream())
                .collect(Collectors.toList());
        Iterator<Double> frequencesIterator = frequences.iterator();
        if (validate(rhythmPatterns, currentSequence))
        {
            rhythmPatterns.stream()
                    .map(rhythm -> rhythm.pattern_)
                    .flatMapToInt(Arrays::stream)
                    .forEach(duration -> notes.add(new double[]{duration > 0 ? frequencesIterator.next() : 0, Constants.BEAT_DURATION_MS}));
        }
        else
        {
            throw new IllegalStateException(String.format("Calculated triad sequence [%s] is not compatible with rhythmPatterns [%s]", currentSequence, rhythmPatterns));
        }

        previousTriadSequence_ = currentSequence;
        return notes.toArray(new double[0][]);
    }

    public static List<List<Integer>> findCombinations(Set<Integer> numbers, int target)
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

    public static void main(String[] args)
    {
        Set<Integer> numbers = new HashSet<>(Arrays.asList(2, 3, 4));
        int[] targets = {6, 9, 12};

        List<List<List<Integer>>> allCombinations = new ArrayList<>();

        for (int target : targets)
        {
            List<List<Integer>> combinations = findCombinations(numbers, target);
            allCombinations.add(combinations);
        }

        // Вывод результата
        for (int i = 0; i < targets.length; i++)
        {
            System.out.println("Combinations for sum " + targets[i] + ": " + allCombinations.get(i));
        }
    }
}
