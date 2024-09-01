package ivko.lana.neurotone.audio_generator;

/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.StereoSaver;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.WaveDetail;

import javax.sound.sampled.AudioFormat;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class AudioSaver extends StereoSaver
{
    private static final Logger logger = CustomLogger.getLogger(AudioSaver.class.getName());

    public static String FileName_ = "generated.wav";
    private AudioFormat format_;
    private long dataSize = 0; // отслеживание размера данных

    public AudioSaver()
    {
        super();
    }

    @Override
    public void setOutputStream(OutputStream outputStream)
    {
        super.setOutputStream(outputStream);
        try
        {
            format_ = new AudioFormat(Constants.SAMPLE_RATE, 16, 2, true, true);
            // Пишем пустой заголовок, чтобы обновить его позже
            writeWavHeader(getOutputStream(), format_, 0);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void setFileName(String fileName)
    {
        FileName_ = fileName;
    }

    @Override
    protected String getFileName()
    {
        return FileName_;
    }

    public static String getAudioFileName()
    {
        logger.info("Asking for the audio file name");
        return FileName_;
    }

    @Override
    protected void send(byte[] data)
    {
        super.send(data);
        dataSize += data.length; // Обновляем размер данных
    }

    public void post(WaveDetail leftChannelWave, WaveDetail rightChannelWave)
    {
        short[] leftChannel = leftChannelWave.getSamples();
        short[] rightChannel = rightChannelWave.getSamples();
        int totalSamples = leftChannel.length; // Предполагается, что длина обоих каналов одинакова
        byte[] stereoByteArray = new byte[totalSamples * 4]; // 4 байта на выборку (2 байта на канал)

        for (int i = 0; i < totalSamples; i++)
        {
            // Преобразование значений из leftChannel
            stereoByteArray[i * 4] = (byte) leftChannel[i];              // Младший байт
            stereoByteArray[i * 4 + 1] = (byte) (leftChannel[i] >> 8);   // Старший байт

            // Преобразование значений из rightChannel
            stereoByteArray[i * 4 + 2] = (byte) rightChannel[i];         // Младший байт
            stereoByteArray[i * 4 + 3] = (byte) (rightChannel[i] >> 8);  // Старший байт
        }
        // Добавляем аудиоданные в очередь для асинхронной записи
        postToQueue(stereoByteArray);
    }

    protected void doAfterAll()
    {
        try
        {
            updateWavHeader(FileName_); // Обновляем заголовок перед закрытием
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        super.doAfterAll();
    }

    private void updateWavHeader(String filename) throws IOException
    {
        try (RandomAccessFile wavFile = new RandomAccessFile(filename, "rw"))
        {
            wavFile.seek(0); // Переход в начало файла
            writeWavHeader(new FileOutputStream(wavFile.getFD()), format_, dataSize); // Обновляем заголовок
        }
    }

    private void writeWavHeader(OutputStream out, AudioFormat format, long dataSize) throws IOException {
        byte[] header = new byte[44];
        long totalDataLen = dataSize + 36; // Общий размер данных плюс 36 байт заголовка
        int channels = format.getChannels();
        int bitsPerSample = format.getSampleSizeInBits();
        long byteRate = (long) format.getSampleRate() * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1 (PCM)
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) ((int) format.getSampleRate() & 0xff);
        header[25] = (byte) (((int) format.getSampleRate() >> 8) & 0xff);
        header[26] = (byte) (((int) format.getSampleRate() >> 16) & 0xff);
        header[27] = (byte) (((int) format.getSampleRate() >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (blockAlign);
        header[33] = 0;
        header[34] = (byte) bitsPerSample;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (dataSize & 0xff);
        header[41] = (byte) ((dataSize >> 8) & 0xff);
        header[42] = (byte) ((dataSize >> 16) & 0xff);
        header[43] = (byte) ((dataSize >> 24) & 0xff);

        out.write(header, 0, 44);
    }

}
