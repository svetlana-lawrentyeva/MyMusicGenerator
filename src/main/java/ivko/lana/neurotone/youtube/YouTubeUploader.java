package ivko.lana.neurotone.youtube;

/**
 * @author Lana Ivko
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailSetResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import ivko.lana.neurotone.old_generation.Main;
import ivko.lana.neurotone.old_generation.VideoDetails;
import ivko.lana.neurotone.old_generation.VideoGeneratorConstants;
import ivko.lana.neurotone.old_generation.VideoUtils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class YouTubeUploader
{
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Bucharest");
    private static final String CLIENT_SECRETS = "C:/keys/client_secret.json";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/youtube.upload");
    private static final String APPLICATION_NAME = "YouTube Loader";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static HttpTransport HTTP_TRANSPORT;

    private YouTube youtube_;
    private Credential credential_;
    private static Calendar Calendar_;
    private SimpleDateFormat dateFormat_ = new SimpleDateFormat("dd-MM-yyyy");

    private YouTubeUploader()
    {
        Date currentDate = new Date();
        Calendar_ = Calendar.getInstance();
        Calendar_.setTime(currentDate);
        if (!VideoGeneratorConstants.LOAD_MANUALLY && !VideoGeneratorConstants.IS_TEST)
        {
            try
            {
                deleteOldTokens();
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                DATA_STORE_FACTORY = new FileDataStoreFactory(new File("tokens"));
                credential_ = authorize();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static YouTubeUploader getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    public void loadVideo(String outputDirectory, String videoFileName, String previewFileName, VideoDetails videoDetails)
    {
        List<String> tags = videoDetails.getPreparedTags();
        String title = videoDetails.getPreparedTitle();
        String description = videoDetails.getPreparedDescription();

        System.out.printf("Preparing loading:\nTitle:\n\n%s\n\nDescription:\n\n%s\n\nTags:\n\n%s\n", title, description, String.join(",", tags));

        String publishDate = dateFormat_.format(Calendar_.getTime());
        Calendar_.add(Calendar.DAY_OF_MONTH, 1);
        if (!VideoGeneratorConstants.IS_TEST)
        {
            try
            {
                // Upload video
                File file = new File(outputDirectory + videoFileName);
                String publishDateString = publishDate;
                ZonedDateTime publishDateTime = ZonedDateTime.parse(publishDateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME); // Получаем текущее время
                ZonedDateTime now = ZonedDateTime.now();

                // Рассчитываем время послезавтра
                ZonedDateTime dayAfterTomorrow = now.plus(2, ChronoUnit.DAYS);

                // Проверяем, если время публикации раньше времени послезавтра
                if (publishDateTime.isBefore(dayAfterTomorrow)) {
                    // Устанавливаем время публикации на сегодня
                    publishDateTime = now;
                }
                if (file.isFile())
                {
                    uploadVideo(file.getAbsolutePath(), title, description, tags, "10", outputDirectory + previewFileName, publishDateTime);
                    System.out.println("File: " + file.getAbsolutePath());
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else
        {
            String metaDataFileName = VideoUtils.formatFrequency(String.valueOf(videoDetails.getOutputFileName())) + "_metadata.txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputDirectory + metaDataFileName, true)))
            {
                writer.println("TITLE\n\n" + title);
                writer.println();
                writer.println("-------------------------");
                writer.println();
                writer.println("DESCRIPTION\n\n" + description);
                writer.println();
                writer.println("-------------------------");
                writer.println();
                writer.println("TAGS\n\n" + String.join(",", tags));
                writer.println();
                writer.println("-------------------------");
                writer.println();
                writer.println("PUBLISH DATE\n\n" + publishDate);
                writer.println();
                writer.println("-------------------------");
                writer.println();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private Credential authorize() throws Exception
    {
        System.out.println("Starting authorization process...");
        InputStream in = YouTubeUploader.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        System.out.println("Authorization process completed.");
        return credential;
    }

    public void deleteOldTokens()
    {
        File tokenFolder = new File("tokens");
        if (tokenFolder.exists() && tokenFolder.isDirectory())
        {
            for (File file : tokenFolder.listFiles())
            {
                file.delete();
            }
        }
    }

    private Credential getValidCredential() throws IOException
    {
        if (credential_.getExpiresInSeconds() != null && credential_.getExpiresInSeconds() <= 60)
        {
            credential_.refreshToken();
        }
        return credential_;
    }

    private YouTube getYouTubeService() throws IOException, GeneralSecurityException
    {
        HttpRequestInitializer requestInitializer = request ->
        {
            getValidCredential().initialize(request);
            request.setConnectTimeout(300000); // 5 minutes connect timeout
            request.setReadTimeout(300000); // 5 minutes read timeout
        };

        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void uploadVideo(String filePath, String title, String description, List<String> tags, String categoryId, String outputPreviewPath, ZonedDateTime publishDateTime) throws Exception
    {
        YouTube youtube = getYouTubeService();
        try
        {
            Video videoObjectDefiningMetadata = new Video();

            // Установка статуса видео
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("private");
            status.setPublishAt(new com.google.api.client.util.DateTime(publishDateTime.toInstant().toEpochMilli()));
            videoObjectDefiningMetadata.setStatus(status);

            // Установка метаданных видео
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(title);
            snippet.setDescription(description);
            snippet.setCategoryId(categoryId);
            snippet.setTags(tags);
            videoObjectDefiningMetadata.setSnippet(snippet);

            Video returnedVideo;
            if (!Main.loadManually)
            {
                InputStreamContent mediaContent = new InputStreamContent(
                        "video/*", new BufferedInputStream(new FileInputStream(filePath)));
                mediaContent.setLength(new File(filePath).length());

                // Создание и выполнение запроса на загрузку видео
                YouTube.Videos.Insert videoInsert = youtube.videos()
                        .insert("snippet,status", videoObjectDefiningMetadata, mediaContent);
                returnedVideo = videoInsert.execute();
                System.out.println("Video has been downloaded: " + returnedVideo.getSnippet().getTitle());
            }
            else
            {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Input videoId: ");
                String videoId = scanner.nextLine();

                videoObjectDefiningMetadata.setId(videoId);

                YouTube.Videos.Update videoUpdate = youtube.videos().update("snippet,status", videoObjectDefiningMetadata);
                returnedVideo = videoUpdate.execute();
                System.out.println("Video has been updated: " + returnedVideo.getSnippet().getTitle());
            }


            System.out.println("Video ID: " + returnedVideo.getId());
            System.out.println("Title: " + returnedVideo.getSnippet().getTitle());
            System.out.println("Description: " + returnedVideo.getSnippet().getDescription());
            System.out.println("Tags: " + returnedVideo.getSnippet().getTags());
            System.out.println("Category: " + returnedVideo.getSnippet().getCategoryId());
            System.out.println("Publish Time: " + returnedVideo.getStatus().getPublishAt());

            if (outputPreviewPath != null)
            {
                String videoId = returnedVideo.getId();
                File thumbnailFile = new File(outputPreviewPath);
                FileInputStream thumbnailStream = new FileInputStream(thumbnailFile);

                YouTube.Thumbnails.Set thumbnailSet = youtube.thumbnails()
                        .set(videoId, new InputStreamContent("image/jpeg", thumbnailStream));

                ThumbnailSetResponse response = thumbnailSet.execute();
                System.out.println("Thumbnail URL: " + response.getItems().get(0).getDefault().getUrl());
            }
        }
        catch (Throwable e)
        {
            System.out.println("Error uploading video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class InstanceHolder
    {
        private static final YouTubeUploader INSTANCE = new YouTubeUploader();
    }
}
