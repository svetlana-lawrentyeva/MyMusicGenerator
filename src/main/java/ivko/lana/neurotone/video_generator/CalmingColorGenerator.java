package ivko.lana.neurotone.video_generator;

import ivko.lana.neurotone.util.CustomLogger;
import org.bytedeco.opencv.opencv_core.Scalar;
import java.awt.Color;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Lana Ivko
 */
public class CalmingColorGenerator
{
    private static final Logger logger = CustomLogger.getLogger(CalmingColorGenerator.class.getName());
    public static void main(String[] args) {
        // Генерация случайного успокаивающего цвета для шара
        // Генерация случайного успокаивающего цвета для шара
        Scalar calmingColorScalar = generateCalmingColorScalar();

        // Генерация подходящего цвета чисел для шара и черного фона
        Scalar numberColorScalar = generateNumberColorScalar(calmingColorScalar);

        // Печатаем сгенерированные цвета
        System.out.println("Цвет шара (BGR): " + calmingColorScalar);
        System.out.println("Цвет чисел (BGR): " + numberColorScalar);
    }

    public static Scalar generateCalmingColorScalar() {
        Random random = new Random();

        // Диапазоны для успокаивающих цветов
        float minHue = 0.5f; // Зелёный, синий, пурпурный диапазон
        float maxHue = 0.8f;

        // Выбираем случайный оттенок в пределах диапазона
        float hue = minHue + random.nextFloat() * (maxHue - minHue);

        // Высокая насыщенность для яркости на черном фоне
        float saturation = 0.6f + random.nextFloat() * 0.4f; // От 0.6 до 1.0

        // Средняя яркость для успокаивающего эффекта
        float brightness = 0.5f + random.nextFloat() * 0.3f; // От 0.5 до 0.8

        // Создаем цвет на основе HSV (оттенок, насыщенность, яркость)
        Color color = Color.getHSBColor(hue, saturation, brightness);

        // Преобразуем цвет в формат BGR для OpenCV
        int blue = color.getBlue();
        int green = color.getGreen();
        int red = color.getRed();

        // Создаем Scalar для OpenCV
        return new Scalar(blue, green, red, 0); // Четвертый параметр - альфа-канал, можно оставить 0
    }

    public static Scalar generateNumberColorScalar(Scalar calmingColorScalar) {
        // Извлекаем компоненты BGR из Scalar
        int blue = (int) calmingColorScalar.get(0);
        int green = (int) calmingColorScalar.get(1);
        int red = (int) calmingColorScalar.get(2);

        // Конвертируем BGR в RGB для работы с Color
        Color shadeColor = new Color(red, green, blue);

        // Выбираем светлый цвет для чисел, чтобы он был виден на черном фоне
        // Создаем светлый цвет (приближенный к белому) для хорошего контраста с черным
        float[] hsbValues = new float[3];
        Color.RGBtoHSB(shadeColor.getRed(), shadeColor.getGreen(), shadeColor.getBlue(), hsbValues);

        // Устанавливаем насыщенность и яркость для белого цвета
        float hue = hsbValues[0]; // Используем тот же оттенок для гармонии
        float saturation = 0.1f;  // Низкая насыщенность для светлого цвета
        float brightness = 0.9f;  // Высокая яркость, чтобы быть видимым на черном фоне

        // Конвертируем обратно в RGB и создаем Scalar для OpenCV
        Color numberColor = Color.getHSBColor(hue, saturation, brightness);
        int numberBlue = numberColor.getBlue();
        int numberGreen = numberColor.getGreen();
        int numberRed = numberColor.getRed();

        return new Scalar(numberBlue, numberGreen, numberRed, 0);
    }
}
