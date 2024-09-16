package ivko.lana.neurotone.interesting.three_body_problem;

import java.awt.*;

public class TriangleThreeBodyProblem extends ThreeBodyProblem
{
    protected static final int LENGTH = 120;
    private double angle = 0; // Угол для расчета движения

    public TriangleThreeBodyProblem()
    {
        super();
    }

    @Override
    protected void init()
    {
        dt = 0.02;

        // Радиус для каждой оси
        double radiusX = 1.5;
        double radiusY = 0.8;

        // Начальные углы для каждой точки
        double initialAngle1 = 0;              // Угол для первой точки
        double initialAngle2 = 2 * Math.PI / 3; // Угол для второй точки (сдвиг 120 градусов)
        double initialAngle3 = 4 * Math.PI / 3; // Угол для третьей точки (сдвиг 240 градусов)

        // Инициализация начальных позиций для каждой точки
        x1 = radiusX * Math.cos(initialAngle1);
        y1 = radiusY * Math.sin(initialAngle1);

        x2 = radiusX * Math.cos(initialAngle2);
        y2 = radiusY * Math.sin(initialAngle2);

        x3 = radiusX * Math.cos(initialAngle3);
        y3 = radiusY * Math.sin(initialAngle3);

        // Скорости можно оставить нулевыми
        vx1 = 0;
        vy1 = 0;
        vx2 = 0;
        vy2 = 0;
        vx3 = 0;
        vy3 = 0;

        G = 1;
        m1 = 1;
        m2 = 1;
        m3 = 1;
    }

    @Override
    protected int getLength()
    {
        return LENGTH;
    }

    @Override
    protected void updatePositions()
    {
        // Обновляем угол с течением времени
        angle += dt;

        // Параметры для эллиптического движения
        double radiusX = 300;  // Радиус по оси X (увеличен для экрана)
        double radiusY = 200;  // Радиус по оси Y (увеличен для экрана)

        // Центр экрана (координаты середины окна)
        double centerX = getWidth() / 2;  // Центр по X (середина окна)
        double centerY = getHeight() / 2; // Центр по Y (середина окна)

        // Фазовые сдвиги для треугольного решения Лагранжа
        double phaseShift1 = 0;              // Первая точка
        double phaseShift2 = 2 * Math.PI / 3; // Вторая точка (120 градусов)
        double phaseShift3 = 4 * Math.PI / 3; // Третья точка (240 градусов)

        // Вычисляем положение для первой точки (с учетом центра)
        x1 = centerX + radiusX * Math.cos(angle + phaseShift1);
        y1 = centerY + radiusY * Math.sin(angle + phaseShift1);

        // Вычисляем положение для второй точки
        x2 = centerX + radiusX * Math.cos(angle + phaseShift2);
        y2 = centerY + radiusY * Math.sin(angle + phaseShift2);

        // Вычисляем положение для третьей точки
        x3 = centerX + radiusX * Math.cos(angle + phaseShift3);
        y3 = centerY + radiusY * Math.sin(angle + phaseShift3);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Масштаб для удобства отображения (небольшое увеличение)
        int scale = 1;

        // Рисуем точки
        g2d.setColor(Color.RED);
        g2d.fillOval((int) (x1 * scale), (int) (y1 * scale), 20, 20);

        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) (x2 * scale), (int) (y2 * scale), 20, 20);

        g2d.setColor(Color.GREEN);
        g2d.fillOval((int) (x3 * scale), (int) (y3 * scale), 20, 20);
    }
}
