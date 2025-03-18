package ivko.lana.neurotone.util;

import javax.sound.midi.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.Random;
import java.util.concurrent.*;

public class FakeMidiCreator
{
    private static final int BUFFER_SIZE = 8192;
    private static final int QUEUE_CAPACITY = 10;
    public static final String BASE_DIRECTORY = "F:\\MUSIC\\music2\\";
    public static final String OUTPUT_MID = BASE_DIRECTORY + "output.mid";
    public static final String INPUT_WAV = BASE_DIRECTORY + "audio.wav";
    private static final String LOG_FILE = BASE_DIRECTORY + "log.txt";
    private static final double NOTES_PER_SECOND = 0.05; // Базовое количество нот в секунду (диапазон ±5)


    private long globalTick = 0; // Поле для отслеживания глобального значения тика

    private int globalSampleIndex = 0;
    private Track track;
    private Sequence sequence;
    private int notesCount = 0;

    private static void log(String message)
    {
        System.out.println(message);
        try (FileWriter fw = new FileWriter(LOG_FILE, true))
        {
            fw.write(message + "\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processAudioFile(File inputFile) throws Exception
    {
        AudioInputStream ais = AudioSystem.getAudioInputStream(inputFile);
        long totalBytes = inputFile.length();
        long[] processedBytes = {0};

        BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        AudioFormat format = ais.getFormat();
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int numChannels = format.getChannels();
        long totalSamples = (inputFile.length() - 44) / (bytesPerSample * numChannels); // Вычитаем заголовок WAV

        int audioDurationInSeconds = (int) Math.ceil(totalSamples / 48000.0);
        log("Общая длительность аудиофайла (сек): " + audioDurationInSeconds);

        executor.submit(() ->
        {
            readAudioData(ais, queue, totalBytes, processedBytes);
            latch.countDown();
        });

        sequence = new Sequence(Sequence.PPQ, 480);
        track = sequence.createTrack();
        executor.submit(() ->
        {
            log("Обрабатываем новый буфер аудиоданных...");
            processAudioData(queue);
            latch.countDown();
        });

        latch.await();
        executor.shutdown();
        ais.close();

        if (notesCount > 0)
        {
            File midiFile = new File(OUTPUT_MID);
            MidiSystem.write(sequence, 1, midiFile);
        }
        else
        {
            log("MIDI-файл не записан, так как не было добавлено нот.");
        }
        log("Количество тиков в MIDI: " + globalTick);
    }

    private static void readAudioData(AudioInputStream ais, BlockingQueue<byte[]> queue, long totalBytes, long[] processedBytes)
    {
        try
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = ais.read(buffer)) != -1)
            {
                queue.put(bytesRead == BUFFER_SIZE ? buffer.clone() : trimBuffer(buffer, bytesRead));
                processedBytes[0] += bytesRead;

                if (processedBytes[0] % 10_000_000 < BUFFER_SIZE)
                {
                    double progress = (processedBytes[0] / (double) totalBytes) * 100;
                    log(String.format("Прогресс: %.2f%% (%d/%d байт)", progress, processedBytes[0], totalBytes));
                }
            }
            queue.put(new byte[0]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static byte[] trimBuffer(byte[] buffer, int bytesRead)
    {
        byte[] trimmed = new byte[bytesRead];
        System.arraycopy(buffer, 0, trimmed, 0, bytesRead);
        return trimmed;
    }

    private void processAudioData(BlockingQueue<byte[]> queue)
    {
        try
        {
            byte[] buffer;
            while ((buffer = queue.take()).length != 0)
            {
                double[] amplitudes = extractAmplitudes(buffer);
                if (amplitudes.length > 0)
                {
                    createMidiFromAmplitudes(amplitudes);
                    globalSampleIndex += amplitudes.length; // Увеличиваем индекс на длину обработанного буфера

                }
            }
        }
        catch (Exception e)
        {
            log("Ошибка в processAudioData: " + e.getMessage());
        }
    }


    private static double[] extractAmplitudes(byte[] audioData)
    {
        double[] amplitudes = new double[audioData.length / 2];
        for (int i = 0; i < amplitudes.length; i++)
        {
            int sample = (audioData[2 * i] & 0xFF) | (audioData[2 * i + 1] << 8);
            amplitudes[i] = Math.abs(sample / 32768.0);
        }
        return amplitudes;
    }

    private void createMidiFromAmplitudes(double[] amplitudes) throws Exception
    {

        int samplesPerSecond = 48000;
        int sampleStep = samplesPerSecond / 2; // Берём амплитуду раз в 0.5 секунды
        double secondsPerSample = 1.0 / samplesPerSecond;
        int tickMultiplier = 480;

        for (int i = 0; i < amplitudes.length; i += sampleStep)
        {
            long sampleIndex = globalSampleIndex + i;
            double timeInSeconds = sampleIndex * secondsPerSample;
            int tick = (int) (timeInSeconds * tickMultiplier);

            globalTick = tick; // Сохраняем глобальное значение тика

            double amplitude = amplitudes[i] * 2.9;
            int velocity = Math.min((int) (amplitude * 127), 127);

            int note = 60 + (int) (amplitude * 30);
            note = Math.max(21, Math.min(108, note));
            velocity = Math.max(30, Math.min(127, velocity));

            notesCount++;
            log("Добавлена нота: " + note + ", Velocity: " + velocity + ", Tick: " + tick);
            track.add(createMidiEvent(ShortMessage.NOTE_ON, 0, note, velocity, tick));
            track.add(createMidiEvent(ShortMessage.NOTE_OFF, 0, note, 0, tick + 240));
        }
    }

    private static MidiEvent createMidiEvent(int command, int channel, int note, int velocity, int tick)
            throws Exception
    {
        ShortMessage message = new ShortMessage();
        message.setMessage(command, channel, note, velocity);
        return new MidiEvent(message, tick);
    }

    public static void main(String[] args) throws Exception
    {
        File outputFile = new File(OUTPUT_MID);
        if (outputFile.exists())
        {
            outputFile.delete();
        }
        File logFile = new File(LOG_FILE);
        if (logFile.exists())
        {
            logFile.delete();
        }
        FakeMidiCreator fakeMidiCreator = new FakeMidiCreator();

        File inputFile = new File(INPUT_WAV);
        fakeMidiCreator.processAudioFile(inputFile);
        log("Обработка завершена.");
    }
}
