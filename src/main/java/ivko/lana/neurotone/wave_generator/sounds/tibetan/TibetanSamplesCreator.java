package ivko.lana.neurotone.wave_generator.sounds.tibetan;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.SoundsCache;
import ivko.lana.neurotone.wave_generator.SoundsLibrary;
import ivko.lana.neurotone.wave_generator.sounds.Sound;
import ivko.lana.neurotone.wave_generator.sounds.simple.SimpleSamplesCreator;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class TibetanSamplesCreator extends SimpleSamplesCreator
{
    private static final Logger logger = CustomLogger.getLogger(TibetanSamplesCreator.class.getName());

    private static final double LEFT_MULTIPLIER = 9;
    private static final int RIGHT_MULTIPLIER = -7;

    private double angle_;

    public TibetanSamplesCreator()
    {
        super();
    }

    public short[] createSamples(int durationMs, double frequency, double amplitude, boolean isLeft, boolean isBaseSound)
    {
        return durationMs == 0
                ? createHitSamples(frequency, amplitude)
                : createFullSamples(durationMs, frequency, amplitude, isLeft, isBaseSound);
    }

    private short[] createFullSamples(int durationMs, double frequency, double amplitude, boolean isLeft, boolean isBaseSound)
    {
        double multiplier = isLeft ? LEFT_MULTIPLIER : RIGHT_MULTIPLIER;

        short[] baseSignal = createClearSamples(durationMs, frequency, amplitude, 1, 1);
//        baseSignal = applyEffects(baseSignal, Constants.BASE_PULSATION_SPEED, multiplier);
        double vibrationFactor = getVibrationFactor(frequency);
        baseSignal = applyVibrationEffect(baseSignal, 1.5, vibrationFactor * (1 / multiplier));
        short[] phoneSignal = createClearSamples(durationMs, frequency*2, amplitude / 3, 1, 0.5);
        phoneSignal = applyVibrationEffect(phoneSignal, 1.5, 0.0015);
        phoneSignal = applyEffects(phoneSignal, Constants.BASE_PULSATION_SPEED, multiplier);
        baseSignal = combineSamples(baseSignal, phoneSignal);

        if (isBaseSound)
        {
            short[] hitSignal = prepareHitSignal(frequency, amplitude, multiplier);
            baseSignal = combineSamples(baseSignal, hitSignal);
        }
        return baseSignal;
    }

    private short[] prepareHitSignal(double frequency, double amplitude, double multiplier)
    {
        double durationFadeInFactor = 1;
        double durationFadeOutFactor = 0.2;
        int durationHitMs = (int) (Constants.HIT_DURATION_MS * durationFadeInFactor);
        durationHitMs = (int) Math.max(durationHitMs, Constants.PAUSE_DURATION_MS + Constants.SMALL_AMPLITUDES_DURATION_MS + Constants.HIT_DURATION_MS * durationFadeInFactor);
        short[] baseSignalForHit = createClearSamples(durationHitMs, frequency, amplitude * 3, durationFadeInFactor, durationFadeOutFactor);
        baseSignalForHit = applyEffects(baseSignalForHit, Constants.BASE_PULSATION_SPEED, multiplier);

        short[] hitSignal = getHit(frequency);
        hitSignal = combineSamples(hitSignal, baseSignalForHit);
        return hitSignal;
    }

    private static double getVibrationFactor(double frequency)
    {
        while (frequency > 1)
        {
            frequency /= 10;
        }
        return frequency;
    }

    private short[] shiftSignal(short[] signal, int shift)
    {
        short[] result = new short[signal.length + shift];
        System.arraycopy(signal, 0, result, shift, signal.length);
        return result;
    }

    private short[] createClearSamples(int durationMs, double frequency, double amplitude, double durationFadeInFactor, double durationFadeOutFactor)
    {
        int pauseLength = Util.convertMsToSampleLength(Constants.PAUSE_DURATION_MS);
        int smallAmplitudesLength = Util.convertMsToSampleLength(Constants.SMALL_AMPLITUDES_DURATION_MS);
        int introSamplesLength = pauseLength + smallAmplitudesLength;
        int fadeInSamplesLength = Util.convertMsToSampleLength((int) (Constants.FADE_IN_DURATION_MS * durationFadeInFactor)) - introSamplesLength;
        int fadeOutSamplesLength = Util.convertMsToSampleLength((int) (Constants.FADE_OUT_DURATION_MS * durationFadeOutFactor));

        // Создаем интро
        short[] introWave = getIntro(frequency, amplitude, pauseLength, smallAmplitudesLength);
        // Создаем нарастание
        short[] fadeInWave = createFadeInWave(frequency, amplitude, fadeInSamplesLength);

        int constantSamplesLength = Util.convertMsToSampleLength(durationMs) - introSamplesLength - fadeInSamplesLength;

        if (constantSamplesLength < 0)
        {
            throw new IllegalArgumentException(
                    String.format("Суммарная длительность паузы (%s), малой амплитуды (%s) и нарастающей части (%s) [%s] превышает общую длительность ноты (%s).",
                            pauseLength,
                            smallAmplitudesLength,
                            fadeInSamplesLength,
                            pauseLength + smallAmplitudesLength + fadeInSamplesLength,
                            Util.convertMsToSampleLength(durationMs)
                    ));
        }
        // Создаем основную часть
        short[] constantWave = createConstantWave(frequency, constantSamplesLength, amplitude);
        // Создаем затухание
        short[] fadeOutWave = createFadeOutWave(frequency, amplitude, fadeOutSamplesLength, true);

        short[] combinedSignal = new short[introSamplesLength + fadeInSamplesLength + constantSamplesLength + fadeOutSamplesLength];

        System.arraycopy(introWave, 0, combinedSignal, 0, introSamplesLength);
        System.arraycopy(fadeInWave, 0, combinedSignal, introSamplesLength, fadeInSamplesLength);
        System.arraycopy(constantWave, 0, combinedSignal, introSamplesLength + fadeInSamplesLength, constantSamplesLength);
        System.arraycopy(fadeOutWave, 0, combinedSignal, introSamplesLength + fadeInSamplesLength + constantSamplesLength, fadeOutSamplesLength);
        return combinedSignal;
    }

    private static int getHitPosition()
    {
        return Util.convertMsToSampleLength(Constants.PAUSE_DURATION_MS) + Util.convertMsToSampleLength(Constants.SMALL_AMPLITUDES_DURATION_MS);
    }

    private short[] getHit(double frequency)
    {
        int startHitPosition = getHitPosition();
        // Создание ударного эффекта
        SoundsCache hitSoundsCache = SoundsLibrary.getInstance().getHitsCache(frequency);
        int harmonicSize = hitSoundsCache.getSize();
        short[][] hitHarmonics = new short[harmonicSize][];
        for (int i = 0; i < harmonicSize; ++i)
        {
            Sound sound = hitSoundsCache.getAt(i);
            hitHarmonics[i] = sound.getSamples();
        }
        short[] combinedHitHarmonics = new short[hitHarmonics[0].length + startHitPosition];

        for (int i = 0; i < hitHarmonics.length; ++i)
        {
            short[] hitHarmonic = hitHarmonics[i];
            for (int j = 0; j < hitHarmonic.length; ++j)
            {
                combinedHitHarmonics[j + startHitPosition] += hitHarmonic[j];
            }
        }
        return combinedHitHarmonics;
    }

    private short[] createHitSamples(double frequency, double amplitude)
    {
        double durationFadeInFactor = 0.5;
        double durationFadeOutFactor = 0.05;
        int duration = (int) (Constants.FADE_IN_DURATION_MS * durationFadeInFactor);
        duration = (int) Math.max(duration, Constants.PAUSE_DURATION_MS + Constants.SMALL_AMPLITUDES_DURATION_MS + Constants.FADE_IN_DURATION_MS * durationFadeInFactor);
        short[] hitSignal = createClearSamples(duration, frequency, amplitude, durationFadeInFactor, durationFadeOutFactor);

        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;
        double distortionAmount = 5.0; // Усиливаем искажения
        double phaseShift = Math.PI / 4; // Добавляем фазовый сдвиг

        double lowFreq = 0.5; // Низкочастотное колебание
        double lowFreqIncrement = 2.0 * Math.PI * lowFreq / Constants.SAMPLE_RATE;

        for (int i = 0; i < hitSignal.length; i++)
        {
            // Вычисляем искажение с использованием низкочастотного сигнала
            double lowFreqDistortion = Math.sin(i * lowFreqIncrement);
            double distortion = 1.0 + distortionAmount * Math.sin(i * angleIncrement + phaseShift) * lowFreqDistortion;

            // Применяем искажение к каждому семплу
            hitSignal[i] = (short) (hitSignal[i] * distortion);

            // Ограничение значений, чтобы не выйти за пределы допустимых значений для short
            hitSignal[i] = Util.getLimitedValue(hitSignal[i]);
        }
        return hitSignal;
    }

    public static double min = Double.MAX_VALUE;
    public static double max = 0;

    // short[] vibratedSamples = applyVibrationEffect(originalSamples, 5.0, 0.5);
    private short[] applyVibrationEffect(short[] samples, double frequency, double amount)
    {
        int totalSamples = samples.length;
        short[] vibratedSamples = new short[totalSamples];

        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;

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

            int abs = Math.abs(channel[i]);
            min = Math.min(min, abs);
            max = Math.max(max, abs);
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

    private short[] getIntro(double frequency, double amplitude, int pauseLength, int smallAmplitudesLength)
    {
        // Пауза перед началом нарастания
        short[] pause = new short[pauseLength];

        // Генерация свип-сигнала с малыми амплитудами
        short[] smallAmplitudeWave = createSmallAmplitudeWave(frequency, amplitude * 0.005, smallAmplitudesLength);

        short[] output = new short[pauseLength + smallAmplitudeWave.length];
        System.arraycopy(pause, 0, output, 0, pauseLength);
        System.arraycopy(smallAmplitudeWave, 0, output, pauseLength, smallAmplitudeWave.length);

        return output;
    }

    public short[] createFadeInWave(double frequency, double amplitude, int fadeInSamplesLength)
    {
        boolean isLined = true;
        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;
        short[] output = new short[fadeInSamplesLength];

        for (int i = 0; i < fadeInSamplesLength; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            double fadeFactor = !isLined
                ? Math.pow(i / (double) fadeInSamplesLength, (double) 1 / 6.0)
                : i / (double) fadeInSamplesLength; // Линейное затухание
            output[i] = (short) (Math.sin(angle_) * amplitude * fadeFactor * Short.MAX_VALUE);
        }

        return output;
    }

    // Метод для создания слабых колебаний с малыми амплитудами
    private short[] createSmallAmplitudeWave(double frequency, double amplitude, int length)
    {
        short[] smallWave = new short[length];
        double angleIncrement = 2.0 * Math.PI * frequency / Constants.SAMPLE_RATE;

        for (int i = 0; i < length; i++)
        {
            angle_ = incrementAngle(angleIncrement);
            smallWave[i] = (short) (Math.sin(angle_) * amplitude * Short.MAX_VALUE);
        }
        return smallWave;
    }
}