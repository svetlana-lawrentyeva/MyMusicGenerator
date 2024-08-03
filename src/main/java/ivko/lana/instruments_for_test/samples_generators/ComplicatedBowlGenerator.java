package ivko.lana.instruments_for_test.samples_generators;

/**
 * @author Lana Ivko
 */
public class ComplicatedBowlGenerator
{
    private static final double MIN_AMPLITUDE = 0.2; // Минимальная амплитуда затухания
    private static final double MAX_AMPLITUDE = 0.3; // Максимальная амплитуда для пиков
    public static final String FILE_PATH = "complicated_bowl.wav";

    public static void main(String[] args)
    {
        int duration = 360; // Длительность в секундах
        int sampleRate = 44100; // Частота дискретизации
        Util.clearPreviousFile(FILE_PATH);
//        double[] frequencies = new double[]{385, 1035, 1900, 2935};
//        double[] deltas = new double[]{0.05, 0.2, 0.15, 0.1};
//        byte[][] bowlSounds = new byte[4][0];
        double[] frequencies = new double[]{385};
        double[] deltas = new double[]{0.2};
        byte[][] bowlSounds = new byte[1][0];
        for (int i = 0; i < frequencies.length; ++i)
        {
            double frequency = frequencies[i];
            double delta = deltas[i];
            byte[] bowlSound = generateTone(duration, sampleRate, frequency, delta);
            bowlSounds[i] = bowlSound;
        }
        byte[] bowlSound = bowlSounds[0];
//        byte[] bowlSound = mixAudioTracks(bowlSounds);
        Util.saveWaveFile(bowlSound, sampleRate, FILE_PATH);
    }

    public static byte[] mixAudioTracks(byte[][] tracks)
    {
        if (tracks.length == 0)
        {
            return null;
        }


        int totalSamples = tracks[0].length / 2; // Предполагаем, что все треки имеют одинаковую длину
        byte[] output = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++)
        {
            int sample = 0;
            for (byte[] track : tracks)
            {
                sample += ((track[i * 2 + 1] << 8) | (track[i * 2] & 0xff));
            }

            // Обрезка значений, чтобы избежать переполнения
            if (sample > Short.MAX_VALUE)
            {
                sample = Short.MAX_VALUE;
            }
            else if (sample < Short.MIN_VALUE)
            {
                sample = Short.MIN_VALUE;
            }

            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }


    public static byte[] generateTone(int duration, int sampleRate, double frequency, double amplitudeDelta)
    {
        int totalSamples = duration * sampleRate;
        byte[] output = new byte[totalSamples * 2]; // 16-битный (2 байта на сэмпл)

        double midAmplitude = (MAX_AMPLITUDE + MIN_AMPLITUDE) / 2;
        double maxAmplitude = midAmplitude + amplitudeDelta; // Начальная амплитуда
        double minAmplitude = midAmplitude - amplitudeDelta; // Минимальная амплитуда

        for (int i = 0; i < totalSamples; i++)
        {
            double sampleValue = Math.sin(2.0 * Math.PI * frequency * i / sampleRate);

            // Применение дельты амплитуды
            double currentAmplitude = minAmplitude + (maxAmplitude - minAmplitude) * (i % sampleRate) / sampleRate;
            sampleValue *= currentAmplitude;

            short sample = (short) (sampleValue * Short.MAX_VALUE);
            output[i * 2] = (byte) (sample & 0xff);
            output[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
        }

        return output;
    }

}
