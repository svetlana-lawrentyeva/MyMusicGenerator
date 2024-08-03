package ivko.lana.hypnosys_audio_generator;

/**
 * @author Lana Ivko
 */

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

import java.io.File;
import java.io.IOException;

public class SimpleMidiCreator
{

    public static void main(String[] args)
    {
        createSimpleMidi("test.midi");
    }

    public static void createSimpleMidi(String outputFilePath)
    {
        Pattern pattern = new Pattern("C D E F G A B");
        try
        {
            File midiFile = new File(outputFilePath);
            MidiFileManager.savePatternToMidi(pattern, midiFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

