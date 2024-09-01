package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.util.ShiftFactor;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.sounds.IOvertoneHelper;
import ivko.lana.neurotone.wave_generator.sounds.SoundType;

/**
 * @author Lana Ivko
 */
public class TibetanHitSound
{
    private short[] samples_;

    public TibetanHitSound(SoundType soundType, double baseFrequency)
    {
        IOvertoneHelper overtoneHelper = soundType.getOvertoneHelper();
        ShiftFactor[] shiftFactors = overtoneHelper.getHitShiftFactors();

        TibetanHitSamplesCreator hitSamplesCreator = new TibetanHitSamplesCreator();
        for (int i = 0; i < shiftFactors.length; ++i)
        {
            ShiftFactor shiftFactor = shiftFactors[i];
            double overtoneFrequency = shiftFactor.calculate(baseFrequency);
            double amplitude = shiftFactor.getAmplitude();
            if (samples_ == null)
            {
                samples_ = hitSamplesCreator.createHitSamples(overtoneFrequency, amplitude);
            }
            else
            {
                samples_ = Util.combineSamples(samples_, hitSamplesCreator.createHitSamples(overtoneFrequency, amplitude));
            }
        }
    }

    public short[] getSamples()
    {
        return samples_;
    }
}
