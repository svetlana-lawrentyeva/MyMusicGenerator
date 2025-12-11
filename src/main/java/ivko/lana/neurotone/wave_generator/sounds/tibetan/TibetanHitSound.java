package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.util.IShiftFactor;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;
import ivko.lana.neurotone.wave_generator.sounds.SoundType;

import java.util.Random;

/**
 * @author Lana Ivko
 */
public class TibetanHitSound
{
    private short[] samples_;

    public TibetanHitSound(SoundType soundType, double baseFrequency)
    {
        IOvertoneHelper overtoneHelper = soundType.getOvertoneHelper();
        IShiftFactor[] shiftFactors = overtoneHelper.getHitShiftFactors();

        TibetanHitSamplesCreator hitSamplesCreator = new TibetanHitSamplesCreator();
        for (int i = 0; i < shiftFactors.length; ++i)
        {
            createSoundLayer(baseFrequency, shiftFactors[i], hitSamplesCreator, 1);
//            createSoundLayer(baseFrequency, shiftFactors[i], hitSamplesCreator, 0.01);
//            createSoundLayer(baseFrequency, shiftFactors[i], hitSamplesCreator, 0.015);
//            createSoundLayer(baseFrequency, shiftFactors[i], hitSamplesCreator, 0.02);
//            createSoundLayer(baseFrequency, shiftFactors[i], hitSamplesCreator, 0.03);
        }
    }

    private void createSoundLayer(double baseFrequency, IShiftFactor shiftFactor, TibetanHitSamplesCreator hitSamplesCreator, double overtoneMultiplier)
    {
        double overtoneFrequency = shiftFactor.calculate(baseFrequency);
        double amplitude = shiftFactor.getAmplitude();

        double frequency = overtoneFrequency / 10;
        short[] hitSamples = hitSamplesCreator.createHitSamples(frequency + frequency * overtoneMultiplier, amplitude * 3 * overtoneMultiplier);
        samples_ = samples_ == null ? hitSamples : Util.combineSamples(samples_, hitSamples);
    }

    public short[] getSamples()
    {
        return samples_;
    }
}
