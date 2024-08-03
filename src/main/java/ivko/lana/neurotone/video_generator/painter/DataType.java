package ivko.lana.neurotone.video_generator.painter;

/**
 * @author Lana Ivko
 */
public enum DataType {
    NON_TRANSPARENT {@Override public int getOriginType() {return org.opencv.core.CvType.CV_8UC3;} @Override public int getChannelCount() {return 3;} @Override public int getChannelSize() {return 1;}},
    TRANSPARENT {@Override public int getOriginType() {return org.opencv.core.CvType.CV_8UC4;} @Override public int getChannelCount() {return 4;} @Override public int getChannelSize() {return 1;}};

    public abstract int getOriginType();
    public abstract int getChannelCount();
    public abstract int getChannelSize();
}
