package ivko.lana.neurotone.processing;

import ivko.lana.neurotone.IWaveGenerator;
import ivko.lana.neurotone.StereoPlayer;
import ivko.lana.neurotone.StereoSaver;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.video_generator.VideoSaver;
import ivko.lana.neurotone.wave_generator.FrequencyConverter;
import ivko.lana.neurotone.wave_generator.SavedWaveSupplier;
import ivko.lana.neurotone.wave_generator.WaveDetail;
import ivko.lana.neurotone.wave_generator.WaveGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class VideoProcessor
{
    private static final int BUFFER_SIZE = 65536; // 64 KB

    private static final Logger logger = CustomLogger.getLogger(VideoProcessor.class.getName());
    private final List<StereoPlayer> stereoPlayers_;
    private volatile AtomicInteger processesCounter_;
    private final IWaveGenerator waveGenerator_;
    private ExecutorService executorService_;
    private OutputStream ffmpegIn_;

    public VideoProcessor(Supplier<IWaveGenerator> waveGeneratorSupplier)
    {
        FfmpegProcess ffmpegProcess = new FfmpegProcess();
        while (!ffmpegProcess.isInitialized())
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        ffmpegIn_ = ffmpegProcess.getOutputStream();
        stereoPlayers_ = List.of(initializeSaverAndGet(new VideoSaver())/*, initializeSaverAndGet(new AudioSaver())*/);
        waveGenerator_ = waveGeneratorSupplier.get();
    }

    private StereoSaver initializeSaverAndGet(StereoSaver saver)
    {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(ffmpegIn_, BUFFER_SIZE);
        saver.setOutputStream(bufferedOutputStream);
        return saver;
    }

    public void start()
    {
        executorService_ = Executors.newFixedThreadPool(stereoPlayers_.size());
        processesCounter_ = new AtomicInteger(0);

        try
        {
            while (waveGenerator_.generateMusic())
            {
                logger.info("Создаем новый CountDownLatch с количеством стереоплееров");
                CountDownLatch latch = new CountDownLatch(stereoPlayers_.size());
                CountDownLatch startSignal = new CountDownLatch(1); // Для одновременного запуска потоков

                boolean isFull = false;
                for (StereoPlayer player : stereoPlayers_)
                {
                    executorService_.submit(() ->
                    {
                        try
                        {
                            logger.info("Ожидание сигнала на запуск всех потоков");
                            startSignal.await();
                            processesCounter_.incrementAndGet();
                            player.processData(waveGenerator_.getLeftChannel(), waveGenerator_.getRightChannel());
                            logger.info("Данные были отправлены в очередь");
                        }
                        catch (Throwable e)
                        {
                            logger.severe(String.format("Stop all processes due to Unexpected exception in %s: %s", VideoSaver.class.getSimpleName(), e));
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                        finally
                        {
                            logger.info("Уменьшаем счетчик latch после завершения работы потока");
                            processesCounter_.decrementAndGet();
                            latch.countDown();
                        }
                    });

                    isFull |= player.isFull();
                }

                while (isFull)
                {
                    logger.info(String.format("Sleeping for 1 second. Some saver is busy."));
                    Thread.sleep(1000);

                    isFull = false;
                    for (StereoPlayer player : stereoPlayers_)
                    {
                        isFull |= player.isFull();
                    }
                }

                logger.info("Все потоки готовы, даем стартовый сигнал");
                startSignal.countDown();

                logger.info("Ожидаем завершения всех потоков в текущей итерации");
                latch.await();
            }
        }
        catch (Exception e)
        {
            logger.severe(String.valueOf(e));
            e.printStackTrace();
        }
        finally
        {
            stop();
        }
    }

    public void stop()
    {
        try
        {
            logger.info("Отправляем задачи на завершение работы плееров в тот же executorService");
            CountDownLatch stopLatch = new CountDownLatch(stereoPlayers_.size());

            for (StereoPlayer player : stereoPlayers_)
            {
                executorService_.submit(() ->
                {
                    try
                    {
                        logger.info(String.format("%s is finished. Closing it", player.getClass().getSimpleName()));
                        player.close();
                        while (player.isRunning())
                        {
                            logger.info(String.format("%s is not finished yet. Sleep for 100 ms", player.getClass().getSimpleName()));
                            Thread.sleep(100);
                        }
                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                    finally
                    {
                        logger.info("Уменьшаем счетчик latch после завершения работы потока");
                        stopLatch.countDown();
                    }
                });
            }

            logger.info("Ожидаем завершения всех задач закрытия плееров");
            stopLatch.await();

        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        finally
        {
            logger.info("Закрываем ExecutorService");
            if (executorService_ != null)
            {
                executorService_.shutdown();
                try
                {
                    if (!executorService_.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS))
                    {
                        executorService_.shutdownNow();
                    }
                }
                catch (InterruptedException e)
                {
                    executorService_.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            try
            {
                logger.info("Ffmpeg is going to be closed");
                ffmpegIn_.flush();
                ffmpegIn_.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            ffmpegIn_ = null;
            logger.info("Ffmpeg is closed");
        }
    }

    private static List<double[][]> generateNoteSequence()
    {
        double[][] notes = {
                {432.0, 1000},
        };

        List<double[][]> noteSequence = new ArrayList<>();
        noteSequence.add(notes);

        return noteSequence;
    }

    private static final int RHYTHM_NUMBER = 1;
    private static final boolean IS_MOCK = true;

    public static void main(String[] args)
    {
        int baseFrequency = 216;
        Constants.setBaseFrequency(baseFrequency);
        Constants.setPulsationDepth(0.1f);
        Constants.setPulsationSpeedFactor(0.1f);
        Constants.setFrequencyOffset(baseFrequency / 2);
//        WaveGeneratorType waveGeneratorType = WaveGeneratorType.REAL;
        WaveGeneratorType waveGeneratorType = WaveGeneratorType.SAVED;

        VideoProcessor generator = new VideoProcessor(waveGeneratorType::getWaveGenerator);
        generator.start();
    }

    private enum WaveGeneratorType
    {
        MOCK {@Override public IWaveGenerator getWaveGenerator() {return getMockGenerator();}},
        REAL {@Override public IWaveGenerator getWaveGenerator() {return new WaveGenerator(RHYTHM_NUMBER);}},
        SAVED {@Override public IWaveGenerator getWaveGenerator() {return getSavedGenerator();}};

        public abstract IWaveGenerator getWaveGenerator();
    }

    private static IWaveGenerator getSavedGenerator()
    {
        return new SavedWaveSupplier();
    }

    private static IWaveGenerator getMockGenerator()
    {
        Iterator<double[][]> iterator = generateNoteSequence().iterator();
        FrequencyConverter frequencyConverter = new FrequencyConverter();
        return new IWaveGenerator()
        {
            @Override
            public boolean generateMusic()
            {
                boolean hasNext = iterator.hasNext();
                if (hasNext)
                {
                    frequencyConverter.convert(iterator.next());
                }
                return hasNext;
            }

            @Override
            public WaveDetail getLeftChannel()
            {
                return frequencyConverter.getLeftChannel();
            }

            @Override
            public WaveDetail getRightChannel()
            {
                return frequencyConverter.getRightChannel();
            }
        };
    }
}

