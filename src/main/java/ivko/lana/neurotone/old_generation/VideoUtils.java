package ivko.lana.neurotone.old_generation;

/**
 * @author Lana Ivko
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VideoUtils
{

    public static final String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg";
    public static final String ffprobePath = "C:\\ffmpeg\\bin\\ffprobe";
    public static final String videoFilePath = "D:\\VIDEO\\ANKULISTKA\\binaurals\\174Hz.mov";
    public static final String frequencyFilePath = "/frequencies_test.txt";
    public static final String outputDirectory = "D:\\VIDEO\\ANKULISTKA\\binaurals\\results\\";

    public static double getVideoDuration(String ffprobePath, String videoFilePath)
    {
        ProcessBuilder processBuilder = new ProcessBuilder(
                ffprobePath,
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoFilePath
        );
        processBuilder.redirectErrorStream(true);
        try
        {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            reader.close();

            if (line == null || line.trim().isEmpty())
            {
                throw new RuntimeException("Failed to get duration from FFmpeg output.");
            }

            return Double.parseDouble(line.trim());
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String formatFrequency(String frequency)
    {
        return frequency.replaceAll("(?<=\\d)\\.0(?=\\D|$)", "");
    }
}


