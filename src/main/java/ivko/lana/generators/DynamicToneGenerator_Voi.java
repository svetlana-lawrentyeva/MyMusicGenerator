package ivko.lana.generators;/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.util.Util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class DynamicToneGenerator_Voi
{

    public static void main(String[] args) throws LineUnavailableException
    {
        // Настройка аудиоформата
        float sampleRate = 44100;
        byte[] buffer = new byte[2];
        AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();


        Bowed bowedInstrument = new Bowed(); // A4 frequency
        bowedInstrument.setBowPressure(0.7); // Увеличиваем давление смычка
        bowedInstrument.setBowVelocity(0.5); // Устанавливаем скорость смычка
        bowedInstrument.startBowing(0.8); // Начинаем игру смычком

        int baseFrequency = 100;
        short[] samples1 = generateSamples(bowedInstrument, baseFrequency);
        short[] samples2 = generateSamples(bowedInstrument, baseFrequency + baseFrequency * 0.01);
        short[] samples3 = generateSamples(bowedInstrument, baseFrequency + baseFrequency * 0.02);

        int phaseShift = (int) (samples1.length * 0.02);
        short[] extendedSamples2 = new short[(int) (samples1.length + phaseShift)];
        short[] extendedSamples3 = new short[(int) (samples1.length + phaseShift + phaseShift)];
        System.arraycopy(samples2, 0, extendedSamples2, phaseShift, samples2.length);
        System.arraycopy(samples3, 0, extendedSamples3, phaseShift + phaseShift, samples3.length);

        short[] combinedSamples = Util.combineSamples(samples1, extendedSamples2);
        combinedSamples = Util.combineSamples(combinedSamples, extendedSamples3);

        short[] fullSamples = new short[combinedSamples.length - phaseShift * 4];
        System.arraycopy(combinedSamples, phaseShift * 2, fullSamples, 0, fullSamples.length);

        for (int i = 0; i < fullSamples.length; i++)
        {
            buffer[0] = (byte) (fullSamples[i] & 0xFF);
            buffer[1] = (byte) (fullSamples[i] >> 8);
            sdl.write(buffer, 0, 2);
        }

        sdl.drain();
        sdl.stop();
    }

    private static short[] generateSamples(Bowed bowedInstrument, double frequency)
    {
        double modulationStepMs = 2000;
        double modulationStepHz = 10;
        short[] samples659 = bowedInstrument.generateSamples(frequency - 1, 20, modulationStepMs, modulationStepHz);
        short[] samples660 = bowedInstrument.generateSamples(frequency, 20, modulationStepMs, modulationStepHz);
        short[] samples661 = bowedInstrument.generateSamples(frequency + 1, 20, modulationStepMs, modulationStepHz);

        short[] samples = Util.combineSamples(samples659, samples660);
        samples = Util.combineSamples(samples, samples661);
        return samples;
    }
}

