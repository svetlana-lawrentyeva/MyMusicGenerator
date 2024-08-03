package ivko.lana.instruments_for_test;

/**
 * @author Lana Ivko
 */

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AudioAnalyzer
{

    public static void analyzeFrequencyAndMagnitude(String filePath, PrintWriter writer) throws UnsupportedAudioFileException, IOException, InterruptedException
    {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(new File(filePath), 1024, 512);

        writer.println("Frequency\tMagnitude");
        dispatcher.addAudioProcessor(new AudioProcessor()
        {
            FFT fft = new FFT(1024, new HammingWindow());

            @Override
            public boolean process(AudioEvent audioEvent)
            {
                float[] audioFloatBuffer = audioEvent.getFloatBuffer();
                float[] transformBuffer = new float[fft.size()];
                System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformBuffer);
                float[] magnitudes = new float[fft.size() / 2];
                fft.modulus(transformBuffer, magnitudes);

                // Output magnitudes and frequencies to file
                for (int i = 0; i < magnitudes.length; i++)
                {
                    if (magnitudes[i] > 0.01)
                    {  // Threshold to filter out noise
                        writer.printf("%.2f\t%.2f%n", fft.binToHz(i, audioEvent.getSampleRate()), magnitudes[i]);
                    }
                }
                return true;
            }

            @Override
            public void processingFinished()
            {
            }
        });

        Thread dispatcherThread = new Thread(dispatcher);
        dispatcherThread.start();
        dispatcherThread.join(); // Wait for the dispatcher to finish processing
    }


    public static void analyzePitch(String filePath, PrintWriter writer) throws UnsupportedAudioFileException, IOException, InterruptedException
    {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(new File(filePath), 1024, 512);

        writer.println("Pitch");
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 44100, 1024, new PitchDetectionHandler()
        {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent)
            {
                final float pitchInHz = pitchDetectionResult.getPitch();
                if (pitchInHz != -1)
                {
                    writer.println(pitchInHz);
                }
            }
        }));

        Thread dispatcherThread = new Thread(dispatcher);
        dispatcherThread.start();
        dispatcherThread.join(); // Wait for the dispatcher to finish processing
    }

    public static void main(String[] args)
    {
        String[] filePaths = {
//                "C:\\Users\\slana\\Downloads\\example.wav",
//                "C:\\Users\\slana\\Downloads\\example1.wav",
                "D:/VIDEO/NEUROTONE/EXAMPLE.wav"
        };

        try (PrintWriter writer = new PrintWriter(new FileWriter("analysis_FrequencyAndMagnitude.txt", true)))
        {
            for (String filePath : filePaths)
            {
                analyzeFrequencyAndMagnitude(filePath, writer);
            }
        }
        catch (UnsupportedAudioFileException | IOException | InterruptedException e)
        {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("analysis_Pitch.txt", true)))
        {
            for (String filePath : filePaths)
            {
                analyzePitch(filePath, writer);
            }
        }
        catch (UnsupportedAudioFileException | IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}




