package ivko.lana.neurotone;

/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public abstract class StereoSaver extends StereoPlayer
{
    private static final Logger logger = CustomLogger.getLogger(StereoSaver.class.getName());

    private OutputStream outputStream_;
    protected final AtomicLong postedToQueueSize_ = new AtomicLong(0);
    protected final AtomicLong totalPostedToQueueSize_ = new AtomicLong(0);


    public StereoSaver()
    {
        super();
    }

    public void setOutputStream(OutputStream outputStream)
    {
        outputStream_ = outputStream;
    }

    protected void prepare()
    {
    }

    protected OutputStream getOutputStream()
    {
        return outputStream_;
    }

    protected void send(byte[] data)
    {
        try
        {
            if (outputStream_ == null)
            {
                setOutputStream(createOutputStream());
            }
            outputStream_.write(data);
            outputStream_.flush();
            postedToQueueSize_.addAndGet(-data.length);
            totalPostedToQueueSize_.addAndGet(data.length);

            // Рассчитываем количество сэмплов и продолжительность
            long totalSamples = totalPostedToQueueSize_.get() / 4; // 4 байта на стерео сэмпл (2 байта на канал)
            long expectedDurationInSeconds = (long) (totalSamples / Constants.SAMPLE_RATE);

            // Логирование
            logger.info(String.format("StereoSaver wrote %s seconds", expectedDurationInSeconds));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void postToQueue(byte[] data)
    {
        postedToQueueSize_.addAndGet(data.length);
        super.postToQueue(data);
        logger.info(String.format("StereoSaver posted %s seconds", data.length / 4 / Constants.SAMPLE_RATE));
    }

    @Override
    public boolean needWait()
    {
        return super.needWait() || postedToQueueSize_.get() > 0;
    }

    protected abstract String getFileName();

    private FileOutputStream createOutputStream()
    {
        String fileName = getFileName();
        File file = new File(fileName);
        if (file.exists())
        {
            file.delete();
        }
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(fileName, true);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return outputStream;
    }

    @Override
    public boolean isRunning()
    {
        return super.isRunning() && postedToQueueSize_.get() > 0;
    }

    protected void doAfterAll()
    {
        try
        {
            while (needWait())
            {
                Thread.sleep(100);
            }
            logger.info(String.format("%s is not running. Closing outputStream", getClass().getSimpleName()));
            outputStream_.flush();
            outputStream_.close(); // Закрытие потока записи
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
