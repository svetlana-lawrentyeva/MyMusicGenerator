package ivko.lana.generators;

import ivko.lana.musicentities.RhythmType;
import ivko.lana.util.MusicUtil;
import ivko.lana.yaml.DurationProbability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class RhythmPattern
{
    private static Map<Integer, Double> NoteDividerToProbability_ = new HashMap<>();

    static
    {
        NoteDividerToProbability_.put(2, 0.1);
        NoteDividerToProbability_.put(4, 0.4);
        NoteDividerToProbability_.put(8, 0.3);
        NoteDividerToProbability_.put(16, 0.15);
        NoteDividerToProbability_.put(32, 0.05);
    }

    private List<Integer> pattern_;

    private int baseNoteDuration_;
    private int baseNoteCounter_;
    private RhythmType rhythmType_;

    public RhythmPattern(List<DurationProbability> durations, int baseNoteDuration, int baseNoteCounter, RhythmType rhythmType)
    {
        baseNoteDuration_ = baseNoteDuration;
        baseNoteCounter_ = baseNoteCounter;
        rhythmType_ = rhythmType;
        if (durations != null && !durations.isEmpty())
        {
            NoteDividerToProbability_.clear();
            durations.forEach(duration -> NoteDividerToProbability_.put(duration.getDuration(), duration.getProbability()));
        }
        pattern_ = generatePattern();
    }

    public RhythmType getRhythmType()
    {
        return rhythmType_;
    }

    private List<Integer> generatePattern()
    {
        List<Integer> pattern = new ArrayList<>();
        int totalLength = baseNoteCounter_ * baseNoteDuration_;
        int generatedLength = 0;
        while (generatedLength < totalLength)
        {
            int value = MusicUtil.getInstance().generateConsiderProbability(NoteDividerToProbability_);
            value = Math.min(value, totalLength - generatedLength);
            pattern.add(value);
            generatedLength += value;
        }
        double endPauseProbability = rhythmType_.getPauseProbability();
        if (endPauseProbability > 0)
        {
            Map<Integer, Double> pauseProbability = new HashMap<>();
            pauseProbability.put(0, endPauseProbability);
            pauseProbability.put(1, 1 - endPauseProbability);

            int value = MusicUtil.getInstance().generateConsiderProbability(pauseProbability);
            if (value == 0)
            {
                Integer lastValue = pattern.get(pattern.size() - 1);
                pattern.set(pattern.size() - 1, -lastValue);
            }
        }
        return pattern;
    }

    public List<Integer> getPattern()
    {
        return pattern_;
    }

}
