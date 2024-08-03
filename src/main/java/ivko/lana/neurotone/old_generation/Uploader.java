package ivko.lana.neurotone.old_generation;


import ivko.lana.neurotone.youtube.YouTubeUploader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Lana Ivko
 */
public class Uploader
{

    private static final String FFMPEG_PATH = "C:\\ffmpeg\\bin\\ffmpeg";
    private static final String FFPROBE_PATH = "C:\\ffmpeg\\bin\\ffprobe";
    public static final String SOURCE_PATH = "D:\\VIDEO\\ANKULISTKA\\binaurals\\source\\";
    private static final String VIDEO_FILE_NAME = "delta_base.mp4";
    private static final String TEST_VIDEO_FILE_NAME = "1_minute_blue.mov";
    private static final String OUTPUT_DIRECTORY = "D:\\VIDEO\\ANKULISTKA\\binaurals\\results\\";
    public static final String LOG_CREATE_VIDEO_FILE_NAME = "createdVideos.txt";
    private static final String TEST_OUTPUT_DIRECTORY = "D:\\VIDEO\\ANKULISTKA\\binaurals\\result_for_test\\";

    private final String videoFilePath_;

    private Uploader()
    {
        videoFilePath_ = SOURCE_PATH + (VideoGeneratorConstants.IS_TEST ? TEST_VIDEO_FILE_NAME : VIDEO_FILE_NAME);
    }

    public static Uploader getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public void upload(VideoDetails videoDetails, int videosUploadedCounter)
    {
        String outputDirectory = (VideoGeneratorConstants.IS_TEST ? TEST_OUTPUT_DIRECTORY : OUTPUT_DIRECTORY) + videosUploadedCounter + "\\";
        Path path = Paths.get(outputDirectory);

        if (!Files.exists(path))
        {
            try
            {
                Files.createDirectories(path);
            }
            catch (IOException e)
            {
                System.err.println("Failed to create directory: " + e.getMessage());
            }
        }

        String outputFileName = videoDetails.getOutputFileName();
        String outputPreviewFileName = "preview_" + videoDetails.getOutputFileName() + ".jpg";
        String outputFilePath = outputDirectory + outputFileName;
        String outputPreviewPath = outputDirectory + outputPreviewFileName;
        System.out.printf("File %s does not exist. Starting generating...\n", outputFilePath);
        clearWorkingDirectory(outputDirectory);
        videoDetails.merge(FFMPEG_PATH, outputFilePath, outputPreviewPath, SOURCE_PATH);

        YouTubeUploader.getInstance().loadVideo(outputDirectory, outputFileName, outputPreviewFileName, videoDetails);
        logCreatedVideo(videoDetails);
        System.out.println("Video was loaded: " + outputFilePath);
    }

    private void logCreatedVideo(VideoDetails videoDetails)
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SOURCE_PATH + LOG_CREATE_VIDEO_FILE_NAME, true)))
        {
            String logInfo = videoDetails.getLogInfo();
            writer.println(logInfo);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void clearWorkingDirectory(String path)
    {
        try
        {
            Path directory = Paths.get(path);
            if (!Files.isDirectory(directory))
            {
                throw new IllegalArgumentException("The provided path is not a directory");
            }

            Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    if (exc == null)
                    {
                        return FileVisitResult.CONTINUE;
                    }
                    else
                    {
                        throw exc;
                    }
                }
            });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static class InstanceHolder
    {
        private static final Uploader INSTANCE = new Uploader();
    }
}
