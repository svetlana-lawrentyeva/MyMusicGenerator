package ivko.lana.generators;/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.util.Util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;

public class DynamicToneGenerator
{
    private static final int MIN_MAIN_DURATION_CYCLES = 4;
    private static final int MAX_MAIN_DURATION_CYCLES = 8;
    private static final double[] SOLFEGE = {174, 285, 396, 417, 528, 639, 741, 852, 963};
    public static final int FREQUENCY_NUMBER = 4;


    public static void main(String[] args) throws LineUnavailableException
    {
        // Настройка аудиоформата
        float sampleRate = 44100;
        byte[] buffer = new byte[2];
        AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();

        Random random = new Random();
        int startSolfegeIndex = random.nextInt(SOLFEGE.length - FREQUENCY_NUMBER);
        double[] solfeges = new double[FREQUENCY_NUMBER];
        System.arraycopy(SOLFEGE, startSolfegeIndex, solfeges, 0,FREQUENCY_NUMBER);

        int mainSolfegeIndex = random.nextInt(solfeges.length);
        int mainDurationCycles = MIN_MAIN_DURATION_CYCLES + random.nextInt(MAX_MAIN_DURATION_CYCLES - MIN_MAIN_DURATION_CYCLES);
        int mainCyclesCounter = 0;

        double cycleDuration = 20;  // Длительность в секундах
        short[] totalSamples = null;
        for (int j = 0; j < 16; ++j)
        {
            if (mainCyclesCounter++ >= mainDurationCycles)
            {
                int solfegeIndexStep = 1;
                boolean solfegeIndexDirection = random.nextBoolean();
                mainSolfegeIndex += solfegeIndexStep * (solfegeIndexDirection ? 1 : -1);
                if (mainSolfegeIndex < 0)
                {
                    mainSolfegeIndex = 0;
                }
                if (mainSolfegeIndex >= solfeges.length)
                {
                    mainSolfegeIndex = solfeges.length - 1;
                }
                mainCyclesCounter = 0;
                mainDurationCycles = MIN_MAIN_DURATION_CYCLES + random.nextInt(MAX_MAIN_DURATION_CYCLES - MIN_MAIN_DURATION_CYCLES);
            }
            List<short[]> sampleList = new ArrayList<>();
            double mainFrequency = SOLFEGE[3];
            sampleList.add(creteSamples(mainFrequency, cycleDuration, sampleRate, 1.0));

//            int secondaryFrequencyNumber = 2 + random.nextInt(3 - 2);

            short[] samples = sampleList.get(0);
            for (int i = 1; i < sampleList.size(); ++i)
            {
                samples = Util.combineSamples(samples, sampleList.get(i));
            }

            if (totalSamples == null)
            {
                totalSamples = samples;
            }
            else
            {
                int lastIndex = findLastAppropriateIndex(totalSamples);

                short[] newTotalSamples = new short[lastIndex];
                System.arraycopy(totalSamples, 0, newTotalSamples, 0, lastIndex);

                int crossfadeRealLength = (int) (sampleRate / 2);
                int crossfadeArealLength = crossfadeRealLength / 2;
                int crossfadeTotalLength = crossfadeRealLength + crossfadeArealLength;

                newTotalSamples = new short[totalSamples.length + samples.length - crossfadeTotalLength];

                System.arraycopy(totalSamples, 0, newTotalSamples, 0, totalSamples.length - crossfadeTotalLength);

                short[] previousTail = new short[crossfadeTotalLength];
                short[] nextHead = new short[crossfadeTotalLength];

                for (int i = 0; i < crossfadeTotalLength; i++)
                {
                    double fadeOutFactor = i > crossfadeArealLength ? 1.0 - ((i - crossfadeArealLength) / (double) crossfadeRealLength) : 1;  // Линейное затухание
                    double fadeInFactor = i < crossfadeRealLength ? (i / (double) crossfadeRealLength) : 1;  // Линейное нарастание

                    previousTail[i] = (short) (totalSamples[totalSamples.length - crossfadeTotalLength + i] * fadeOutFactor); // Коррекция амплитуды и фазы на стыке
                    nextHead[i] = (short) (samples[i] * fadeInFactor); // Коррекция амплитуды и фазы на стыке
                }

                short[] fadeoutCombinedSamples = Util.combineSamples(previousTail, nextHead);
                System.arraycopy(fadeoutCombinedSamples, 0, newTotalSamples, totalSamples.length - crossfadeTotalLength, fadeoutCombinedSamples.length);

                System.arraycopy(samples, crossfadeTotalLength, newTotalSamples, totalSamples.length, samples.length - crossfadeTotalLength);

                totalSamples = newTotalSamples;
            }
        }

//        totalSamples = changeVolume(totalSamples, 0.2);
//        totalSamples = timeStretch(totalSamples, 6.0, 1024);

        for (int i = 0; i < totalSamples.length; i++)
        {
            buffer[0] = (byte) (totalSamples[i] & 0xFF);
            buffer[1] = (byte) (totalSamples[i] >> 8);
            sdl.write(buffer, 0, 2);
        }

        sdl.drain();
        sdl.stop();
    }

