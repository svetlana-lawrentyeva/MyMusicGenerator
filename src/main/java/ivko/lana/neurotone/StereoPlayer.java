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

    public static final int FADE_OUT_TAIL = Util.convertMsToSampleLength(Constants.FADE_OUT_DURATION_MS);
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
        short[] leftChannel = leftChannelWave.getSamples();
        short[] rightChannel = rightChannelWave.getSamples();
        // Определяем длину новых массивов, которые будут меньше на fadeOutLength
        int newLength = leftChannel.length - FADE_OUT_TAIL;

        // Создаем массивы для новых хвостов звуковых фрагментов
        short[] newLeftFadeOutTail = new short[FADE_OUT_TAIL];
        short[] newRightFadeOutTail = new short[FADE_OUT_TAIL];

        // Создаем новые массивы для основного звука без хвоста
        short[] newLeftChannel = new short[newLength];
        short[] newRightChannel = new short[newLength];

        // Если в хвостах есть данные, добавляем их к началу новых массивов

        if (LeftTail_.getSamples() != null && RightTail_.getSamples() != null)
        {
            // Складываем значения хвостов с первыми элементами нового канала
            for (int i = 0; i < FADE_OUT_TAIL; i++)
            {
                newLeftChannel[i] = Util.getLimitedValue(LeftTail_.getSamples()[i] + leftChannel[i]);
                newRightChannel[i] = Util.getLimitedValue(RightTail_.getSamples()[i] + rightChannel[i]);
            }
            // Копируем оставшиеся значения из новых каналов
            System.arraycopy(leftChannel, FADE_OUT_TAIL, newLeftChannel, FADE_OUT_TAIL, newLength - FADE_OUT_TAIL);
            System.arraycopy(rightChannel, FADE_OUT_TAIL, newRightChannel, FADE_OUT_TAIL, newLength - FADE_OUT_TAIL);
        }
        else
        {
            // Копируем значения из новых каналов
            System.arraycopy(leftChannel, 0, newLeftChannel, 0, newLength);
            System.arraycopy(rightChannel, 0, newRightChannel, 0, newLength);
        }

        // Копируем хвосты звуковых фрагментов в массивы хвостов
        System.arraycopy(leftChannel, newLength, newLeftFadeOutTail, 0, FADE_OUT_TAIL);
        System.arraycopy(rightChannel, newLength, newRightFadeOutTail, 0, FADE_OUT_TAIL);

        // Обновляем хвосты
        LeftTail_.setSamples(newLeftFadeOutTail);
        RightTail_.setSamples(newRightFadeOutTail);

        post(new WaveDetail(leftChannelWave.getFrequencies(), leftChannelWave.getDurations(), newLeftChannel),
                new WaveDetail(rightChannelWave.getFrequencies(), rightChannelWave.getDurations(), newRightChannel));
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
