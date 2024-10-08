package ivko.lana.converter;

/**
 * @author Lana Ivko
 */

import java.io.IOException;

public class MidiToAudioConverter
{

    public static void convertWithTimidity(String midiFilePath, String wavFilePath) throws IOException, InterruptedException
    {
        // Команда для запуска TiMidity++
        String command = String.format("timidity %s -Ow -o %s", midiFilePath, wavFilePath);

        // Запуск процесса
        Process process = Runtime.getRuntime().exec(command);

        // Ожидание завершения процесса
        int exitCode = process.waitFor();
        if (exitCode == 0)
        {
            System.out.println("Конвертация завершена успешно.");
        }
        else
        {
            System.err.println("Ошибка при конвертации. Код выхода: " + exitCode);
        }
    }

    public static void convertWithFluidSynth(String midiFilePath, String wavFilePath)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "fluidsynth",
                    "-f", "D:\\path\\to\\fluidsynth.cfg",
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

    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage: MidiToWavConverter <input midi file> <output wav file>");
            return;
        }

        String midiFilePath = args[0];
        String wavFilePath = args[1];

        try
        {
            convertWithTimidity(midiFilePath, wavFilePath);
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