    public static short[] timeStretch(short[] samples, double stretchFactor, int windowSize) {
        int numWindows = samples.length / windowSize;
        int newLength = (int) (samples.length * stretchFactor);
        short[] stretchedSamples = new short[newLength];

        int overlapSize = windowSize / 2;  // Накладываем половину окна
        int stepSize = (int) (windowSize * stretchFactor);  // Новый шаг с учётом растяжения

        int stretchedIndex = 0;

        for (int i = 0; i < numWindows; i++) {
            int startSample = i * windowSize;

            for (int j = 0; j < windowSize; j++) {
                int currentSampleIndex = startSample + j;

                // Проверка границ массива
                if (currentSampleIndex < samples.length && stretchedIndex < stretchedSamples.length) {
                    // Добавляем семплы с наложением
                    stretchedSamples[stretchedIndex] += samples[currentSampleIndex];
                }

                stretchedIndex++;

                // Если мы дошли до конца текущего окна, добавляем перекрытие
                if (j >= overlapSize && stretchedIndex < stretchedSamples.length) {
                    stretchedSamples[stretchedIndex] += samples[currentSampleIndex - overlapSize];
                }
            }

            // Смещаемся на новый шаг
            stretchedIndex += stepSize - windowSize;
        }

        return stretchedSamples;
    }


    public static short[] changeVolume(short[] samples, double factor) {
        short[] newSamples = new short[samples.length];
        for (int i = 0; i < samples.length; i++) {
            newSamples[i] = (short) (samples[i] * factor);  // Уменьшаем амплитуду семпла
        }
        return newSamples;
    }


    private static int findLastAppropriateIndex(short[] samples)
    {
        int appropriateIndex = samples.length;
        boolean found = false;
        while (!found)
        {
            if (appropriateIndex == 0 || samples[appropriateIndex - 1] < 0)
            {
                found = true;
            }
            else
            {
                appropriateIndex--;
            }
        }
        return appropriateIndex;
    }

    private static short[] creteSamples(double baseFrequency, double duration, float sampleRate, double amplitudeFactor)
    {
        List<short[]> sampleList = new ArrayList<>();
        sampleList.add(generateUnderwaterMelody(baseFrequency + 0.0, duration, sampleRate, 0.025, 0.05, amplitudeFactor));
//        sampleList.add(generateUnderwaterMelody(baseFrequency + 0.5, duration, sampleRate, 0.030, 0.06, amplitudeFactor / 1.5));
        sampleList.add(generateUnderwaterMelody(baseFrequency + 1.0, duration, sampleRate, 0.035, 0.07, amplitudeFactor / 2.8));
//        sampleList.add(generateUnderwaterMelody(baseFrequency + 1.5, duration, sampleRate, 0.040, 0.08, amplitudeFactor / 3.1));
        sampleList.add(generateUnderwaterMelody(baseFrequency + 2.0, duration, sampleRate, 0.045, 0.09, amplitudeFactor / 4.4));
        sampleList.add(generateUnderwaterMelody(baseFrequency + 3, duration, sampleRate, 0.01, 0.08, amplitudeFactor / 7.0));
        sampleList.add(generateUnderwaterMelody(baseFrequency + 4, duration, sampleRate, 0.007, 0.09, amplitudeFactor / 12.0));
        sampleList.add(generateUnderwaterMelody(baseFrequency + 5, duration, sampleRate, 0.003, 0.10, amplitudeFactor / 23.0));
        short[] samples = sampleList.get(0);
        for (int i = 1; i < sampleList.size(); ++i)
        {
            samples = Util.combineSamples(samples, sampleList.get(i));
        }
        return samples;
    }

    public static short[] generateUnderwaterMelody(double baseFrequency, double duration, float sampleRate, double modulationSpeedFactor, double modulationDepthFactor, double amplitudeFactor)
    {
        long numSamples = (long) (sampleRate * duration);
        short[] samples = new short[(int) numSamples];
        double[] baseFrequencies =
                {
                        baseFrequency / 1.5,
//                        3 * baseFrequency / 2.0,
                        baseFrequency,
                        baseFrequency * 1.5,
//                        baseFrequency * (1.5 * 1.5),
//                        baseFrequency * (1.5 * 1.5 * 1.5),
//                        baseFrequency * (1.5 * 1.5 * 1.5 * 1.5),
                };
        double[] harmonyFrequencies0 = Arrays.stream(baseFrequencies)
                .boxed()
                .map(frequency -> frequency * 2)
                .mapToDouble(Double::doubleValue)
                .toArray();

        double amplitude = 0.02 * amplitudeFactor;
        double harmonyAmplitude = 0.01 * amplitudeFactor;

        double time = 0;

        for (int i = 0; i < numSamples; i++)
        {
            double signal = 0;
            double[] phaseShifts = {0.0, Math.PI / 4, Math.PI / 2, Math.PI, 3 * Math.PI / 2};  // Фазовые сдвиги

            for (int j = 0; j < baseFrequencies.length; ++j)
            {
                double baseFreq = baseFrequencies[j];
                double modulatedFreq = baseFreq + modulationDepthFactor * Math.sin(time * 5 * modulationSpeedFactor);  // Более медленная модуляция
                double angle = i / (sampleRate / modulatedFreq) * 2.0 * Math.PI + phaseShifts[j % phaseShifts.length];  // Добавляем фазовый сдвиг
//                double modulatedAmplitude = (amplitude * (baseFrequencies.length - j)) / (j + 1) * (1.0 + 0.05 * Math.sin(time * 0.05));  // Плавная модуляция амплитуды
                signal += Math.sin(angle) * amplitude;

            }

            for (int j = 0; j < harmonyFrequencies0.length; ++j)
            {
                double harmonyFreq = harmonyFrequencies0[j];
                double modulatedHarmonyFreq = harmonyFreq + modulationDepthFactor * Math.sin(time * 3 * modulationSpeedFactor);  // Очень мягкая модуляция гармоник
                double harmonyAngle = i / (sampleRate / modulatedHarmonyFreq) * 2.0 * Math.PI;
//                double modulatedAmplitude = harmonyAmplitude * (1.0 + 0.05 * Math.sin(time * 0.05));  // Плавная модуляция амплитуды
                signal += Math.sin(harmonyAngle) * harmonyAmplitude;
            }
            samples[i] = (short) (signal * 32767);
        }
        return samples;
    }
}
