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

        if (leftChannel == null || rightChannel == null)
        {
            logger.warning(String.format("%s received null channel samples. Skipping post.", getClass().getSimpleName()));
            return;
        }

        int totalSamples = Math.min(leftChannel.length, rightChannel.length);
        if (totalSamples <= 0)
        {
            logger.warning(String.format("%s received empty channel data. Skipping post.", getClass().getSimpleName()));
            return;
        }

        if (leftChannel.length != rightChannel.length)
        {
            logger.warning(String.format("%s left/right sample length mismatch: %s vs %s. Truncating to %s samples.",
                    getClass().getSimpleName(), leftChannel.length, rightChannel.length, totalSamples));
        }

        long byteLength = (long) totalSamples * 4L;
        if (byteLength > Integer.MAX_VALUE)
        {
            int cappedSamples = Integer.MAX_VALUE / 4;
            logger.warning(String.format("%s sample count too large (%s). Capping to %s samples.",
                    getClass().getSimpleName(), totalSamples, cappedSamples));
            totalSamples = cappedSamples;
            byteLength = (long) totalSamples * 4L;
        }

        byte[] stereoByteArray = new byte[(int) byteLength]; // 4 байта на выборку (2 байта на канал)

        for (int i = 0; i < totalSamples; i++)
        {
            int byteIndex = i * 4;
            if (byteIndex + 3 >= stereoByteArray.length)
            {
                logger.warning(String.format("%s byte buffer too short at index %s of %s. Stopping conversion.",
                        getClass().getSimpleName(), byteIndex, stereoByteArray.length));
                break;
            }

            // Преобразование значений из leftChannel
            stereoByteArray[byteIndex] = (byte) (leftChannel[i] >> 8);      // Старший байт
            stereoByteArray[byteIndex + 1] = (byte) (leftChannel[i]);       // Младший байт

            // Преобразование значений из rightChannel
            stereoByteArray[byteIndex + 2] = (byte) (rightChannel[i] >> 8); // Старший байт
            stereoByteArray[byteIndex + 3] = (byte) (rightChannel[i]);      // Младший байт
        }
        logger.info(String.format("%s %s samples were converted into %s bytes", getClass().getSimpleName(), leftChannel.length,
                stereoByteArray.length));

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
