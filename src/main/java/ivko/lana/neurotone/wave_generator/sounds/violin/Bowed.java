package ivko.lana.neurotone.wave_generator.sounds.violin;

import ivko.lana.neurotone.processing.Constants;
import ivko.lana.neurotone.util.Util;

public class Bowed
{
    private DelayL delayLine;
    private OnePole lowPassFilter;
    private OnePole highPassFilter;
    private BowTable bowTable;
    private double amplitude;
    private boolean bowDown;
    private double bowPressure = 0.5;  // Давление смычка
    private double bowVelocity = 0.1;  // Скорость смычка
    private double vibratoGain = 0.0003;  // Более мягкое вибрато
    private double vibratoFrequency = 5.0;  // Частота вибрато (в герцах)

    // Улучшенные гармоники для мягкого звука
    // Оставляем только низкочастотный фильтр для смягчения звука

    // Перенастраиваем гармоники для более мягкого звука
    private double[] harmonicAmplitudes = {0.5, 0.1, 0.1, 0.0000001, 0.05};  // Уменьшаем вклад высоких гармоник
    private double[] additionalHarmonicAmplitudes = {0.008, 0.0090, 0.01, 0.002, 0.003, 0.0004, 0.0005, 0.0006};

    public Bowed()
    {
        delayLine = new DelayL(100, 0.03);  // Примерные значения
        lowPassFilter = new OnePole(0.9);   // Низкочастотный фильтр для смягчения звука
        highPassFilter = new OnePole(-0.9); // Высокочастотный фильтр для удаления резкости
        bowTable = new BowTable();
    }

    // Установить давление смычка
    public void setBowPressure(double pressure)
    {
        bowPressure = pressure;
    }

    // Установить скорость смычка
    public void setBowVelocity(double velocity)
    {
        bowVelocity = velocity;
    }

    public void startBowing(double amplitude)
    {
        this.amplitude = amplitude * 10;  // Увеличиваем амплитуду
        bowDown = true;
    }

    public void stopBowing()
    {
        bowDown = false;
    }

    // Метод для генерации звука с более мягкими гармониками и фильтрами
    public short[] generateSamples(double baseFrequency, double duration)
    {
        int numSamples = (int) (Constants.SAMPLE_RATE * duration);
        short[] samples = new short[numSamples];

        for (int i = 0; i < numSamples; i++)
        {
            if (!bowDown)
            {
                amplitude -= 0.005;
                if (amplitude < 0) amplitude = 0;
            }

            // Влияние смычка (давление и скорость)
            double bowEffect = bowTable.tick(amplitude * bowPressure * bowVelocity);

            // Постоянное вибрато
            double vibrato = Math.sin(2.0 * Math.PI * vibratoFrequency * i / Constants.SAMPLE_RATE) * vibratoGain;
            double frequencyWithVibrato = baseFrequency + vibrato;

            // Моделируем улучшенные гармоники
            double sampleValue = 0;
            for (int j = 0; j < harmonicAmplitudes.length; j++)
            {
                double harmonicFreq = frequencyWithVibrato * (j + 1);  // Гармоники - кратные основной частоте
                sampleValue += harmonicAmplitudes[j] * Math.sin(2.0 * Math.PI * harmonicFreq * i / Constants.SAMPLE_RATE);
            }

            double harmonicFrequencyWithVibrato = baseFrequency / 2 + vibrato;
            // Моделируем улучшенные гармоники
            for (int j = 0; j < additionalHarmonicAmplitudes.length; j++)
            {
                double harmonicFreq = harmonicFrequencyWithVibrato * ((j + 8) * 2 +1);  // Гармоники - кратные основной частоте
                sampleValue += additionalHarmonicAmplitudes[j] * Math.sin(2.0 * Math.PI * harmonicFreq * i / Constants.SAMPLE_RATE);
            }

            double delayedSample = delayLine.tick(bowEffect * sampleValue);  // Применяем задержку к гармоническому сигналу
            double filteredSample = lowPassFilter.tick(delayedSample);  // Применяем низкочастотный фильтр
            filteredSample = highPassFilter.tick(filteredSample);       // Применяем высокочастотный фильтр

            // Увеличиваем громкость, умножая значение перед преобразованием в short
            samples[i] = Util.getLimitedValue((int) (filteredSample * 32767));
        }

        return samples;
    }

    // Внутренний класс для таблицы смычка
    public static class BowTable
    {
        private double offset_ = 0.0;
        private double slope_ = 0.1;
        private double minOutput_ = 0.01;
        private double maxOutput_ = 0.98;

        public BowTable()
        {
            // Конструктор по умолчанию
        }

        // Установить значение смещения таблицы
        public void setOffset(double offset)
        {
            offset_ = offset;
        }

        // Установить значение наклона таблицы
        public void setSlope(double slope)
        {
            slope_ = slope;
        }

        // Вычислить выходное значение на основе входного сигнала
        public double tick(double input)
        {
            double output = slope_ * input + offset_;
            if (output < minOutput_) output = minOutput_;
            if (output > maxOutput_) output = maxOutput_;
            return output;
        }
    }

    public static class DelayL
    {

        private double[] buffer;
        private int bufferSize;
        private double delay;
        private int inPoint;
        private int outPoint;
        private double alpha;
        private double omAlpha;
        private double lastOutput;

        public DelayL(int delayLength, double maxDelay)
        {
            bufferSize = (int) Math.ceil(maxDelay + 1);
            buffer = new double[bufferSize];
            setDelay(delayLength);
            clear();
        }

        public void clear()
        {
            for (int i = 0; i < bufferSize; i++)
            {
                buffer[i] = 0.0;
            }
            lastOutput = 0.0;
        }

        public void setDelay(double delayTime)
        {
            delay = delayTime;
            inPoint = 0;
            outPoint = (int) ((inPoint - delay + bufferSize) % bufferSize);
            alpha = delay - (int) delay;
            omAlpha = 1.0 - alpha;
        }

        public double tick(double input)
        {
            buffer[inPoint] = input;
            inPoint = (inPoint + 1) % bufferSize;

            // Linear interpolation
            lastOutput = alpha * buffer[outPoint] + omAlpha * buffer[(outPoint + 1) % bufferSize];
            outPoint = (outPoint + 1) % bufferSize;
            return lastOutput;
        }

        public double lastOut()
        {
            return lastOutput;
        }
    }

    public static class OnePole
    {

        private double a0, b1;
        private double lastOutput;

        public OnePole(double pole)
        {
            setPole(pole);
        }

        public void setPole(double pole)
        {
            b1 = pole;
            a0 = 1.0 - pole;
            lastOutput = 0.0;
        }

        public double tick(double input)
        {
            lastOutput = a0 * input + b1 * lastOutput;
            return lastOutput;
        }

        public double lastOut()
        {
            return lastOutput;
        }
    }
}
