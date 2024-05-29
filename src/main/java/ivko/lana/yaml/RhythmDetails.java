package ivko.lana.yaml;

import ivko.lana.musicentities.MusicType;

import java.util.List;

/**
 * @author Lana Ivko
 */
public class RhythmDetails
{
    private int baseDuration;
    private List<DurationProbability> durations;
    private List<Integer> accents;
    private List<Integer> relaxDrum;
    private List<Integer> epicDrum;
    private int baseDurationMultiplier;
    private String name_;


    public RhythmDetails() {
    }

    public RhythmDetails(int baseDuration, List<DurationProbability> durations, List<Integer> accents, List<Integer> relaxDrum, List<Integer> epicDrum) {
        this.baseDuration = baseDuration;
        this.durations = durations;
        this.accents = accents;
        this.relaxDrum = relaxDrum;
        this.epicDrum = epicDrum;
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

    public List<Integer> getDrums(MusicType musicType)
    {
        return musicType == MusicType.EPIC
                ? epicDrum
                : relaxDrum;
    }

    public void setAccents(List<Integer> accents) {
        this.accents = accents;
    }

    public void setRelaxDrum(List<Integer> relaxDrum) {
        this.relaxDrum = relaxDrum;
    }

    public void setEpicDrum(List<Integer> epicDrum) {
        this.epicDrum = epicDrum;
    }

    public void setBaseDurationMultiplier(int baseDurationMultiplier)
    {
        this.baseDurationMultiplier = baseDurationMultiplier;
    }

    public int getBaseDurationMultiplier()
    {
        return baseDurationMultiplier;
    }

    @Override
    public String toString()
    {
        return "RhythmDetails{" +
                "name=" + name_ +
                ", baseDuration=" + baseDuration +
                ", baseDurationMultiplier=" + baseDurationMultiplier +
                '}';
    }

    public void setName(String name)
    {
        name_ = name;
    }

    public String getName()
    {
        return name_;
    }
}
