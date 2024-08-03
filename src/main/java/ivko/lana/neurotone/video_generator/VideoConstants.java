package ivko.lana.neurotone.video_generator;

import org.bytedeco.opencv.opencv_core.Scalar;

/**
 * @author Lana Ivko
 */
public class VideoConstants
{
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static final int FPS = 50; // Частота дискретизации

    public static final Scalar BACKGROUND = new Scalar(0, 0, 0, 0);


    // COLORS
    public static final Scalar RED = new Scalar(0, 0, 50, 255);
    public static final Scalar GREEN = new Scalar(0, 50, 0, 255);
    public static final Scalar BLUE = new Scalar(50, 0, 0, 255);
    public static final Scalar GOLD = new Scalar(0, 125, 175, 255);
    public static final Scalar LIGHT_SKIN = new Scalar(189, 224, 255, 255);
    public static final Scalar MEDIUM_SKIN = new Scalar(152, 194, 229, 255);
    public static final Scalar OLIVE_SKIN = new Scalar(114, 164, 202, 255);
    public static final Scalar DARK_SKIN = new Scalar(66, 87, 139, 255);
    public static final Scalar SKIN = new Scalar(68, 95, 150, 255);
    public static final Scalar YELLOW = new Scalar(68, 255, 255, 255);

//    Sphere color is: (132.0, 88.0, 35.0, 0.0)
}
