package ivko.lana.neurotone.youtube;

/**
 * @author Lana Ivko
 */


import ivko.lana.neurotone.old_generation.Main;
import ivko.lana.neurotone.old_generation.Uploader;
import ivko.lana.neurotone.old_generation.VideoDetails;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YouTubeUploadService
{
    private static final String FREQUENCY_TEST_FILE_PATH = "/frequencies_test.txt";
    private static final String FREQUENCY_FILE_PATH = "/frequencies.txt";

    private static final String AMERICA_ZONE_ID = "America/Los_Angeles"; // Pacific Time Zone
    private static final String ROMANIA_ZONE_ID = "Europe/Bucharest";
    private static final int MAX_VIDEOS_PER_DAY = 5;
    private static final int TIME_BUFFER_MINUTES = 1; // Buffer time in minutes
    private static final long SLEEP_TIME_BETWEEN_UPLOADS_SECONDS = 1000; // Time between uploads in seconds
    private static final long TEST_SLEEP_TIME_BETWEEN_UPLOADS_SECONDS = 10; // Time between uploads in seconds

    private static final long MIN_DELAY = 3 * 60 * 60 + 30 * 60; // 3 hours and 30 minutes

    private final Iterator<VideoDetails> iterator_;
    private final ScheduledExecutorService scheduler;

    public YouTubeUploadService()
    {
        List<VideoDetails> videoDetailsList = getVideoDetailsList();
        iterator_ = videoDetailsList.iterator();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    private static List<VideoDetails> getVideoDetailsList()
    {
        List<VideoDetails> videoDetailsList = new ArrayList<>();
        videoDetailsList.add(new VideoDetails("Title", "description", "publishDate")
        {
            @Override
            public String getPreviewFileName()
            {
                return null;
            }

            @Override
            public void merge(String ffmpegPath, String outputFilePath, String outputPreviewPath, String sourcePath)
            {

            }

            @Override
            protected String prepareOutputFileName()
            {
                return null;
            }

            @Override
            protected String prepareTitle()
            {
                return null;
            }

            @Override
            protected String prepareDescription()
            {
                return null;
            }
        });
        return videoDetailsList;
    }

    public void startService()
    {
        if (!Main.isTest)
        {
            YouTubeUploader.getInstance();
        }
        uploadVideos();
    }

    private static long calculateInitialDelay()
    {
        ZoneId zoneId = ZoneId.of(AMERICA_ZONE_ID);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = now.withHour(0).withMinute(5).withSecond(0).withNano(0);

        if (now.compareTo(nextRun) > 0)
        {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        return duration.getSeconds();
    }

    private void uploadVideos()
    {
        if (Main.loadManually)
        {
            OneDayVideoCreator videoCreator = new OneDayVideoCreator();
            int videoCounter = 0;
            while(iterator_.hasNext())
            {
                videoCreator.createVideo(iterator_.next(), videoCounter++);
            }
        }
        else
        {
            try
            {
                OneDayVideoCreator videoCreator = new OneDayVideoCreator();
                int videosUploadedCounter = 0;
                while (videosUploadedCounter >= 0 && videosUploadedCounter < MAX_VIDEOS_PER_DAY && iterator_.hasNext())
                {
                    videosUploadedCounter = videoCreator.createVideo(iterator_.next(), videosUploadedCounter);
                }
                System.out.println("Total videos uploaded today: " + videosUploadedCounter);


                // Calculate the delay until the next run at 00:01 Pacific Time
                if (!iterator_.hasNext())
                {
                    System.out.println("All videos uploaded. Shutting down scheduler.");
                    scheduler.shutdown();
                    return;
                }

                // Calculate the delay until the next run at 00:01 Pacific Time
                long delayUntilNextRun = Main.isTest ? 0 : calculateInitialDelay();
                if (!iterator_.hasNext())
                {
                    return;
                }
                if (delayUntilNextRun > 0)
                {
                    System.out.printf("Sleeping for %s seconds for delay\n", delayUntilNextRun);
                    scheduler.schedule(this::uploadVideos, delayUntilNextRun, TimeUnit.SECONDS);
                }
                System.out.println("--------------------------------------------------------------------------------------------------------------------------");
                System.out.println("--------------------------------------------------------------------------------------------------------------------------");
                System.out.println("--------------------------------------------------------------------------------------------------------------------------");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                e.printStackTrace();
                scheduler.shutdown();
            }
        }
    }

    private static class OneDayVideoCreator
    {
        private ZoneId americaZoneId_;
        private ZoneId currentZoneId_;
        private ZonedDateTime americaNow_;
        private ZonedDateTime currentNow_;
        private  long minutesLeft_;
        private long maxUploadTimeMinutes_;

        OneDayVideoCreator()
        {
            americaZoneId_ = ZoneId.of(AMERICA_ZONE_ID);
            currentZoneId_ = ZoneId.of(ROMANIA_ZONE_ID);
            americaNow_ = ZonedDateTime.now(americaZoneId_);
            currentNow_ = ZonedDateTime.now(currentZoneId_);
            System.out.printf("Current time by %s: %s\n", AMERICA_ZONE_ID, americaNow_.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            System.out.printf("Current time by %s: %s\n", ROMANIA_ZONE_ID, currentNow_.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            ZonedDateTime endOfDay = americaNow_.withHour(23).withMinute(59).withSecond(0).withNano(0);
            Duration duration = Duration.between(americaNow_, endOfDay);
            minutesLeft_ = duration.toMinutes();
            maxUploadTimeMinutes_ = 5; // Initial assumption about upload time
        }

        private int createVideo(VideoDetails videoDetails, int videosUploadedCounter)
        {
            ZonedDateTime startUpload = ZonedDateTime.now(americaZoneId_);
            System.out.printf("Started uploading video %s. Current time by %s: %s\n", videosUploadedCounter, AMERICA_ZONE_ID, startUpload.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            // Load and upload each video
            Uploader.getInstance(). upload(videoDetails, videosUploadedCounter);

            ZonedDateTime endUpload = ZonedDateTime.now(americaZoneId_);
            System.out.printf("Finished uploading video %s. Current time by %s: %s\n", videosUploadedCounter, AMERICA_ZONE_ID, endUpload.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            System.out.printf("Uploading video %s by %s was from %s -> %s\n", videosUploadedCounter, AMERICA_ZONE_ID, startUpload, endUpload);
            Duration uploadTime = Duration.between(startUpload, endUpload);
            long uploadTimeHours = uploadTime.toHours();
            long uploadTimeMinutes = uploadTime.toMinutes() % 60;
            long uploadTimeSeconds = uploadTime.getSeconds() % 60;

            // Log the time taken for each upload
            System.out.printf("Video %s uploaded in %s hours %s minutes and %s seconds\n", videosUploadedCounter + 1, uploadTimeHours, uploadTimeMinutes, uploadTimeSeconds);

            return videosUploadedCounter + 1;
        }
    }
}
