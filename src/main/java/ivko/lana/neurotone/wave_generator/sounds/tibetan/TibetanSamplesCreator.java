package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.SoundsCache;
import ivko.lana.neurotone.wave_generator.sounds.Sound;
import ivko.lana.neurotone.wave_generator.sounds.SoundType;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleClearSamplesCreator;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleSamplesCreator;
import org.bytedeco.libfreenect._freenect_context;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class TibetanSamplesCreator extends SimpleSamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(TibetanSamplesCreator.class.getName());

    private static final double LEFT_MULTIPLIER = 1;
    private static final double RIGHT_MULTIPLIER = -1;

    private int repeatFrequencyCounter_ = 1;
    private double previousFrequency_ = -1;

    public TibetanSamplesCreator()
    {
        super();
    }

    protected SimpleClearSamplesCreator createClearSamplesCreator()
    {
        return new TibetanClearSamplesCreator();
    }

    protected TibetanClearSamplesCreator getClearSamplesCreator()
    {
        return (TibetanClearSamplesCreator) super.getClearSamplesCreator();
    }

    public short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, double phaseMultiplier, int overtoneIndex)
    {
        amplitude *= 0.4;
        double multiplier = (isLeft ? LEFT_MULTIPLIER : RIGHT_MULTIPLIER) * phaseMultiplier ;

        getClearSamplesCreator().setDurationFadeInFactor(1);
        getClearSamplesCreator().setDurationFadeOutFactor(1);

        double basePulsationSpeed = Constants.BASE_PULSATION_SPEED;

        // Время одной выборки
        double time = 1.0 / Constants.SAMPLE_RATE; // Время на один семпл

        // Вычисляем пульсацию
        double pulsation = Constants.PulsationSpeedFactor_ * (basePulsationSpeed * multiplier) * time * 0.5;
        pulsation /= repeatFrequencyCounter_ * 0.25;
        amplitude *= repeatFrequencyCounter_ * 0.25;

        if (frequency == previousFrequency_)
        {
            repeatFrequencyCounter_++;
        }
        else
        {
            repeatFrequencyCounter_ = 1;
        }
        previousFrequency_ = frequency;

        // Определяем угловую скорость пульсации
        double angularFrequency = 2 * Math.PI * pulsation;

        // Вычисляем фазовые сдвиги
        double phaseShift1 = 0;  // без сдвига
        double phaseShift2 = angularFrequency / 3; // сдвиг на одну треть пульсации
        double phaseShift3 = 2 * angularFrequency / 3; // сдвиг на две трети пульсации

        short[] baseSignal = getClearSamplesCreator().createClearSamples(durationMs, frequency, amplitude, phaseShift1);
        baseSignal = applyEffects(baseSignal, pulsation, multiplier);

        short[] baseSignal2 = getClearSamplesCreator().createClearSamples(durationMs, frequency, amplitude, phaseShift2);
        baseSignal2 = applyEffects(baseSignal2, Constants.BASE_PULSATION_SPEED, multiplier);

        short[] baseSignal3 = getClearSamplesCreator().createClearSamples(durationMs, frequency, amplitude, phaseShift3);
        baseSignal3 = applyEffects(baseSignal3, Constants.BASE_PULSATION_SPEED, multiplier);

        baseSignal = Util.combineSamples(baseSignal, baseSignal2);
        baseSignal = Util.combineSamples(baseSignal, baseSignal3);

        baseSignal = applyEffects(baseSignal, Constants.BASE_PULSATION_SPEED, multiplier);
//        baseSignal = applyVibrationEffect(baseSignal, 1 / multiplier);
        getClearSamplesCreator().setDurationFadeInFactor(1);
        getClearSamplesCreator().setDurationFadeOutFactor(0.5 + 0.125 * overtoneIndex);
        short[] phoneSignal = getClearSamplesCreator().createClearSamples(durationMs - (overtoneIndex * 250), frequency, amplitude * 0.3);
//        phoneSignal = applyVibrationEffect(phoneSignal, 0.0015);
        baseSignal = Util.combineSamples(baseSignal, phoneSignal);

        if (overtoneIndex == 0)
        {
            short[] hitSignal = prepareHitSignal(frequency, amplitude);
            baseSignal = Util.combineSamples(baseSignal, hitSignal);
        }
        return baseSignal;
    }

    private short[] prepareHitSignal(double frequency, double amplitude)
    {
        double durationFadeInFactor = 0.03;;
        double durationFadeOutFactor = 0.25;
        int durationHitMs = (int) (Constants.HIT_DURATION_MS * durationFadeInFactor);
        durationHitMs = (int) Math.max(durationHitMs, Constants.PAUSE_DURATION_MS + Constants.SMALL_AMPLITUDES_DURATION_MS + durationHitMs);
        getClearSamplesCreator().setDurationFadeInFactor(durationFadeInFactor);
        getClearSamplesCreator().setDurationFadeOutFactor(durationFadeOutFactor);
        short[] baseSignalForHit = getClearSamplesCreator().createClearSamples(durationHitMs, frequency, amplitude * 0.5);
        short[] hitSignal = getHit(frequency);
        hitSignal = Util.combineSamples(hitSignal, baseSignalForHit);
        return hitSignal;
    }

    private static double getVibrationFactor(double frequency)
    {
        while (frequency > 1)
        {
            frequency /= 10;
        }
        return frequency * Constants.VibrationFactor_;
    }

    private short[] shiftSignal(short[] signal, int shift)
    {
        short[] result = new short[signal.length + shift];
        System.arraycopy(signal, 0, result, shift, signal.length);
        return result;
    }

    private short[] getHit(double frequency)
    {
        TibetanHitSound tibetanHitSound = TibetanHitSoundLibrary.getInstance().getHitSound(SoundType.TIBETAN, frequency);
        return tibetanHitSound.getSamples();
    }

    private short[] applyVibrationEffect(short[] samples, double amount)
    {
        int totalSamples = samples.length;
        short[] vibratedSamples = new short[totalSamples];

        double angleIncrement = 2.0 * Math.PI * Constants.VibrationFactor_ / Constants.SAMPLE_RATE;

        for (int i = 0; i < totalSamples; i++)
        {
            double vibration = 1.0 + amount * Math.sin(i * angleIncrement);

            // Применяем вибрацию к каждому семплу
            vibratedSamples[i] = (short) (samples[i] * vibration);

            // Ограничение значений
            vibratedSamples[i] = Util.getLimitedValue(vibratedSamples[i]);
        }

        return vibratedSamples;
    }

    private short[] applyEffects(short[] samples, double basePulsationSpeed, double multiplier)
    {
        int totalSamples = samples.length;
        short[] channel = new short[totalSamples];

        for (int i = 0; i < totalSamples; i++)
        {
            double panning = createPanning(basePulsationSpeed, multiplier, i);

            // Усиливаем влияние пульсации на переходах
            double enhancedPanning = 0.5 * (1.0 - Math.sin(Math.PI * panning)); // Используем синус для сглаживания

            // Дополнительно усиливаем амплитуду в точках перехода
            double transitionEnhancement = 1.0 + 0.2 * Math.abs(Math.sin(Math.PI * panning)); // Увеличение на 20% в точках перехода

            // Применение сглаженной пульсации с утолщением переходов
            channel[i] = (short) (samples[i] * (1.0 - enhancedPanning) * transitionEnhancement);

            // Ограничение значений
            channel[i] = Util.getLimitedValue(channel[i]);
        }

        return channel;
    }

    private static double createPanning(double basePulsationSpeed, double multiplier, int i)
    {
        double sineValue = getSineValue(basePulsationSpeed, multiplier, i);

        // Применяем сглаживание только к области перехода
        double smoothingFactor = 0.8; // Чем выше значение, тем более плавный переход
        double smoothTransition = Math.signum(sineValue) * Math.pow(Math.abs(sineValue), smoothingFactor);

        // Применение сглаженной пульсации
//                double panning = 0.7 - 0.9 * TibetanConstants.PulsationDepth_ * Math.abs(smoothTransition);
        double panning = 1 - 1 * Constants.PulsationDepth_ * smoothTransition;
        return panning;
    }

    private static double getSineValue(double basePulsationSpeed, double multiplier, int i)
    {
        double time = i / Constants.SAMPLE_RATE; // Текущее время в секундах

        // Скорость перехода для каналов
        double pulsation = Constants.PulsationSpeedFactor_ * (basePulsationSpeed * multiplier) * time * 0.5;

        // Управление пульсацией для канала (панорамирование)
//                double shift = multiplier > 0 ? Math.PI / 2 : 0.0;
        double shift = (Math.PI / 2) / multiplier;

        // Оставляем синус для "сосисочности"
        double sineValue = Math.sin(Math.PI * pulsation - shift);
        return sineValue;
    }
}