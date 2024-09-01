    package ivko.lana.neurotone.video_generator.painter;

import ivko.lana.neurotone.util.CustomLogger;
import ivko.lana.neurotone.video_generator.VideoConstants;
import ivko.lana.util.Pair;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static org.bytedeco.opencv.global.opencv_imgproc.circle;

;
;

/**
 * @author Lana Ivko
 */
public class CirclePainter extends Painter
{
    private static final Logger logger = CustomLogger.getLogger(CirclePainter.class.getName());

    private static final int RADIIUS = (int) (AVAILABLE_SPACE / 2.5);
    private double BASE_SMOOTHING_FACTOR = 0.05;
    private Map<Pair, Integer> radiusCache_ = new HashMap<>();
    private int previousRadius_ = RADIIUS;

    public CirclePainter(int width, int height, Scalar color)
    {
        super(width, height, DataType.TRANSPARENT, color);
    }

    public Mat getImage(short value)
    {
        if (previousRadius_ > 0)
        {
            // Стираем предыдущий круг
            clearCircle(center_, previousRadius_ + 1);
        }
        int radius = getRadius(previousRadius_, value);

        // Рисуем новый круг с интерполированным радиусом
        drawGradientCircle(center_, radius, color_);
        previousRadius_ = radius;
        return getRgbImage();
    }

    private int getRadius(int previousRadius_, short value)
    {
        Pair currentKey = null;
        int radius;
        if (previousRadius_ > Integer.MIN_VALUE)
        {
            currentKey = new Pair(previousRadius_, value);
            if (radiusCache_.containsKey(currentKey))
            {
                return radiusCache_.get(currentKey);
            }
        }
        int radiusShift = (value * (AVAILABLE_SPACE / 4)) / 10000;

        radius = RADIIUS + radiusShift;

        // Логарифмическое сглаживание для уменьшения эффекта больших изменений
        double diff = Math.abs(radius - previousRadius_);

        // Вычисляем коэффициент сглаживания на основе логарифма разницы
        double adjustedSmoothingFactorLeft = BASE_SMOOTHING_FACTOR / Math.log10(diff + 1.1);

        // Применяем нелинейное сглаживание
        radius = (int) (previousRadius_ * (1 - adjustedSmoothingFactorLeft) + radius * adjustedSmoothingFactorLeft);
        radiusCache_.put(currentKey, radius);
        return radius;
    }

    private void drawGradientCircle(Point center, int radius, Scalar startColor)
    {
        Scalar endColor = VideoConstants.BACKGROUND;
        int cx = center.x();
        int cy = center.y();
        int radiusSquared = radius * radius; // Используем квадрат радиуса

        // Предварительное вычисление цвета
        double startB = startColor.get(0);
        double startG = startColor.get(1);
        double startR = startColor.get(2);
        double startA = startColor.get(3);

        double endB = endColor.get(0);
        double endG = endColor.get(1);
        double endR = endColor.get(2);
        double endA = endColor.get(3);

        // Получаем прямой доступ к данным изображения
        byte[] data = new byte[(int) (image_.total() * image_.channels())];
        image_.data().get(data);

        // Параллельный поток для улучшения производительности
        IntStream.range(cy - radius, cy + radius + 1).parallel().forEach(y ->
        {
            int dy = y - cy;
            int dySquared = dy * dy;
            for (int x = cx - radius; x <= cx + radius; x++)
            {
                int dx = x - cx;
                int dxSquared = dx * dx;

                if (dxSquared + dySquared <= radiusSquared)
                {
                    double distanceRatio = Math.sqrt(dxSquared + dySquared) / radius;
                    double inverseRatio = 1.0 - distanceRatio;

                    // Вычисление градиентного цвета
                    double b = startB * inverseRatio + endB * distanceRatio;
                    double g = startG * inverseRatio + endG * distanceRatio;
                    double r = startR * inverseRatio + endR * distanceRatio;
                    double a = startA * inverseRatio + endA * distanceRatio;

                    // Определение позиции в буфере
                    int index = (y * image_.cols() + x) * image_.channels();
                    data[index] = (byte) b;
                    data[index + 1] = (byte) g;
                    data[index + 2] = (byte) r;
                    data[index + 3] = (byte) a;
                }
            }
        });

        // Устанавливаем данные обратно в изображение
        image_.data().put(data);
    }

}