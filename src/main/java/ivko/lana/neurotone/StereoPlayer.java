package ivko.lana.neurotone;

/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.wave_generator.WaveDetail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class StereoPlayer
{
    private static WaveDetail LeftTail_ = new WaveDetail();
    private static WaveDetail RightTail_ = new WaveDetail();
    private static final Logger logger = CustomLogger.getLogger(StereoPlayer.class.getName());

    public static final int FADE_OUT_TAIL = Util.convertMsToSampleLength(Constants.FadeOutDurationMs_);
    private BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private boolean isPlayerRunning_ = true;

    private long summ_ = 0;
    private final ExecutorService executorService_;

    public StereoPlayer()
    {
        prepare();
        executorService_ = Executors.newSingleThreadExecutor();
        executorService_.execute(this::processQueue);
    }

    protected void prepare(){}

    private void processQueue()
    {
        while (isPlayerRunning_ || !queue.isEmpty())
        {
            byte[] data = queue.poll();
            if (data != null)
            {
                summ_ += data.length;
                send(data);
            }
        }
        logger.info(String.format("%s executorService_.shutdown()", getClass().getSimpleName()));
        executorService_.shutdown();
    }

    public void processData(WaveDetail leftChannelWave, WaveDetail rightChannelWave)
    {
        if (leftChannelWave == null || rightChannelWave == null)
        {
            logger.warning(String.format("%s received null WaveDetail. Skipping processing.", getClass().getSimpleName()));
            return;
        }

        short[] leftChannel = leftChannelWave.getSamples();
        short[] rightChannel = rightChannelWave.getSamples();

        if (leftChannel == null || rightChannel == null)
        {
            logger.warning(String.format("%s received null channel samples. Skipping processing.", getClass().getSimpleName()));
            return;
        }

        int rawLength = Math.min(leftChannel.length, rightChannel.length);
        if (rawLength == 0)
        {
            logger.warning(String.format("%s received empty channel data. Skipping processing.", getClass().getSimpleName()));
            return;
        }

        int tailLength = Math.min(FADE_OUT_TAIL, rawLength);
        int mainLength = rawLength - tailLength;

        short[] newLeftFadeOutTail = new short[tailLength];
        short[] newRightFadeOutTail = new short[tailLength];
        short[] newLeftChannel = new short[mainLength];
        short[] newRightChannel = new short[mainLength];

        short[] previousLeftTail = LeftTail_.getSamples();
        short[] previousRightTail = RightTail_.getSamples();
        boolean hasPreviousTail = previousLeftTail != null && previousRightTail != null;

        if (hasPreviousTail)
        {
            int overlap = Math.min(Math.min(previousLeftTail.length, previousRightTail.length), mainLength);
            for (int i = 0; i < overlap; i++)
            {
                newLeftChannel[i] = Util.getLimitedValue(previousLeftTail[i] + leftChannel[i]);
                newRightChannel[i] = Util.getLimitedValue(previousRightTail[i] + rightChannel[i]);
            }

            if (mainLength > overlap)
            {
                System.arraycopy(leftChannel, overlap, newLeftChannel, overlap, mainLength - overlap);
                System.arraycopy(rightChannel, overlap, newRightChannel, overlap, mainLength - overlap);
            }
        }
        else if (mainLength > 0)
        {
            System.arraycopy(leftChannel, 0, newLeftChannel, 0, mainLength);
            System.arraycopy(rightChannel, 0, newRightChannel, 0, mainLength);
        }

        System.arraycopy(leftChannel, rawLength - tailLength, newLeftFadeOutTail, 0, tailLength);
        System.arraycopy(rightChannel, rawLength - tailLength, newRightFadeOutTail, 0, tailLength);

        LeftTail_.setSamples(newLeftFadeOutTail);
        RightTail_.setSamples(newRightFadeOutTail);

        if (mainLength > 0)
        {
            post(new WaveDetail(leftChannelWave.getFrequencies(), leftChannelWave.getDurations(), newLeftChannel),
                    new WaveDetail(rightChannelWave.getFrequencies(), rightChannelWave.getDurations(), newRightChannel));
        }
    }

    public abstract void post(WaveDetail leftChannel, WaveDetail rightChannel);


    protected void postToQueue(byte[] data)
    {
        queue.offer(data);
    }

    protected abstract void send(byte[] data);

    public void close()
    {
        try
        {
            while (isFull())
            {
                logger.info(String.format("Sleeping for 1 second. Some saver is busy."));
                Thread.sleep(1000);
            }
            if (LeftTail_.getSamples() != null && RightTail_.getSamples() != null)
            {
                logger.info(String.format("%s Найдены хвосты для записию Отправляем их в очередь", getClass().getSimpleName()));
                post(LeftTail_, RightTail_);
            }
            while (needWait())
            {
//                logger.info(String.format("%s needs wait. Sleep for 100 ms", getClass().getSimpleName()));
                Thread.sleep(100);
            }
            isPlayerRunning_ = false;
        }
        catch (Throwable e)
        {
            logger.log(Level.SEVERE, String.format("Прерывание при ожидании обработки очереди: %s", e), e);
            throw new RuntimeException(e);
        }
        finally
        {
            doAfterAll();
            logger.info(String.format("%s Останавливаем процессы", getClass().getSimpleName()));
            stopProcesses();
            logger.info(String.format("Total %s bytes have been written", summ_));
        }
    }

    public boolean needWait()
    {
        return !queue.isEmpty();
    }

    protected void doAfterAll(){};

    protected void stopProcesses(){}

    public boolean isRunning()
    {
//        logger.info(String.format("StereoPlayer %s isRunning_: %s", getClass().getSimpleName(), isRunning_));
        return isPlayerRunning_;
    }

    public boolean isFull()
    {
        return false;
    }
}
