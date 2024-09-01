package ivko.lana.neurotone.old_generation;

import ivko.lana.neurotone.youtube.YouTubeUploadService;

/**
 * @author Lana Ivko
 */
public class Main
{
    public static final boolean isTest = true;
    public static final boolean loadManually = true;

    public static void main(String[] args)
    {
        YouTubeUploadService service = new YouTubeUploadService();
        service.startService();
    }
}