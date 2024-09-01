package ivko.lana.neurotone.wave_generator;

import java.util.*;

public class RhythmGenerator
{
    public static class Rhythm
    {
        int[] pattern_;
        double probability_;
        int length_;

        public Rhythm(int[] pattern, double probability)
        {
            pattern_ = pattern;
            probability_ = probability;
            length_ = 0;

            for (int num : pattern_)
            {
                length_ += num;
            }
        }

        @Override
        public String toString()
        {
            return "Rhythm{" +
                    "pattern_=" + Arrays.toString(pattern_) +
                    ", length_=" + length_ +
                    '}';
        }
    }

    private static final List<Rhythm> rhythms_ = new ArrayList<>();
    private static final Random random = new Random();
    private static Map<Integer, List<Rhythm>> AvailableRhythmsByLength_ = new HashMap<>();

//    static
//    {
//        rhythms_.add(new Rhythm(new int[]{1, 3}, 20));
//        rhythms_.add(new Rhythm(new int[]{3, 1}, 28));
//        rhythms_.add(new Rhythm(new int[]{4}, 38));
//        rhythms_.add(new Rhythm(new int[]{1, 1, 2}, 40));
//        rhythms_.add(new Rhythm(new int[]{1, 2, 1}, 65));
//        rhythms_.add(new Rhythm(new int[]{1, 1, 1, 1}, 68));
//        rhythms_.add(new Rhythm(new int[]{2, 1, 1}, 72));
//        rhythms_.add(new Rhythm(new int[]{2, 2}, 77));
//    }

    static
    {
        rhythms_.add(new Rhythm(new int[]{1, 1, 0, 0}, 20));
        rhythms_.add(new Rhythm(new int[]{1, 0, 0, 1}, 28));
        rhythms_.add(new Rhythm(new int[]{1, 1, 1, 0}, 40));
        rhythms_.add(new Rhythm(new int[]{1, 1, 0, 1}, 65));
        rhythms_.add(new Rhythm(new int[]{1, 1, 1, 1}, 68));
        rhythms_.add(new Rhythm(new int[]{1, 0, 1, 1}, 72));
        rhythms_.add(new Rhythm(new int[]{1, 0, 1, 0}, 77));
    }

    private List<Rhythm> patterns_;
    private Iterator<Rhythm> iterator_;

    public RhythmGenerator()
    {
        patterns_ = collectRhythmPatterns();
        iterator_ = patterns_.iterator();
    }

    public Set<Integer> getAvailableRhythmLengths()
    {
        return AvailableRhythmsByLength_.keySet();
    }

    public Rhythm getNext()
    {
       if (!iterator_.hasNext())
       {
           iterator_ = patterns_.iterator();
       }
        return iterator_.next();
    }

    private Rhythm getNextRhythm(List<Rhythm> rhythms)
    {
        // Приведение вероятностей к диапазону 0-1
        double totalProbability = 0;
        for (Rhythm rhythm : rhythms)
        {
            totalProbability += rhythm.probability_;
        }

        // Генерация случайного числа для выбора ритма
        double randomValue = random.nextDouble() * totalProbability;
        double cumulativeProbability = 0.0;

        for (Rhythm rhythm : rhythms)
        {
            cumulativeProbability += rhythm.probability_;
            if (randomValue <= cumulativeProbability)
            {
                return rhythm;
            }
        }

        // На случай, если что-то пошло не так, возвращаем последний ритм
        return rhythms.get(rhythms.size() - 1);
    }

    public List<Rhythm> getRhythmPatterns(List<Integer> rhythmLengths)
    {
        List<Rhythm> rhythms = new ArrayList<>();
        for (Integer rhythmLength : rhythmLengths)
        {
            List<Rhythm> availableRhythms = AvailableRhythmsByLength_.get(rhythmLength);
            Rhythm nextRhythm = getNextRhythm(availableRhythms);
            rhythms.add(nextRhythm);
        }
        return rhythms;
    }
    public List<Rhythm> collectRhythmPatterns()
    {
        List<Rhythm> patterns = new ArrayList<>();
        for (Rhythm rhythm : rhythms_)
        {
            patterns.add(rhythm);
            List<Rhythm> rhythms = AvailableRhythmsByLength_.computeIfAbsent(rhythm.length_, k -> new ArrayList<>());
            rhythms.add(rhythm);
        }
        return patterns;
    }
}

