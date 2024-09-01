package ivko.lana.neurotone.video_generator;

/**
 * @author Lana Ivko
 */

import ivko.lana.neurotone.StereoPlayer;
import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.video_generator.painter.CirclePainter;
import ivko.lana.neurotone.video_generator.painter.NumberPainter;
import ivko.lana.neurotone.wave_generator.WaveDetail;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class VideoPlayer extends StereoPlayer
{
    private static final Logger logger = CustomLogger.getLogger(VideoPlayer.class.getName());

    private static final int CHANNELS = 3; // Количество каналов (например, RGB)
    private CanvasFrame canvas_;
    private CirclePainter leftCirclePainter_;
    private CirclePainter rightCirclePainter_;
    private NumberPainter leftNumberPainter_;
    private NumberPainter rightNumberPainter_;
    private Consumer<Mat> matConsumer_;

    private OpenCVFrameConverter.ToMat converter_;


    public VideoPlayer()
    {
        super();
        leftCirclePainter_ = new CirclePainter(VideoConstants.WIDTH / 2, VideoConstants.HEIGHT, Side.LEFT.getCirgleColor());
        rightCirclePainter_ = new CirclePainter(VideoConstants.WIDTH / 2, VideoConstants.HEIGHT, Side.RIGHT.getCirgleColor());
        leftNumberPainter_ = new NumberPainter(VideoConstants.WIDTH / 2, VideoConstants.HEIGHT, VideoConstants.OLIVE_SKIN);
        rightNumberPainter_ = new NumberPainter(VideoConstants.WIDTH / 2, VideoConstants.HEIGHT, VideoConstants.OLIVE_SKIN);
        converter_ = new OpenCVFrameConverter.ToMat();
    }

    private void prepareCanvas()
    {
        canvas_ = new CanvasFrame("Video Playback");
        canvas_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas_.setCanvasSize(VideoConstants.WIDTH, VideoConstants.HEIGHT);
        logger.info(String.format("Sphere color is: %s", Side.LEFT.getCirgleColor()));
        logger.info(String.format("Numbers color is: %s", Side.LEFT.getNumberColor()));
    }

    public void setMatConsumer(Consumer<Mat> frameConsumer)
    {
        matConsumer_ = frameConsumer;
    }

    @Override
    public void post(WaveDetail leftChannel, WaveDetail rightChannel)
    {
        if (matConsumer_ == null)
        {
            prepareCanvas();
            matConsumer_ = m ->
            {
                Frame frame = converter_.convert(m);
                canvas_.showImage(frame);
            };
        }
        postVideoData(leftChannel, rightChannel);
    }

    private void consumeMat(Mat mat)
    {
        matConsumer_.accept(mat);
    }

    private void postVideoData(WaveDetail leftChannelWave, WaveDetail rightChannelWave)
    {
        short[] leftChannel = leftChannelWave.getSamples();
        short[] rightChannel = rightChannelWave.getSamples();

        double[] leftFrequencies = leftChannelWave.getFrequencies();
        double[] rightFrequencies = rightChannelWave.getFrequencies();

//        Mat leftFrequency = leftNumberPainter_.getImage(leftFrequencies[0]);
//        Mat rightFrequency = rightNumberPainter_.getImage(rightFrequencies[0]);
//        Mat frequencyMat = concatImages(leftFrequency, rightFrequency);
        if (leftFrequencies != null && rightFrequencies != null && isRunning())
        {
            int samplesPerFrame = (int) (Constants.SAMPLE_RATE / VideoConstants.FPS);
//            double frequencyStep = Constants.BEAT_DURATION_MS * Constants.SAMPLE_RATE/1000;
//            int frequencyCounter = 0;
            for (int i = 0, counter = 0; i < leftChannel.length; i += samplesPerFrame, ++counter)
            {
                Mat leftSphere = leftCirclePainter_.getImage(leftChannel[i]);
                Mat rightSphere = rightCirclePainter_.getImage(rightChannel[i]);

//                int timeShift = (int) (1 / (frequencyStep / (i % frequencyStep)) * 100);
//
                Mat sphereMat = concatImages(leftSphere, rightSphere);
//                if (i % frequencyStep == 0)
//                {
//                    frequencyCounter++;
//                    if (frequencyCounter < leftFrequencies.length)
//                    {
//                        double leftFrequencyValue = leftFrequencies[frequencyCounter];
//                        double rightFrequencyValue = rightFrequencies[frequencyCounter];
//                        if (leftFrequencyValue != 0)
//                        {
//                            leftFrequency = leftNumberPainter_.getImage(leftFrequencyValue);
//                        }
//                        if (rightFrequencyValue != 0)
//                        {
//                            rightFrequency = rightNumberPainter_.getImage(rightFrequencyValue);
//                        }
//                    }
//                    frequencyMat = concatImages(leftFrequency, rightFrequency);
//                }

//                consumeMat(overlayTransparentOnOpaque(sphereMat, frequencyMat));
                consumeMat(sphereMat);
            }
        }
    }

    public static Mat overlayTransparentOnOpaque(Mat base, Mat overlay) {
        // Проверяем, что изображения имеют одинаковый размер
        if (base.size().width() != overlay.size().width() || base.size().height() != overlay.size().height()) {
            throw new IllegalArgumentException("Images must be of the same size.");
        }

        // Убедимся, что тип данных корректен
        if (base.type() != opencv_core.CV_8UC3 || overlay.type() != opencv_core.CV_8UC4) {
            throw new IllegalArgumentException("Base image must be CV_8UC3 and overlay image must be CV_8UC4.");
        }

        // Разделяем наложенное изображение на цветовые каналы и альфа-канал
        MatVector overlayChannels = new MatVector(4);
        opencv_core.split(overlay, overlayChannels);
        Mat overlayBGR = new Mat();
        opencv_core.merge(new MatVector(overlayChannels.get(0), overlayChannels.get(1), overlayChannels.get(2)), overlayBGR);
        Mat alphaChannel = overlayChannels.get(3);

        // Нормализуем альфа-канал в диапазоне [0, 1]
        Mat alpha = new Mat();
        alphaChannel.convertTo(alpha, opencv_core.CV_32F, 1.0 / 255.0, 0);

        // Преобразуем alpha в трехканальную матрицу
        Mat alpha3Channels = new Mat();
        MatVector alphaChannels = new MatVector(alpha, alpha, alpha);
        opencv_core.merge(alphaChannels, alpha3Channels);

        // Инвертируем альфа-канал
        Mat alphaInv = new Mat();
        opencv_core.subtract(Mat.ones(alpha.size(), opencv_core.CV_32F).asMat(), alpha, alphaInv);

        // Преобразуем alphaInv в трехканальную матрицу
        Mat alphaInv3Channels = new Mat();
        MatVector alphaInvChannels = new MatVector(alphaInv, alphaInv, alphaInv);
        opencv_core.merge(alphaInvChannels, alphaInv3Channels);

        // Создаем пустую матрицу для результата
        Mat result = new Mat();
        base.convertTo(result, opencv_core.CV_32FC3);  // Преобразуем в float с 3 каналами

        // Преобразуем overlayBGR в float
        Mat overlayFloat = new Mat();
        overlayBGR.convertTo(overlayFloat, opencv_core.CV_32FC3);

        // Проверяем размеры и количество каналов перед выполнением арифметических операций
        if (result.size().width() != overlayFloat.size().width() || result.size().height() != overlayFloat.size().height()) {
            throw new IllegalArgumentException("Result and overlayFloat matrices must have the same size.");
        }
        if (alpha3Channels.size().width() != alphaInv3Channels.size().width() || alpha3Channels.size().height() != alphaInv3Channels.size().height()) {
            throw new IllegalArgumentException("Alpha and alphaInv matrices must have the same size.");
        }

        // Проверяем количество каналов
        if (result.channels() != overlayFloat.channels()) {
            throw new IllegalArgumentException("Result and overlayFloat matrices must have the same number of channels.");
        }
        if (alpha3Channels.channels() != 3 || alphaInv3Channels.channels() != 3) {
            throw new IllegalArgumentException("Alpha and alphaInv matrices must have three channels.");
        }

        // Перемножаем каждый канал изображения на соответствующий альфа-канал
        opencv_core.multiply(overlayFloat, alpha3Channels, overlayFloat);
        opencv_core.multiply(result, alphaInv3Channels, result);

        // Складываем изображения
        opencv_core.add(result, overlayFloat, result);

        // Конвертируем обратно в 8-битное изображение
        result.convertTo(result, opencv_core.CV_8UC3);

        return result;
    }

    private Mat concatImages(Mat leftImage, Mat rightImage)
    {
        // Создаем новое изображение, объединяя левую и правую половины
        int totalWidth = leftImage.cols() + rightImage.cols();
        int height = leftImage.rows();
        Mat combinedImage = new Mat(new Size(totalWidth, height), leftImage.type());

        // Копируем левую половину в левую часть нового изображения
        leftImage.copyTo(combinedImage.colRange(0, leftImage.cols()));

        // Копируем правую половину в правую часть нового изображения
        rightImage.copyTo(combinedImage.colRange(leftImage.cols(), totalWidth));

        return combinedImage;
    }

    @Override
    public boolean needWait()
    {
        return canvas_ == null ? super.needWait() : canvas_.isVisible();
    }

    @Override
    public boolean isRunning()
    {
        return matConsumer_ != null;
    }

    @Override
    protected void send(byte[] data)
    {
        // Реализация метода отправки
    }

    @Override
    protected void doAfterAll()
    {
        if (canvas_ != null)
        {
            canvas_.dispose();
        }
    }
}
