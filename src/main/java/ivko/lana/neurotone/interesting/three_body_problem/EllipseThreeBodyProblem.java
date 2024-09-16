package ivko.lana.neurotone.interesting.three_body_problem;

public class EllipseThreeBodyProblem extends ThreeBodyProblem
{
    protected static final int LENGTH = 120;
    private double angle = 0; // Угол для расчета движения

    public EllipseThreeBodyProblem()
    {
        super();
    }

    @Override
    protected void init()
    {
        dt = 0.02;
        // Инициализация положения тел в центре
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;
        x3 = 0;
        y3 = 0;

        // Начальные скорости можно оставить нулевыми, так как они больше не нужны для кругового движения
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
        // Обновляем угол движения
        angle += dt;

        // Радиус и смещения для каждой из трех точек
        double radiusX = 1.5;  // Радиус по X
        double radiusY = 0.8;  // Радиус по Y
        double offsetX = 0;    // Смещение по X
        double offsetY = 0;    // Смещение по Y

        // Теперь точки будут двигаться по эллиптическим траекториям
        x1 = radiusX * Math.sin(angle) + offsetX;
        y1 = radiusY * Math.cos(angle) + offsetY;

        x2 = radiusX * Math.sin(angle + 2 * Math.PI / 3) + offsetX;
        y2 = radiusY * Math.cos(angle + 2 * Math.PI / 3) + offsetY;

        x3 = radiusX * Math.sin(angle + 4 * Math.PI / 3) + offsetX;
        y3 = radiusY * Math.cos(angle + 4 * Math.PI / 3) + offsetY;

        // Скорости остаются нулевыми, так как они не требуются для этой анимации
    }
}
