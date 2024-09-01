package ivko.lana.instruments_for_test.tibetan_sound_analyzer;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WavFileReader
{
    public static double[] getMonoSamples(File wavFile) throws IOException, UnsupportedAudioFileException
    {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        AudioFormat format = audioInputStream.getFormat();

        int bytesPerFrame = format.getFrameSize();
        if (bytesPerFrame == AudioSystem.NOT_SPECIFIED)
        {
            bytesPerFrame = 1;
        }

        byte[] audioBytes = audioInputStream.readAllBytes();
        int totalFrames = audioBytes.length / bytesPerFrame;
        double[] samples = new double[totalFrames];

        boolean isBigEndian = format.isBigEndian();
        int channels = format.getChannels();
        int sampleSizeInBits = format.getSampleSizeInBits();

        for (int frame = 0; frame < totalFrames; frame++)
        {
            double sampleSum = 0;
            for (int channel = 0; channel < channels; channel++)
            {
                int sampleIndex = (frame * bytesPerFrame) + (channel * sampleSizeInBits / 8);
                double sample = 0;

                if (sampleSizeInBits == 16)
                {
                    int low, high;
                    if (isBigEndian)
                    {
                        high = audioBytes[sampleIndex];
                        low = audioBytes[sampleIndex + 1];
                    }
                    else
                    {
                        low = audioBytes[sampleIndex];
                        high = audioBytes[sampleIndex + 1];
                    }
                    int val = (high << 8) | (low & 0xFF);
                    sample = val / 32768.0; // Нормализация для 16 бит
                }
                else if (sampleSizeInBits == 8)
                {
                    int val = audioBytes[sampleIndex];
                    sample = (val - 128) / 128.0; // Нормализация для 8 бит
                }
                sampleSum += sample;
            }
            samples[frame] = sampleSum / channels; // Авергирование каналов для моно
        }

        audioInputStream.close();
        return samples;
    }

    public static void analyze(double[] signal, float sampleRate)
    {
        // Применяем окно Хэмминга
        double[] windowedSignal = applyHammingWindow(signal);

        // Вычисляем ближайшую степень двойки
        int n = nextPowerOfTwo(windowedSignal.length);

        // Дополняем нулями до нужной длины
        double[] paddedSignal = new double[n];
        System.arraycopy(windowedSignal, 0, paddedSignal, 0, windowedSignal.length);

        // Выполняем FFT
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] fftResult = fft.transform(paddedSignal, TransformType.FORWARD);

        // Вычисляем амплитуды и частоты
        int length = fftResult.length / 2; // Симметрия, берем только первую половину
        double[] amplitudes = new double[length];
        double[] frequencies = new double[length];

        for (int i = 0; i < length; i++)
        {
            amplitudes[i] = fftResult[i].abs();
            frequencies[i] = (double) i * sampleRate / n;
        }

        // Поиск основной частоты
        double maxAmplitude = 0;
        double fundamentalFrequency = 0;

        for (int i = 1; i < length; i++)
        {
            if (amplitudes[i] > maxAmplitude)
            {
                maxAmplitude = amplitudes[i];
                fundamentalFrequency = frequencies[i];
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("wav_analyzer.txt", true)))
        {

            // Поиск обертонов до 20,000 Гц
            writer.println("Frequency\tAmplitude");
//        double threshold = 0.01 * maxAmplitude; // Порог для обертонов (5% от амплитуды основной частоты)
            double threshold = 0; // Порог для обертонов (5% от амплитуды основной частоты)
            for (int i = 1; i < length; i++)
            {
                if (frequencies[i] > 3000) break; // Ограничение частоты до 20,000 Гц
                if (amplitudes[i] >= threshold)
                {
                    writer.printf("%.2f\t%.2f\n", frequencies[i], amplitudes[i]);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static double[] applyHammingWindow(double[] signal)
    {
        int N = signal.length;
        double[] windowed = new double[N];
        for (int n = 0; n < N; n++)
        {
            windowed[n] = signal[n] * (0.54 - 0.46 * Math.cos(2 * Math.PI * n / (N - 1)));
        }
        return windowed;
    }

    public static int nextPowerOfTwo(int n)
    {
        int result = 1;
        while (result < n) result <<= 1;
        return result;
    }

    public static void main(String[] args)
    {
        File wavFile = new File("D:\\VIDEO\\NEUROTONE\\development\\left.wav"); // Замените на путь к вашему файлу
        try
        {
            double[] samples = getMonoSamples(wavFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
            AudioFormat format = audioInputStream.getFormat();
            float sampleRate = format.getSampleRate();

            analyze(samples, sampleRate);

        }
        catch (IOException | UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
    }
}
