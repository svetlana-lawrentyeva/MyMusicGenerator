package ivko.lana.neurotone.processing;

import ivko.lana.neurotone.wave_generator.WaveType;
import ivko.lana.neurotone.wave_generator.melody.ITriadSequenceGenerator;
import ivko.lana.neurotone.wave_generator.melody.NoteGenerator;
import ivko.lana.neurotone.wave_generator.sounds.SoundType;

/**
 * @author Lana Ivko
 */
public class Constants
{
//    public static final SoundType SOUND_TYPE = SoundType.SIMPLE;
    public static final SoundType SOUND_TYPE = SoundType.TIBETAN;
    public static final float SAMPLE_RATE = 44100.0f; // Частота дискретизации
    public static final int HIT_DURATION_MS = 8000; // Длительность затухания удара
    public static final int PAUSE_DURATION_MS = (int) (1000 * 0.09317); // Длительность паузы
    public static final int SMALL_AMPLITUDES_DURATION_MS = (int) (1000 * (0.129545 - 0.09317)); // Длительность маленьких колебаний
    public static final int FADE_IN_DURATION_MS = 400; // Длительность нарастания
    public static final int BASE_PULSATION_SPEED = 3; // Базовая скорость пульсации
    public static float PulsationDepth_ = 0.5f; // Параметр для управления пульсацией (от 0 до 1)
    public static float PulsationSpeedFactor_ = 1.0f; // Коэффициент для управления скоростью пульсации
    public static double FrequencyOffset_ = 0.0; // Коэффициент для управления скоростью пульсации
    public static double BaseFrequency_ = 432;
    public static double BeatDurationMs_ = 4000.0;
    public static int FadeOutDurationMs_ = 10000; // Длительность затухания
    public static boolean IsSeparated_ = false;
    public static boolean OneTone_ = false;

    public static double UnitizationDividerFactor_ = 1;

    public static double VibrationFactor_ = 1;
    public static ITriadSequenceGenerator.DistributorType DistributorType_ = ITriadSequenceGenerator.DistributorType.DYADED;

    public static NoteGenerator.ScaleDegreeType ScaleDegreeType_ = NoteGenerator.ScaleDegreeType.MAJOR;

    public static WaveType WaveType_ = WaveType.SOLFEGE;


    // Метод для установки глубины пульсации
    //
    // Этот параметр управляет амплитудой колебаний,
    // то есть насколько сильно звук изменяется от одного канала к другому.
    // Значение 0.3f означает, что перекат будет происходить менее резко,
    // с меньшей амплитудой, чем при максимальном значении 1.0f.
    // Однако этот параметр не влияет на скорость переката, он влияет только на его интенсивность.

    public static void setPulsationDepth(float depth)
    {
        if (depth < 0.0f) depth = 0.0f;
        if (depth > 1.0f) depth = 1.0f;
        PulsationDepth_ = depth;
    }

    public static void setWaveType(WaveType waveType)
    {
        WaveType_ = waveType;
    }

    public static void setUnitizationDividerFactor(double unitizationDividerFactor)
    {
        UnitizationDividerFactor_ = unitizationDividerFactor;
    }

    public static void setSeparation(boolean isSeparated)
    {
        IsSeparated_ = isSeparated;
    }

    public static void setOneTone(boolean oneTone)
    {
        OneTone_ = oneTone;
    }

    public static void setDistributorType(ITriadSequenceGenerator.DistributorType distributorType_)
    {
        DistributorType_ = distributorType_;
    }

    public static void setScaleDegreeType(NoteGenerator.ScaleDegreeType scaleDegreeType)
    {
        ScaleDegreeType_ = scaleDegreeType;
    }


    public static void setBaseFrequency(double baseFrequency)
    {
        BaseFrequency_ = baseFrequency;
    }

    // Метод для установки скорости пульсации
    //
    // Этот параметр управляет скоростью переката звука из одного уха в другое.
    // Значение 0.2f означает, что перекат будет происходить медленнее.
    // Чем меньше значение pulsationRateFactor,
    // тем медленнее звук будет перекатываться из одного канала в другой.

    public static void setPulsationSpeedFactor(float speedFactor)
    {
        if (speedFactor < 0.1f) speedFactor = 0.1f;
        if (speedFactor > 10.0f) speedFactor = 10.0f;
        PulsationSpeedFactor_ = speedFactor;
    }

    // Метод для установки бинаурального ритма
    public static void setFrequencyOffset(double frequencyOffset)
    {
        FrequencyOffset_ = frequencyOffset;
    }

    // Метод для установки бинаурального ритма
    public static void setVibrationFactor(double vibrationFactor)
    {
        VibrationFactor_ = vibrationFactor;
    }

    public static void setBeatDurationMs(double beatDurationMs)
    {
        Constants.BeatDurationMs_ = beatDurationMs;
    }

    public static void setFadeOutDurationMs(int fadeOutDurationMs)
    {
        Constants.FadeOutDurationMs_ = fadeOutDurationMs;
    }
}
