package ivko.lana.neurotone.processing;

import ivko.lana.neurotone.IWaveGenerator;
import ivko.lana.neurotone.StereoPlayer;
import ivko.lana.neurotone.wave_generator.WaveType;
import ivko.lana.neurotone.wave_generator.melody.NoteGenerator;
import ivko.lana.neurotone.wave_generator.WaveDetail;
import ivko.lana.neurotone.wave_generator.WaveGenerator;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.wave_generator.FrequencyConverter;
import org.apache.commons.math3.analysis.function.Min;

import java.io.IOException;
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
public class TibetanGenerator
{
    private static final Logger logger = CustomLogger.getLogger(TibetanGenerator.class.getName());

    private final Mode mode_;

    private final List<StereoPlayer> stereoPlayers_;
    private volatile AtomicInteger processesCounter_;
    private final IWaveGenerator waveGenerator_;
    private ExecutorService executorService_;

    public TibetanGenerator(GeneratorType generatorType, Mode mode, Supplier<IWaveGenerator> waveGeneratorSupplier)
    {
        mode_ = mode;
        stereoPlayers_ = mode_.getStereoPlayers(generatorType);
        waveGenerator_ = waveGeneratorSupplier.get();
    }

    // Метод для запуска генерации музыки
    public void start()
    {
        executorService_ = Executors.newFixedThreadPool(stereoPlayers_.size());
        processesCounter_ = new AtomicInteger(0);

        try
        {
            long leftMs = Minutes_ * 60 * 1000;
            logger.info(String.format("Before generating we have %s (%s s, %s min) ms left", leftMs, leftMs / 1000, leftMs / 1000 / 60));
            do
            {
                waveGenerator_.generateMusic();
                // Создаем новый CountDownLatch с количеством стереоплееров
                CountDownLatch latch = new CountDownLatch(stereoPlayers_.size());
                CountDownLatch startSignal = new CountDownLatch(1); // Для одновременного запуска потоков

                WaveDetail leftChannel = waveGenerator_.getLeftChannel();
                WaveDetail rightChannel = waveGenerator_.getRightChannel();

                for (StereoPlayer player : stereoPlayers_)
                {
                    executorService_.submit(() ->
                    {
                        try
                        {
                            startSignal.await(); // Ожидание сигнала на запуск всех потоков
                            processesCounter_.incrementAndGet();
                            player.processData(leftChannel, rightChannel);
                            logger.info(String.format("Player processed %s seconds", leftChannel.getSamples().length / Constants.SAMPLE_RATE));
                        }
                        catch (Throwable e)
                        {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                        finally
                        {
                            processesCounter_.decrementAndGet();
                            latch.countDown(); // Уменьшаем счетчик latch после завершения работы потока
                        }
                    });
                }
                long createdMs = Util.convertSampleLengthToMs(leftChannel.getSamples().length) - Constants.FadeOutDurationMs_;
                leftMs -= createdMs;
                logger.info(String.format("After generating %s (%s s, %s min) ms we have %s (%s s, %s min) ms left",
                        createdMs, createdMs / 1000, createdMs / 1000 / 60, leftMs, leftMs / 1000, leftMs / 1000 / 60));

                // Все потоки готовы, даем стартовый сигнал
                startSignal.countDown();

                // Ожидаем завершения всех потоков в текущей итерации
                latch.await();
            }
            while (leftMs + Constants.FadeOutDurationMs_ >= 0);
        }
        catch (Throwable e)
        {
            logger.severe(String.valueOf(e));
            e.printStackTrace();
        }
        finally
        {
            stop();
        }
    }

    // Метод для остановки генерации музыки
    public void stop()
    {
        try
        {
            // Отправляем задачи на завершение работы плееров в тот же executorService
            CountDownLatch stopLatch = new CountDownLatch(stereoPlayers_.size());

            for (StereoPlayer player : stereoPlayers_)
            {
                executorService_.submit(() ->
                {
                    try
                    {
                        player.close();
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    finally
                    {
                        stopLatch.countDown(); // Уменьшаем счетчик latch после завершения работы потока
                    }
                });
            }

            // Ожидаем завершения всех задач закрытия плееров
            stopLatch.await();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        finally
        {
            // Закрываем ExecutorService
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
        }
    }

    public enum Mode
    {
        PLAY {@Override List<StereoPlayer> getStereoPlayers(GeneratorType generatorType) {return generatorType.getPlayers();}},
        SAVE {@Override List<StereoPlayer> getStereoPlayers(GeneratorType generatorType) {return generatorType.getSavers();}},
        PLAY_AND_SAVE {@Override List<StereoPlayer> getStereoPlayers(GeneratorType generatorType) {return Util.concatLists(generatorType.getPlayers(), generatorType.getSavers());}};

        abstract List<StereoPlayer> getStereoPlayers(GeneratorType generatorType);
    }

    private static IWaveGenerator getWaveGenerator(boolean isMock)
    {
        return isMock
                ? getMockGenerator()
                : new WaveGenerator(Minutes_);
    }

    private static IWaveGenerator getMockGenerator()
    {
        Iterator<double[][]> iterator = generateNoteSequence().iterator();
        FrequencyConverter frequencyConverter = new FrequencyConverter(Constants.WaveType_);
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

    private static List<double[][]> generateNoteSequence()
    {
//        double[][] notes1 =
//                {
//                        {1.0, 5000.0},
//                        {3.0, 5000.0},
//                        {5.0, 5000.0},
//                        {2.0, 5000.0},
//                        {4.0, 5000.0},
//                        {6.0, 5000.0}
//                };
//        double[][] notes2 =
//                {
//                        {3.0, 5000.0},
//                        {5.0, 5000.0},
//                        {7.0, 5000.0}
//                };
//        double[][] notes3 =
//                {
//                        {2.0, 5000.0},
//                        {4.0, 5000.0},
//                        {6.0, 5000.0},
//                        {2.0, 5000.0},
//                        {4.0, 5000.0},
//                        {6.0, 5000.0}
//                };
//        double[][] notes2 = {
//                {243.0, 2000.0},
//                {288.0, 2000.0},
//                {360.0, 2000.0},
//                {324.0, 2000.0},
//                {405.0, 2000.0},
//                {243.0, 2000.0},
//                {324.0, 2000.0},
//                {405.0, 2000.0},
//                {243.0, 2000.0}
//        };
        double[][] notes1 = {
                {1, 12000},
                {2, 12000},
                {1, 12000},
                {2, 12000},
                {3, 12000},
        };
//        double[][] notes1 = {
//                {1, 5000},
//                {2, 5000},
//                {3, 5000},
//                {4, 5000},
//                {5, 5000},
//                {6, 5000},
//                {7, 5000},
//                {8, 5000},
//                {9, 5000},
//        };
//        double[][] notes = {
//                {432.0, 2000.0},
//                {0.0, 2000.0},
//                {324.0, 2000.0},
//                {0.0, 200.0},
//                {324.0, 800.0},
//                {405.0, 800.0},
//                {243.0, 800.0}
//        };
//        double[][] notes = {
//                {216.0, 2000.0},
//                {270.0, 2000.0},
//                {324.0, 2000.0},
//                {243.0, 4000.0},
//                {288.0, 4000.0},
//                {360.0, 4000.0}
//        };

        List<double[][]> noteSequence = new ArrayList<>();
        noteSequence.add(notes1);
//        noteSequence.add(notes2);
//        noteSequence.add(notes3);

        try
        {
            for (double[][] note : noteSequence)
            {
                NotesSerializer.getInstance().serializeToCSV(note);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return noteSequence;
    }

    private static int Minutes_ = 1;
    public static final boolean IS_MOCK = false;
    public static void main(String[] args)
    {
        String minutesProperty = System.getProperty("minutes", "1");
        String baseFrequencyProperty = System.getProperty("baseFrequency", "174");
//        String baseFrequencyProperty = System.getProperty("baseFrequency", "963");
//        String baseFrequencyProperty = System.getProperty("baseFrequency", "741");
        String offsetProperty = System.getProperty("offset", "0");

        Minutes_ = Integer.parseInt(minutesProperty);
        double baseFrequency = Double.parseDouble(baseFrequencyProperty);
        double offset = Double.parseDouble(offsetProperty);

        NotesSerializer.initialize();
        Constants.setOneTone(true);
//        Constants.setPulsationDepth(1f);
        Constants.setPulsationSpeedFactor(0.4f);
        Constants.setVibrationFactor(1);
        Constants.setBeatDurationMs(4000);
        Constants.setSeparation(false);
        Constants.setBaseFrequency(baseFrequency);
        Constants.setWaveType(WaveType.SOLFEGE);
//        Constants.setUnitizationDividerFactor(0.5);
//        Constants.setFadeOutDurationMs(6000);
        Constants.setFrequencyOffset(offset);
        Constants.setScaleDegreeType(NoteGenerator.ScaleDegreeType.MAJOR);

        TibetanGenerator generator = new TibetanGenerator(GeneratorType.AUDIO, Mode.PLAY, () -> getWaveGenerator(IS_MOCK));
        try
        {
            generator.start();
        }
        catch (Throwable e)
        {
            generator.stop();
        }
    }
}
