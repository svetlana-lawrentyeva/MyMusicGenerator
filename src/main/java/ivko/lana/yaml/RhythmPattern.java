package ivko.lana.yaml;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class RhythmPattern {
    public static final int RHYTHM_NUMBER = 100;

    private int baseDuration;
    private List<DurationProbability> durations;
    private List<Integer> accents;



    public RhythmPattern() {
    }

    public RhythmPattern(int baseDuration, List<DurationProbability> durations, List<Integer> accents) {
        this.baseDuration = baseDuration;
        this.durations = durations;
        this.accents = accents;
    }

    public int getBaseDuration() {
        return baseDuration;
    }

    public void setBaseDuration(int baseDuration) {
        this.baseDuration = baseDuration;
    }

    public List<DurationProbability> getDurations() {
        return durations;
    }

    public void setDurations(List<DurationProbability> durations) {
        this.durations = durations;
    }

    public List<Integer> getAccents() {
        return accents;
    }

    public void setAccents(List<Integer> accents) {
        this.accents = accents;
    }
}
