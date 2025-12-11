package ivko.lana.neurotone.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class WavHeaderReader
{
    public static void main(String[] args) throws Exception
    {
        Multimap<String, String> result = ArrayListMultimap.create();

        String[] fileNames =
                {
                        "F:\\MUSIC\\music\\automation\\forMidi.wav",
                        "F:\\MUSIC\\music\\automation\\output.wav",
                };

        for (String fileName : fileNames)
        {
            File output = new File(fileName);
            readWavHeader(output, result);
        }

        for (String key : result.keySet())
        {
            List<String> answers = new ArrayList<>(result.get(key));
            String results = String.join(" -> ", answers);
            System.out.println(String.format("%S: %s", key, results));
        }
    }

    public static void readWavHeader(File file, Multimap<String, String> result) throws Exception
    {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r"))
        {
            byte[] chunkId = new byte[4];
            raf.read(chunkId);
            int fileSize = Integer.reverseBytes(raf.readInt()) + 8;
            byte[] format = new byte[4];
            raf.read(format);

            result.put("RIFF", new String(chunkId));
            result.put("File Size", String.valueOf(fileSize));
            result.put("WAVE", new String(format));

            while (raf.getFilePointer() < raf.length())
            {
                byte[] chunkType = new byte[4];
                raf.read(chunkType);
                int chunkSize = readIntLE(raf);

                String chunkName = new String(chunkType).trim();
                if (chunkName.equals("fmt"))
                {
                    short audioFormat = readShortLE(raf);
                    short numChannels = readShortLE(raf);
                    int sampleRate = readIntLE(raf);
                    int byteRate = readIntLE(raf);
                    short blockAlign = readShortLE(raf);
                    short bitsPerSample = readShortLE(raf);

                    result.put("fmt", "fmt");
                    result.put("Subchunk1 Size", String.valueOf(chunkSize));
                    result.put("Audio Format", String.valueOf(audioFormat));
                    result.put("Num Channels", String.valueOf(numChannels));
                    result.put("Sample Rate", String.valueOf(sampleRate));
                    result.put("Byte Rate", String.valueOf(byteRate));
                    result.put("Block Align", String.valueOf(blockAlign));
                    result.put("Bits Per Sample", String.valueOf(bitsPerSample));

                    raf.seek(raf.getFilePointer() + chunkSize - 16);
                }
                else if (chunkName.equals("data"))
                {
                    result.put("Data Header", "data");
                    result.put("Data Size", String.valueOf(chunkSize));
                    break;
                }
                else
                {
                    raf.seek(raf.getFilePointer() + chunkSize);
                }
            }
        }
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
}
