package ivko.lana.neurotone.wave_generator.sounds;

/**
 * @author Lana Ivko
 */
public interface ISamplesCreator
{
    short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, double phaseMultiplier, int overtoneIndex);
}
