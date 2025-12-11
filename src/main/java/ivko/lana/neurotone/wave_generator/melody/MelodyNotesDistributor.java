package ivko.lana.neurotone.wave_generator.melody;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.INotesDistributor;
import ivko.lana.neurotone.wave_generator.RhythmGenerator;

import java.util.*;

/**
 * @author Lana Ivko
 */
public class MelodyNotesDistributor implements INotesDistributor
{
    private ITriadSequenceGenerator triadSequenceGenerator_;
    private Triad previousTriad_;
    private Random random_;
    private Triad[] currentTriads_;

    private RhythmGenerator rhythmGenerator_;
    private Map<Integer, List<List<Integer>>> availableRhythmsByNoteNumber_ = new HashMap<>();

    public MelodyNotesDistributor()
    {
        random_ = new Random();
        rhythmGenerator_ = new RhythmGenerator();
        triadSequenceGenerator_ = ITriadSequenceGenerator.getTriadSequenceGenerator();
        previousTriad_ = null;
    }

    @Override
    public String generateFileName()
    {
        return Constants.BaseFrequency_ + ".wav";
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
    public double[][] getNotesForLeftChannel()
    {
        currentTriads_ = triadSequenceGenerator_.generateNext(previousTriad_);
        return getNotesImpl(currentTriads_);
    }

    @Override
    public double[][] getNotesForRightChannel()
    {
        return getNotesImpl(currentTriads_);
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

        List<Double> scaleDegrees = buildSmoothScaleDegrees(triads);
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
        List<double[]> result = notes;
        if (!Constants.UsePause_)
        {
            result = removePauses(result);
        }
        return result.toArray(new double[0][]);
    }

    private List<Double> buildSmoothScaleDegrees(Triad[] triads)
    {
        List<Double> result = new ArrayList<>();
        Double previousNote = null;

        for (Triad triad : triads)
        {
            List<Double> triadDegrees = new ArrayList<>(triad.getScaleDegrees());
            List<Double> orderedTriad = orderTriadDegrees(triadDegrees, previousNote);
            result.addAll(orderedTriad);
            previousNote = orderedTriad.get(orderedTriad.size() - 1);
        }

        return result;
    }

    private List<Double> orderTriadDegrees(List<Double> triadDegrees, Double previousNote)
    {
        List<Double> ordered = new ArrayList<>();
        if (previousNote == null)
        {
            triadDegrees.sort(Comparator.naturalOrder());
            Double startNote = triadDegrees.get(1);
            ordered.add(startNote);
            triadDegrees.remove(startNote);
        }
        else
        {
            Double closestToPrevious = triadDegrees.stream()
                    .min(Comparator.comparingDouble(note -> scaleDistance(previousNote, note)))
                    .orElse(triadDegrees.get(0));
            ordered.add(closestToPrevious);
            triadDegrees.remove(closestToPrevious);
        }

        Double lastAdded = ordered.get(ordered.size() - 1);
        triadDegrees.sort(Comparator.comparingDouble(note -> scaleDistance(lastAdded, note)));
        ordered.addAll(triadDegrees);

        return ordered;
    }

    private double scaleDistance(double from, double to)
    {
        double distance = Math.abs(from - to);
        return Math.min(distance, 7 - distance);
    }

    private List<double[]> removePauses(List<double[]> notes)
    {
        List<double[]> result = new ArrayList<>();
        double accumulator = 0;
        for (int i = notes.size() - 1; i >= 0; --i)
        {
            double[] currentNote = notes.get(i);
            if (currentNote[0] == 0)
            {
                accumulator += currentNote[1];
            }
            else
            {
                result.add(new double[] {currentNote[0], currentNote[1] + accumulator});
                accumulator = 0;
            }
        }
        return result;
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
