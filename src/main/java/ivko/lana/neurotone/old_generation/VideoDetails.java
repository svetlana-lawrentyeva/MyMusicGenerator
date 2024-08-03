package ivko.lana.neurotone.old_generation;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lana Ivko
 */
public abstract class VideoDetails
{
    protected static final String PREVIEW_FILE_NAME = "preview.JPG";
    public static final String CSV_DELIMETER = ",";

    protected final String title_;
    protected final String description_;
    protected final String publishDate_;

    private String preparedTitle_;
    private String preparedDescription_;
    private List<String> preparedTags_;
    private String outputFileName_;

    public VideoDetails(String title, String description, String publishDate)
    {
        title_ = title;
        description_ = description;
        publishDate_ = publishDate;
    }

    public abstract String getPreviewFileName();

    public String getOutputFileName()
    {
        if (outputFileName_ == null)
        {
            outputFileName_ = prepareOutputFileName();
        }
        return outputFileName_;
    }

    public abstract void merge(String ffmpegPath, String outputFilePath, String outputPreviewPath, String sourcePath);

    protected abstract String prepareOutputFileName();

    public String getPublishDate()
    {
        return publishDate_;
    }

    protected List<String> prepareTags()
    {
        String tagsString = ResourceReader.getInstance().get(ResourceReader.ResourceType.TAGS);
        return Arrays.asList(tagsString.split(CSV_DELIMETER));
    }
    protected abstract String prepareTitle();
    protected abstract String prepareDescription();

    public String getPreparedTitle()
    {
        if (preparedTitle_ == null)
        {
            preparedTitle_ = prepareTitle();
        }
        return  preparedTitle_;
    }

    public String getPreparedDescription()
    {
        if (preparedDescription_ == null)
        {
            preparedDescription_ = prepareDescription();
        }
        return preparedDescription_;
    }

    public List<String> getPreparedTags()
    {
        if (preparedTags_ == null)
        {
            preparedTags_ = prepareTags();
        }
        return preparedTags_;
    }

    @Override
    public String toString()
    {
        return "VideoDetails{" +
                "title_='" + title_ + '\'' +
                ", description_='" + description_ + '\'' +
                ", publishDate_='" + publishDate_ + '\'' +
                '}';
    }

    public String getLogInfo()
    {
        return toString();
    }
}
