package ivko.lana.visualiser;

import javazoom.jl.converter.Converter;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class AudioRecorder
{
    private ByteArrayOutputStream byteArrayOutputStream;
    private TargetDataLine targetDataLine;
    private AudioFormat format;
    private Thread recordingThread;

    public AudioRecorder() throws LineUnavailableException
    {
        format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(format);
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public void startRecording()
    {
        targetDataLine.start();
        recordingThread = new Thread(() ->
        {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while (!Thread.currentThread().isInterrupted())
            {
                bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        });
        recordingThread.start();
    }

    public void stopRecording()
    {
        if (targetDataLine != null && targetDataLine.isOpen())
        {
            targetDataLine.stop();
            targetDataLine.close();
            System.out.println("Recording stopped.");
            recordingThread.interrupt();
        }
    }

    public byte[] getAudioData() throws IOException, InterruptedException
    {
        recordingThread.join();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public void saveToFile(String filePath) throws Exception
    {
        Sequence sequence = new Sequence(Sequence.PPQ, 24);
        Track track = sequence.createTrack();
//        writeToTrack(music_);
//        MidiSystem.write()


        byte[] audioData = getAudioData();
        File wavFile = new File("temp.wav");
        AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(audioData), format, audioData.length / format.getFrameSize());
        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavFile);

        Converter converter = new Converter();
        converter.convert(wavFile.getPath(), filePath);

        wavFile.delete(); // Удалить временный файл
    }
}
