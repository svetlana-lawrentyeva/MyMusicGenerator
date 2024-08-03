package ivko.lana.neurotone.audio_generator;

/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.StereoPlayer;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.WaveDetail;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.logging.Logger;

public class AudioPlayer extends StereoPlayer
{
    private static final Logger logger = CustomLogger.getLogger(AudioPlayer.class.getName());

    private SourceDataLine line_;

    public AudioPlayer()
    {
        super();
    }

    protected void prepare()
    {
        try
        {
            AudioFormat format_ = new AudioFormat(Constants.SAMPLE_RATE, 16, 2, true, true);
            line_ = AudioSystem.getSourceDataLine(format_);
            line_.open(format_);
        }
        catch (LineUnavailableException e)
        {
            throw new RuntimeException(e);
        }
        line_.start();
    }

    protected void send(byte[] audioData)
    {
        line_.write(audioData, 0, audioData.length);
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
            stereoByteArray[i * 4] = (byte) (leftChannel[i] >> 8);      // Старший байт
            stereoByteArray[i * 4 + 1] = (byte) (leftChannel[i]);       // Младший байт

            // Преобразование значений из rightChannel
            stereoByteArray[i * 4 + 2] = (byte) (rightChannel[i] >> 8); // Старший байт
            stereoByteArray[i * 4 + 3] = (byte) (rightChannel[i]);      // Младший байт
        }
        logger.info(String.format("%s %s samples were converted into %s bytes", getClass().getSimpleName(), leftChannel.length, stereoByteArray.length));

        postToQueue(stereoByteArray);
    }

    @Override
    protected void stopProcesses()
    {
        if (line_ != null)
        {
            line_.drain();
            line_.close();
        }
    }

    @Override
    public boolean isRunning()
    {
        return line_ == null
                ? super.isRunning()
                : line_.isRunning() || line_.available() > 0;
    }
}
