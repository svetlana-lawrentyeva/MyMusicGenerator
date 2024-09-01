package ivko.lana.neurotone.interesting.three_body_problem;

public class UpDownThreeBodyProblem extends ThreeBodyProblem
{
    protected static final int LENGTH = 120;
    private double angle = 0; // Угол для расчета движения

    public UpDownThreeBodyProblem()
    {
        super();
    }

    @Override
    protected void init()
    {
        dt = 0.02;

        // Инициализация начальных углов движения для каждой точки
        double initialAngle1 = 0;              // Начальный угол для первой точки
        double initialAngle2 = Math.PI / 3;    // Начальный угол для второй точки (60 градусов)
        double initialAngle3 = 2 * Math.PI / 3; // Начальный угол для третьей точки (120 градусов)

        // Радиусы для орбит по осям X и Y
        double radiusX = 1.5;
        double radiusY = 0.8;

        // Инициализация положения тел в зависимости от их углов
        x1 = radiusX * Math.sin(initialAngle1);
        y1 = radiusY * Math.cos(initialAngle1);

        x2 = radiusX * Math.sin(initialAngle2);
        y2 = radiusY * Math.cos(initialAngle2);

        x3 = radiusX * Math.sin(initialAngle3);
        y3 = radiusY * Math.cos(initialAngle3);

        // Начальные скорости можно оставить нулевыми, если они не нужны для кругового движения
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

        // Параметры для движения
        double radiusX = 1.5;  // Радиус по X
        double radiusY = 0.8;  // Радиус по Y

        // Углы наклона траекторий для каждой точки (например, 30, 60 и 90 градусов)
        double tilt1 = Math.toRadians(30); // Наклон первой траектории на 30 градусов
        double tilt2 = Math.toRadians(60); // Наклон второй траектории на 60 градусов
        double tilt3 = Math.toRadians(90); // Наклон третьей траектории на 90 градусов

        // Положение первой точки с учётом наклона
        x1 = radiusX * Math.sin(angle) * Math.cos(tilt1);
        y1 = radiusY * Math.cos(angle) * Math.sin(tilt1);

        // Положение второй точки с учётом наклона
        x2 = radiusX * Math.sin(angle) * Math.cos(tilt2);
        y2 = radiusY * Math.cos(angle) * Math.sin(tilt2);

        // Положение третьей точки с учётом наклона
        x3 = radiusX * Math.sin(angle) * Math.cos(tilt3);
        y3 = radiusY * Math.cos(angle) * Math.sin(tilt3);
    }
}
