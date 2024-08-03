package ivko.lana.neurotone.wave_generator;

/**
 * @author Lana Ivko
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class WavHeaderWriter {
    public static void main(String[] args) {
        String filename = "output.wav";
        int sampleRate = 44100;
        int numChannels = 2;
        int bitsPerSample = 16;
        byte[] data = generateSampleData();  // Генерация примера данных

        try {
            writeWavFile(filename, sampleRate, numChannels, bitsPerSample, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeWavFile(String filename, int sampleRate, int numChannels, int bitsPerSample, byte[] data) throws IOException {
        int byteRate = sampleRate * numChannels * bitsPerSample / 8;
        int dataSize = data.length;
        int totalDataLen = dataSize + 36;

        try (FileOutputStream fos = new FileOutputStream(new File(filename))) {
            // Запись заголовка
            fos.write(createWavHeader(sampleRate, numChannels, byteRate, bitsPerSample, dataSize, totalDataLen));
            // Запись данных
            fos.write(data);
        }
    }

    public static byte[] createWavHeader(int sampleRate, int numChannels, int byteRate, int bitsPerSample, int dataSize, int totalDataLen) {
        ByteBuffer header = ByteBuffer.allocate(44);

        // RIFF/WAVE header
        header.put(new byte[]{'R', 'I', 'F', 'F'});  // ChunkID
        header.putInt(totalDataLen);  // ChunkSize
        header.put(new byte[]{'W', 'A', 'V', 'E'});  // Format

        // fmt subchunk
        header.put(new byte[]{'f', 'm', 't', ' '});  // Subchunk1ID
        header.putInt(16);  // Subchunk1Size
        header.putShort((short) 1);  // AudioFormat (PCM)
        header.putShort((short) numChannels);  // NumChannels
        header.putInt(sampleRate);  // SampleRate
        header.putInt(byteRate);  // ByteRate
        header.putShort((short) (numChannels * bitsPerSample / 8));  // BlockAlign
        header.putShort((short) bitsPerSample);  // BitsPerSample

        // data subchunk
        header.put(new byte[]{'d', 'a', 't', 'a'});  // Subchunk2ID
        header.putInt(dataSize);  // Subchunk2Size

        return header.array();
    }

    public static byte[] generateSampleData() {
        // Пример генерации данных. Должны быть реальные аудиоданные.
        byte[] data = new byte[44100 * 2 * 2];  // 1 секунда стерео 16-битного звука
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (Math.sin(2 * Math.PI * i / (data.length / 10)) * 127);
        }
        return data;
    }
}
