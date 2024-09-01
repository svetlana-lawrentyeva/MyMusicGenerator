package ivko.lana.instruments_for_test.tibetan_sound_analyzer;

import java.util.Arrays;

/**
 * @author Lana Ivko
 */
public class TibetanBowlSimulation {

    // Параметры системы
    private double density; // плотность материала чаши
    private double damping; // коэффициент демпфирования
    private double youngModulus; // модуль Юнга материала
    private double speedOfSound; // скорость звука в воздухе
    private double diameter; // диаметр чаши
    private double thickness; // толщина стенок чаши
    private double poissonRatio; // коэффициент Пуассона

    // Конструктор
    public TibetanBowlSimulation(double density, double damping, double youngModulus, double speedOfSound, double diameter, double thickness, double poissonRatio) {
        this.density = density;
        this.damping = damping;
        this.youngModulus = youngModulus;
        this.speedOfSound = speedOfSound;
        this.diameter = diameter;
        this.thickness = thickness;
        this.poissonRatio = poissonRatio;
    }

    // Метод для расчета частотных характеристик системы с учетом диаметра и толщины (упрощенный)
    public double[] calculateNaturalFrequencies(int numModes) {
        double[] frequencies = new double[numModes];
        double factor = (1 / (2 * Math.PI)) * Math.sqrt((youngModulus * Math.pow(thickness, 2)) / (density * Math.pow(diameter, 4))); // Учитываем диаметр и добавляем поправку
        for (int i = 0; i < numModes; i++) {
            frequencies[i] = factor * Math.pow((i + 1), 2); // Квадратичная зависимость частот
        }
        return frequencies;
    }

    // Метод для расчета амплитуд каждой частоты
    public double[] calculateAmplitudes(double[] frequencies) {
        double[] amplitudes = new double[frequencies.length];
        double baseAmplitude = 1.0; // базовая амплитуда для первой частоты
        for (int i = 0; i < frequencies.length; i++) {
            amplitudes[i] = baseAmplitude * Math.exp(-damping * frequencies[i]); // экспоненциальное уменьшение амплитуды
        }
        return amplitudes;
    }

    // Метод для расчета акустического давления в заданной точке (упрощенный)
    public double calculateAcousticPressure(double frequency, double distance) {
        double wavelength = speedOfSound / frequency;
        return Math.sin(2 * Math.PI * frequency * distance / wavelength) * Math.exp(-damping * distance);
    }

    // Метод для синтеза импульсной характеристики системы (упрощенный)
    public double[] synthesizeImpulseResponse(double[] frequencies, int sampleRate, double duration) {
        int numSamples = (int) (duration * sampleRate);
        double[] impulseResponse = new double[numSamples];

        for (int n = 0; n < numSamples; n++) {
            double time = n / (double) sampleRate;
            for (double frequency : frequencies) {
                impulseResponse[n] += Math.sin(2 * Math.PI * frequency * time) * Math.exp(-damping * time);
            }
        }

        return impulseResponse;
    }

    // Пример использования класса
    public static void main(String[] args) {
        double density = 8600; // плотность материала (кг/м^3)
        double damping = 0.05; // коэффициент демпфирования
        double youngModulus = 105e9; // модуль Юнга материала (Па)
        double speedOfSound = 343; // скорость звука в воздухе (м/с)
        double diameter = 0.3; // диаметр чаши (м)
        double thickness = 0.01; // толщина стенок чаши (м)
        double poissonRatio = 0.34; // коэффициент Пуассона

        TibetanBowlSimulation bowlSimulation = new TibetanBowlSimulation(density, damping, youngModulus, speedOfSound, diameter, thickness, poissonRatio);

        // Рассчитываем собственные частоты
        double[] frequencies = bowlSimulation.calculateNaturalFrequencies(5);
        System.out.println("Natural Frequencies: " + Arrays.toString(frequencies));

        // Рассчитываем амплитуды для каждой частоты
        double[] amplitudes = bowlSimulation.calculateAmplitudes(frequencies);
        System.out.println("Amplitudes: " + Arrays.toString(amplitudes));

        // Рассчитываем акустическое давление
        double pressure = bowlSimulation.calculateAcousticPressure(frequencies[0], 0.5);
        System.out.println("Acoustic Pressure at 0.5m: " + pressure);

        // Синтезируем импульсную характеристику
        double[] impulseResponse = bowlSimulation.synthesizeImpulseResponse(frequencies, 44100, 1.0);
        System.out.println("Impulse Response (first 10 samples): " + Arrays.toString(Arrays.copyOfRange(impulseResponse, 0, 10)));
    }
}


