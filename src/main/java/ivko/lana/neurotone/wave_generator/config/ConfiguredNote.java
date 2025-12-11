package ivko.lana.neurotone.wave_generator.config;

/**
 * Describes a single note in a configured melody.
 */
public class ConfiguredNote
{
    private int degree;
    private int beats;
    private boolean pause;

    public int getDegree()
    {
        return degree;
    }

    public void setDegree(int degree)
    {
        this.degree = degree;
    }

    public int getBeats()
    {
        return beats;
    }

    public void setBeats(int beats)
    {
        this.beats = beats;
    }

    public boolean isPause()
    {
        return pause;
    }

    public void setPause(boolean pause)
    {
        this.pause = pause;
    }
}
