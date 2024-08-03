package ivko.lana.neurotone.video_generator;

import ivko.lana.neurotone.StereoSaver;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.wave_generator.WaveDetail;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.logging.Logger;


/**
 * @author Lana Ivko
 */
public class VideoSaver extends StereoSaver
{
    private static final Logger logger = CustomLogger.getLogger(VideoSaver.class.getName());

    public static final String FILE_NAME = "output_video.mp4";
    public static final long FULL_MEMORY_LIMIT = 57418240l;

    private VideoPlayer videoPlayer_;

    public VideoSaver()
    {
        super();
        videoPlayer_ = new VideoPlayer();
        videoPlayer_.setMatConsumer(this::postMat);
    }

    @Override
    protected String getFileName()
    {
        logger.info("Asking for the video file name");
        return FILE_NAME;
    }

    private void postMat(Mat mat)
    {
        int size = (int) (mat.total() * mat.channels());

        byte[] byteArray = new byte[size];
        mat.data().get(byteArray);

        postToQueue(byteArray);
    }

    @Override
    public void post(WaveDetail leftChannel, WaveDetail rightChannel)
    {
        videoPlayer_.post(leftChannel, rightChannel);
    }

    @Override
    public boolean isFull()
    {
        boolean isFull = postedToQueueSize_.get() > FULL_MEMORY_LIMIT;
        if (isFull)
        {
            logger.info(String.format("%s is full: %s bytes", getClass().getSimpleName(), postedToQueueSize_.get()));
        }
        return isFull;
    }

    @Override
    protected void stopProcesses()
    {
        videoPlayer_.close();
    }
}
