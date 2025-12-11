package ivko.lana.neurotone.util;

import ivko.lana.neurotone.processing.Constants;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Lana Ivko
 */
public class Panaromator
{
    private static final int BUFFER_SIZE = 4096;
    private static final int QUEUE_CAPACITY = 10;
    private static final int SAMPLE_RATE = 48000;


    public static void processAudioFile(File inputFile, File outputFile) throws Exception
    {
        AudioInputStream ais = AudioSystem.getAudioInputStream(inputFile);
        AudioFormat format = ais.getFormat();
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        boolean bigEndian = format.isBigEndian();

        try (AudioOutputStream audioOut = new AudioOutputStream(outputFile, inputFile))
        {
            BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch latch = new CountDownLatch(2);

            executor.submit(() ->
            {
                readAudioData(ais, queue);
                latch.countDown();
            });

            executor.submit(() ->
            {
                processAudioData(queue, audioOut, bytesPerSample, bigEndian);
                latch.countDown();
            });

            latch.await();
            executor.shutdown();
        }

        ais.close();
    }

    private static void readAudioData(AudioInputStream ais, BlockingQueue<byte[]> queue)
    {
        try
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = ais.read(buffer)) != -1)
            {
                queue.put(bytesRead == BUFFER_SIZE ? buffer.clone() : trimBuffer(buffer, bytesRead));
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

    private static int MIN_ = Integer.MAX_VALUE;
    private static int MAX_ = Integer.MIN_VALUE;
    private static final int MIDDLE = 8388608;

    private static void processAudioData(BlockingQueue<byte[]> queue, AudioOutputStream audioOut, int bytesPerSample, boolean bigEndian)
    {
        try
        {
            byte[] buffer;
            while ((buffer = queue.take()).length != 0)
            {
                int[] samples = bytesToInts(buffer, bytesPerSample, bigEndian);

                int[] processedSamples = addPulsation(samples);
//                int[] processedSamples = samples;
                for (int i = 0; i < samples.length; ++i)
                {
                    if (samples[i] > MAX_)
                    {
                        MAX_ = samples[i];
                    }
                    if (samples[i] < MIN_)
                    {
                        MIN_ = samples[i];
                    }
                }
                byte[] processedBytes = intsToBytes(processedSamples, bytesPerSample, bigEndian);
                audioOut.write(processedBytes);
                audioOut.flush();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static int[] addPulsation(int[] samples)
    {
        return addPulsationImpl(samples, 0.2, 0.0, 0.02); // minVolume = 0.2 → падение громкости до 20%
    }

    public static int[] addPulsationImpl(int[] samples, double basePulsationSpeed, double phaseShift, double minVolume)
    {
        int totalSamples = samples.length;
        int[] channel = new int[totalSamples];

        for (int i = 0; i < totalSamples; i++)
        {
            double time = (double) i / SAMPLE_RATE;

            // Корректная формула без DC-смещения
            double pulsation = minVolume + (1.0 - minVolume) * (1 + Math.sin(2 * Math.PI * basePulsationSpeed * time + phaseShift)) / 2;
            int adopted = samples[i] - MIDDLE;
            double shift = adopted * pulsation;

            // Применяем пульсацию
            channel[i] = (int) (MIDDLE + shift);

            // Ограничение значений
//            channel[i] = getLimitedValue(channel[i]);
//            channel[i] = samples[i];
        }

        return channel;
    }

    private static double createPanning(double basePulsationSpeed, double multiplier, int i, double phaseShift)
    {
        double sineValue = getSineValue(basePulsationSpeed, multiplier, i, phaseShift);

        // Смягчение переходов с меньшим скачком громкости
        double smoothingFactor = 0.9; // Ближе к 1 - более плавные переходы
        double smoothTransition = Math.signum(sineValue) * Math.pow(Math.abs(sineValue), smoothingFactor);

        // Ограничиваем максимальную глубину пульсации, чтобы избежать треска
        double minDepth = 0.90;
        return minDepth + (1.0 - minDepth) * smoothTransition;
    }

    private static double getSineValue(double basePulsationSpeed, double multiplier, int i, double phaseShift)
    {
        double time = i / SAMPLE_RATE; // Время в секундах

        // Корректное вычисление скорости
        double pulsation = Constants.PulsationSpeedFactor_ * (basePulsationSpeed * multiplier) * time;

        // Коррекция фазового сдвига
        double shift = Math.PI / 2 * (1.0 / Math.max(multiplier, 1.0));

        return Math.sin(2 * Math.PI * pulsation - shift + phaseShift);
    }

    public static int getLimitedValue(int originalValue)
    {
        return Math.max(Math.min(originalValue, Integer.MAX_VALUE), Integer.MIN_VALUE);


//        int max24Bit = 8388607;
//        int min24Bit = -8388608;
//        return Math.max(Math.min(originalValue, max24Bit), min24Bit);
    }

    private static int[] bytesToInts(byte[] bytes, int bytesPerSample, boolean bigEndian)
    {
        int[] samples = new int[bytes.length / bytesPerSample];

        for (int i = 0; i < samples.length; i++)
        {
            int index = i * bytesPerSample;
            int sample = 0;

            if (bytesPerSample == 3)
            {
                if (bigEndian)
                {
                    sample = ((bytes[index] & 0xFF) << 16) |
                            ((bytes[index + 1] & 0xFF) << 8) |
                            (bytes[index + 2] & 0xFF);
                }
                else
                {
                    sample = (bytes[index] & 0xFF) |
                            ((bytes[index + 1] & 0xFF) << 8) |
                            ((bytes[index + 2] & 0xFF) << 16);
                }

                // Коррекция знака, если число больше 0x7FFFFF (максимальный 24-битный signed)
                if (sample > 0x7FFFFF)
                {
                    sample -= 0x1000000;
                }
            }
            else if (bytesPerSample == 2)
            {
                if (bigEndian)
                {
                    sample = ((bytes[index] << 8) | (bytes[index + 1] & 0xFF));
                }
                else
                {
                    sample = (bytes[index] & 0xFF) | (bytes[index + 1] << 8);
                }
            }
            samples[i] = sample;
        }
        return samples;
    }

    private static byte[] intsToBytes(int[] samples, int bytesPerSample, boolean bigEndian)
    {
        byte[] bytes = new byte[samples.length * bytesPerSample];

        for (int i = 0; i < samples.length; i++)
        {
            int index = i * bytesPerSample;
            int sample = samples[i];

            if (bytesPerSample == 3)
            {
                // Приведение к диапазону 24-битного PCM (signed)
                sample = sample & 0xFFFFFF; // Оставляем только 24 бита

                if (sample > 0x7FFFFF) // Коррекция знака, если больше максимального значения signed 24-bit
                {
                    sample -= 0x1000000;
                }

                if (bigEndian)
                {
                    bytes[index] = (byte) ((sample >> 16) & 0xFF);
                    bytes[index + 1] = (byte) ((sample >> 8) & 0xFF);
                    bytes[index + 2] = (byte) (sample & 0xFF);
                }
                else
                {
                    bytes[index] = (byte) (sample & 0xFF);
                    bytes[index + 1] = (byte) ((sample >> 8) & 0xFF);
                    bytes[index + 2] = (byte) ((sample >> 16) & 0xFF);
                }
            }
            else if (bytesPerSample == 2)
            {
                if (bigEndian)
                {
                    bytes[index] = (byte) ((sample >> 8) & 0xFF);
                    bytes[index + 1] = (byte) (sample & 0xFF);
                }
                else
                {
                    bytes[index] = (byte) (sample & 0xFF);
                    bytes[index + 1] = (byte) ((sample >> 8) & 0xFF);
                }
            }
            else
            {
                bytes[index] = (byte) (sample & 0xFF);
            }
        }
        return bytes;
    }

    static class AudioOutputStream extends FileOutputStream
    {
        private final File file;
        private int dataSize = 0;
        private final Map<String, byte[]> originHeader;

        public AudioOutputStream(File file, File inputFile) throws Exception
        {
            super(file);
            this.file = file;
            this.originHeader = readWavHeader(inputFile);
            writeWavHeaderWithArray(this, 0, originHeader);
        }

        private static int readIntLE(RandomAccessFile raf) throws Exception
        {
            byte[] buffer = new byte[4];
            raf.read(buffer);
            return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }

        private static short readShortLE(RandomAccessFile raf) throws Exception
        {
            byte[] buffer = new byte[2];
            raf.read(buffer);
            return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
        }

        @Override
        public void write(byte[] data) throws IOException
        {
            super.write(data);
            dataSize += data.length;
        }

        @Override
        public void close() throws IOException
        {
            super.close();
            updateWavHeader();
        }

        private void writeWavHeaderWithArray(OutputStream out, long dataSize, Map<String, byte[]> originHeader) throws IOException
        {
            int subchunk1Size = ByteBuffer.wrap(originHeader.get("Subchunk1 Size")).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int extraBytes = subchunk1Size - 16; // Определяем дополнительные байты
            byte[] fmtData = originHeader.get("fmt");

            byte[] header = new byte[44 + extraBytes];

            long totalDataLen = dataSize + 36 + extraBytes;
            byte[] audioFormat = originHeader.get("Audio Format");
            byte[] numChannels = originHeader.get("Num Channels");
            byte[] sampleRate = originHeader.get("Sample Rate");
            byte[] byteRate = originHeader.get("Byte Rate");
            byte[] blockAlign = originHeader.get("Block Align");
            byte[] bitsPerSample = originHeader.get("Bits Per Sample");
            byte[] dataHeader = originHeader.get("Data Header");

            // RIFF header
            header[0] = 'R';
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';

            byte[] totalSizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) totalDataLen).array();
            System.arraycopy(totalSizeBytes, 0, header, 4, 4);

            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';

            // fmt chunk
            header[12] = 'f';
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';

            byte[] subchunkSizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(subchunk1Size).array();
            System.arraycopy(subchunkSizeBytes, 0, header, 16, 4);

            System.arraycopy(audioFormat, 0, header, 20, 2);
            System.arraycopy(numChannels, 0, header, 22, 2);
            System.arraycopy(sampleRate, 0, header, 24, 4);
            System.arraycopy(byteRate, 0, header, 28, 4);
            System.arraycopy(blockAlign, 0, header, 32, 2);
            System.arraycopy(bitsPerSample, 0, header, 34, 2);

            // Копируем дополнительные байты, если они есть
            if (extraBytes > 0)
            {
                System.arraycopy(fmtData, 16, header, 36, extraBytes);
            }

            // data chunk
            header[36 + extraBytes] = 'd';
            header[37 + extraBytes] = 'a';
            header[38 + extraBytes] = 't';
            header[39 + extraBytes] = 'a';

            byte[] dataSizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) dataSize).array();
            System.arraycopy(dataSizeBytes, 0, header, 40 + extraBytes, 4);

            out.write(header);
        }

        public static void main(String[] args) throws Exception
        {
            File inputFile = new File("F:\\MUSIC\\music\\automation\\forMidi.wav");
            File outputFile = new File("F:\\MUSIC\\music\\automation\\output.wav");

            if (outputFile.exists())
            {
                outputFile.delete();
            }

            processAudioFile(inputFile, outputFile);
            outputFile = new File("F:\\MUSIC\\music\\automation\\output.wav");

            System.out.println("OUTPUT:");
            readWavHeader(outputFile);

            System.out.println(String.format("min: %s; max: %s", MIN_, MAX_));
        }

        public static Map<String, byte[]> readWavHeader(File file) throws Exception
        {
            Map<String, byte[]> result = new LinkedHashMap<>();
            try (RandomAccessFile raf = new RandomAccessFile(file, "r"))
            {
                byte[] chunkId = new byte[4];
                raf.read(chunkId);
                int fileSize = Integer.reverseBytes(raf.readInt()) + 8;
                byte[] format = new byte[4];
                raf.read(format);

                result.put("RIFF", chunkId);
                result.put("File Size", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(fileSize).array());
                result.put("WAVE", format);

                while (raf.getFilePointer() < raf.length())
                {
                    byte[] chunkType = new byte[4];
                    raf.read(chunkType);
                    int chunkSize = readIntLE(raf);

                    String chunkName = new String(chunkType).trim();

                    if (chunkName.equals("fmt"))
                    {
                        byte[] fmtData = new byte[chunkSize];
                        raf.readFully(fmtData);
                        result.put("fmt", fmtData);

                        ByteBuffer buffer = ByteBuffer.wrap(fmtData).order(ByteOrder.LITTLE_ENDIAN);

                        result.put("Subchunk1 Size", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(chunkSize).array());
                        result.put("Audio Format", ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(buffer.getShort(0)).array());
                        result.put("Num Channels", ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(buffer.getShort(2)).array());
                        result.put("Sample Rate", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(buffer.getInt(4)).array());
                        result.put("Byte Rate", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(buffer.getInt(8)).array());
                        result.put("Block Align", ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(buffer.getShort(12)).array());
                        result.put("Bits Per Sample", ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(buffer.getShort(14)).array());
                    }
                    else if (chunkName.equals("data"))
                    {
                        result.put("Data Header", chunkType);
                        result.put("Data Size", ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(chunkSize).array());
                        break; // Останавливаем обработку, т.к. дальше только аудиоданные
                    }
                    else
                    {
                        byte[] chunkData = new byte[chunkSize];
                        raf.readFully(chunkData);
                        result.put(chunkName, chunkData); // Сохраняем неизвестные чанки, включая JUNK
                    }
                }
            }

            for (String key : result.keySet())
            {
                byte[] bytes = result.get(key);
                String results = convertBytesToReadableString1(bytes);
                System.out.println(String.format("%S: %s", key, results));
            }
            return result;
        }

        private static String convertBytesToReadableString(byte[] bytes)
        {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
            {
                sb.append(String.format("%02X ", b)); // Вывод в HEX-формате
            }
            return sb.toString().trim();
        }

        public static String convertBytesToReadableString1(byte[] bytes)
        {
            if (bytes == null || bytes.length == 0)
            {
                return "empty";
            }

            StringBuilder sb = new StringBuilder();

            if (bytes.length == 2)
            {
                sb.append(Short.toString(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort()));
            }
            else if (bytes.length == 4)
            {
                sb.append(Integer.toString(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt()));
            }
            else if (bytes.length == 8)
            {
                sb.append(Long.toString(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong()));
            }
            else
            {
                // Если это строка (например, "RIFF", "fmt ")
                String asString = new String(bytes).replaceAll("[^\\x20-\\x7E]", ""); // Фильтр печатаемых символов
                sb.append(asString.isEmpty() ? bytesToHex(bytes) : asString);
            }

            return sb.toString();
        }

        public static String bytesToHex(byte[] bytes)
        {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
            {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString().trim();
        }


        private void updateWavHeader()
        {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw"))
            {
                raf.seek(0); // Переход в начало файла
                writeWavHeaderWithArray(new FileOutputStream(raf.getFD()), dataSize, originHeader);
            }
            catch (FileNotFoundException e)
            {
                throw new RuntimeException(e);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

    }
}
