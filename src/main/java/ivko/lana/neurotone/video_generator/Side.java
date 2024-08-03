package ivko.lana.neurotone.video_generator;

    import org.bytedeco.opencv.opencv_core.Scalar;

/**
 * @author Lana Ivko
 */
public enum Side
{
    LEFT {@Override public Scalar getCirgleColor() {return CIRCLE_COLOR;}@Override public Scalar getNumberColor() {return NUMBER_COLOR;}},
    RIGHT {@Override public Scalar getCirgleColor() {return CIRCLE_COLOR;}@Override public Scalar getNumberColor() {return NUMBER_COLOR;}};

    public abstract Scalar getCirgleColor();
    public abstract Scalar getNumberColor();

    private static final Scalar CIRCLE_COLOR = CalmingColorGenerator.generateCalmingColorScalar();
    private static final Scalar NUMBER_COLOR = CalmingColorGenerator.generateNumberColorScalar(CIRCLE_COLOR);
}
