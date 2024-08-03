package ivko.lana.hypnosys_audio_generator;

/**
 * @author Lana Ivko
 */

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

import java.io.File;
import java.io.IOException;

public class AudioGenerator
{

    public static void main(String[] args)
    {
        generateAndSaveMusic(60, "output.midi"); // Генерируем 60 секунд музыки
        convertMidiToWav("output.midi", "output.wav");
    }

    public static void generateAndSaveMusic(int durationSeconds, String outputFilePath)
    {
        Pattern pattern = generateMusicPattern(durationSeconds);
        saveMusicToFile(pattern, outputFilePath);
    }

    public static Pattern generateMusicPattern(int durationSeconds)
    {
        String[] notes = {"C", "D", "E", "F", "G", "A", "B"};
        StringBuilder musicString = new StringBuilder();

        for (int i = 0; i < durationSeconds * 2; i++)
        { // Каждый такт занимает полсекунды
            String note = notes[(int) (Math.random() * notes.length)];
            int octave = 4 + (int) (Math.random() * 3); // Октавы от 4 до 6
            int duration = 1 + (int) (Math.random() * 4); // Длительность ноты от 1 до 4
            musicString.append(note).append(octave).append("/").append(duration).append(" ");
        }

        return new Pattern(musicString.toString());
    }

    public static void saveMusicToFile(Pattern pattern, String outputFilePath)
    {
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

    public static void convertMidiToWav(String midiFilePath, String wavFilePath)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "fluidsynth",
                    "-f", "D:\\install\\midi_sounds_library\\fluidsynth.cfg",
                    "-ni",
                    "D:\\install\\midi_sounds_library\\FluidR3_GM.sf2",
                    midiFilePath,
                    "-F",
                    wavFilePath
            );
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

