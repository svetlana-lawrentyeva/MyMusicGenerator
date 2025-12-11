package ivko.lana.neurotone.wave_generator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a melody defined in YAML configuration.
 */
public class ConfiguredMelody
{
    private String name;
    private Double beatDurationMs;
    private boolean loop;
    private List<ConfiguredNote> notes = new ArrayList<>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Double getBeatDurationMs()
    {
        return beatDurationMs;
    }

    public void setBeatDurationMs(Double beatDurationMs)
    {
        this.beatDurationMs = beatDurationMs;
    }

    public boolean isLoop()
    {
        return loop;
    }

    public void setLoop(boolean loop)
    {
        this.loop = loop;
    }

    public List<ConfiguredNote> getNotes()
    {
        return notes;
    }

    public void setNotes(List<ConfiguredNote> notes)
    {
        this.notes = notes;
    }
}
