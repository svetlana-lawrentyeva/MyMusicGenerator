package ivko.lana.neurotone.video_generator.painter;

import ivko.lana.neurotone.util.CustomLogger;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class NumberPainter extends Painter
{
    private static final Logger logger = CustomLogger.getLogger(NumberPainter.class.getName());
    private static final double BASE_FONT_SIZE = 2;
    private int previousRadius_ = (int) (BASE_FONT_SIZE + 5);

    public NumberPainter(int width, int height, Scalar color)
    {
        super(width, height, DataType.TRANSPARENT, color);
    }

    public Mat getImage(double value)
    {
        image_ = new Mat(height_, width_, image_.type(), getBackground());
        // Форматирование числа
        String text;
        if (value == (int) value)
        {
            text = String.format("%d", (int) value);
        }
        else
        {
            text = String.format("%.2f", value).replaceAll("\\.?0*$", "");
        }

        // Вычисление размера шрифта и прозрачности
        double fontSize = BASE_FONT_SIZE;
        int thickness = 3;
        previousRadius_ = thickness;

        // Получаем размер текста
        int[] baseline = new int[1];  // массив для хранения значения базовой линии
        org.bytedeco.opencv.opencv_core.Size textSize = opencv_imgproc.getTextSize(text, opencv_imgproc.FONT_HERSHEY_SIMPLEX, fontSize, thickness, baseline);

        // Корректный расчет центра текста
        Point center = new Point(center_.x() - textSize.width() / 2, center_.y() / 5 + textSize.height() / 2);

        // Рисуем текст на изображении
        opencv_imgproc.putText(
                image_,
                text,
                center,
                opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                fontSize,
                color_,
                thickness,
                opencv_imgproc.LINE_AA,
                false
        );

        return getRgbImage();
    }


    @Override
    protected Mat getRgbImage()
    {
        Mat matRGBA = new Mat();
        opencv_imgproc.cvtColor(image_, matRGBA, opencv_imgproc.COLOR_BGRA2RGBA);
        return matRGBA;
    }
}
