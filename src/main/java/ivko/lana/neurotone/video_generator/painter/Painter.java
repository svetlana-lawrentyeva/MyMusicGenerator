package ivko.lana.neurotone.video_generator.painter;

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.video_generator.VideoConstants;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import java.util.logging.Logger;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.circle;
import static org.opencv.imgproc.Imgproc.LINE_AA;


/**
 * @author Lana Ivko
 */
public abstract class Painter
{
    private static final Logger logger = CustomLogger.getLogger(Painter.class.getName());

    public static final int AVAILABLE_SPACE = Math.min(VideoConstants.WIDTH / 2, VideoConstants.HEIGHT);
    protected Point center_;
    protected Mat image_;
    protected int width_;
    protected int height_;
    protected Scalar color_;

    public Painter(int width, int height, DataType dataType, Scalar color)
    {
        width_ = width;
        height_ = height;
        center_ = new Point(width_ / 2, height_ / 2);
        image_ = new Mat(height_, width_, dataType.getOriginType(), getBackground());

        color_ = color;
    }

    private Mat getBgrImage()
    {
        Mat matRGB = new Mat();
        opencv_imgproc.cvtColor(image_, matRGB, COLOR_BGR2RGB);
        return matRGB;
    }

    protected Mat getRgbImage()
    {
        Mat matRGB = new Mat();
        opencv_imgproc.cvtColor(image_, matRGB, COLOR_BGR2RGB);
        return matRGB;
    }

    public static void main(String[] args)
    {
        try {
            System.load("D:\\install\\OpenCV\\4.9.0\\opencv\\build\\java\\x64\\opencv_java490.dll");
            System.out.println("OpenCV library loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV library.");
            e.printStackTrace();
        }
    }


    protected void clearCircle(Point center, int radius)
    {
        // Определение параметров круга
        int thickness = -1; // Толщина линии круга

        // Рисование круга на изображении
        circle(image_, center, radius, getBackground(), thickness, LINE_AA, 0);
    }

    protected Scalar getBackground()
    {
        return VideoConstants.BACKGROUND;
    }
}
