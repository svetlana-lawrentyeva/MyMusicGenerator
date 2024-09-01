package ivko.lana.neurotone.processing;

import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.video_generator.VideoConstants;
import ivko.lana.neurotone.video_generator.VideoSaver;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class FfmpegProcess
{
    private static final Logger logger = CustomLogger.getLogger(FfmpegProcess.class.getName());

    private Process process_;

    private boolean needFfmpegLogs_ = true;
    public FfmpegProcess()
    {
        logger.info("Creating ffmpeg process");
        Util.deleteFileIfExists(VideoSaver.FILE_NAME);
        startFfmpegProcess();
        logger.info("Ffmpeg process was created");
    }

    private void startFfmpegProcess()
    {
        new Thread(() ->
        {
        try
        {
            // Путь к FFmpeg, если он не добавлен в PATH
            String ffmpegPath = "ffmpeg"; // Укажите полный путь к ffmpeg, если он не в PATH

            // Команда для FFmpeg
            String[] command = getCommandForVideoWithAudioSlow(ffmpegPath);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            logger.info("Executing command: " + Arrays.toString(command));

            if (needFfmpegLogs_)
            {
                processBuilder.redirectErrorStream(true);
            }
            try
            {
                process_ = processBuilder.start();
                StreamGobbler outputGobbler = null;
                StreamGobbler errorGobbler = null;
                if (needFfmpegLogs_)
                {
                    // Потоки для чтения вывода процесса
                    outputGobbler = new StreamGobbler(process_.getInputStream(), "OUTPUT");
                    errorGobbler = new StreamGobbler(process_.getErrorStream(), "ERROR");
                    outputGobbler.start();
                    errorGobbler.start();
                }

                    process_.waitFor();  // Ожидание завершения FFmpeg

                    if (outputGobbler != null && errorGobbler != null)
                    {
                        outputGobbler.join();
                        errorGobbler.join();
                    }
            }
            catch (IOException | InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            logger.info("Command is executed");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        }).start();
    }



    private static String[] getCommandForAudioVideoSynchronization(String ffmpegPath)
    {
        return new String[] {
                ffmpegPath,
                "-f", "rawvideo",
                "-pix_fmt", "rgb24",
                "-s", "1920x1080",
                "-r", String.valueOf(VideoConstants.FPS),    // Частота кадров
                "-i", "pipe:0",                              // Видеовход через стандартный ввод
                "-f", "s16le",
                "-ar", "44100",
                "-ac", "2",
                "-i", "pipe:3",                              // Аудиовход через pipe:3
                "-c:v", "libx265",                           // Кодек для видео
                "-b:v", "10M",                               // Установка битрейта видео в 10 Мбит/с
                "-preset", "slow",                           // Использование пресета "slow" для улучшения качества сжатия
                "-c:a", "aac",                               // Кодек для аудио
                "-b:a", "192k",                              // Битрейт для аудио
                VideoSaver.FILE_NAME                         // Имя выходного файла
        };
    }

    private static String[] getCommandForVideoOnly(String ffmpegPath) {
        return new String[] {
                ffmpegPath,
                "-f", "rawvideo",
                "-pix_fmt", "rgb24",
                "-s", "1920x1080",
                "-r", String.valueOf(VideoConstants.FPS),   // Частота кадров
                "-i", "pipe:0",                             // Видеовход через стандартный ввод (stdin)
                "-an",                                      // Без аудио
                "-c:v", "libx265",                          // Кодек для видео
                "-b:v", "10M",                              // Установка битрейта видео
                "-preset", "slow",                          // Пресет для улучшения качества сжатия
                "-f", "mp4",                                // Формат вывода
//                "-loglevel", "trace",                     // Уровень логирования
                VideoSaver.FILE_NAME                        // Имя выходного файла
        };
    }

    private static String[] getCommandForVideoWithAudioFast(String ffmpegPath) {
        return new String[] {
                ffmpegPath,
                "-f", "rawvideo",                           // Указание формата входного видеопотока
                "-pix_fmt", "rgb24",                        // Формат пикселей входного видеопотока
                "-s", "1920x1080",                          // Разрешение видео
                "-r", String.valueOf(VideoConstants.FPS),   // Частота кадров
                "-i", "pipe:0",                             // Видеовход через стандартный ввод (stdin)
                "-i", AudioSaver.getAudioFileName(),                 // Входной аудиофайл
                "-c:v", "libx264",                          // Кодек для видео
                "-b:v", "2M",                              // Битрейт видео
                "-preset", "ultrafast",                      // Пресет для улучшения качества сжатия
                "-c:a", "aac",                              // Кодек для аудио
                "-b:a", "192k",                             // Битрейт аудио
                VideoSaver.FILE_NAME                        // Имя выходного видеофайла
        };
    }

    private static String[] getCommandForVideoWithAudioSlow(String ffmpegPath) {
        return new String[] {
                ffmpegPath,
                "-f", "rawvideo",                         // Указание формата входного видеопотока
                "-pix_fmt", "rgb24",                      // Формат пикселей входного видеопотока
                "-s", "1920x1080",                        // Разрешение видео
                "-r", String.valueOf(VideoConstants.FPS), // Частота кадров
                "-i", "pipe:0",                           // Видеовход через стандартный ввод (stdin)
                "-i", AudioSaver.getAudioFileName(),               // Входной аудиофайл
                "-c:v", "libx265",                        // Кодек для видео
                "-b:v", "10M",                            // Битрейт видео
                "-preset", "slow",                        // Пресет для улучшения качества сжатия
                "-c:a", "aac",                            // Кодек для аудио
                "-b:a", "192k",                           // Битрейт аудио
                VideoSaver.FILE_NAME                      // Имя выходного видеофайла
        };
    }

    public boolean isInitialized()
    {
        return process_ != null;
    }

    public OutputStream getOutputStream()
    {
        return process_.getOutputStream();
    }

    private static class StreamGobbler extends Thread
    {
        private final InputStream inputStream;
        private final String type;

        public StreamGobbler(InputStream inputStream, String type)
        {
            this.inputStream = inputStream;
            this.type = type;
        }

        @Override
        public void run()
        {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    logger.info(type + "> " + line);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
