package ivko.lana.neurotone.wave_generator;

/**
 * @author Lana Ivko
 */
public class WaveDetail
{
    private double[] frequencies_;
    private double[] durations_;
    private short[] samples_;

    public WaveDetail() {}

    public WaveDetail(double[] frequencies, double[] durations, short[] samples)
    {
        frequencies_ = frequencies;
        durations_ = durations;
        samples_ = samples;
    }

    public double[] getFrequencies()
    {
        return frequencies_;
    }

    public double[] getDurations()
    {
        return durations_;
    }

    public void setSamples(short[] samples)
    {
        samples_ = samples;
    }

    public short[] getSamples()
    {
        return samples_;
    }
}
