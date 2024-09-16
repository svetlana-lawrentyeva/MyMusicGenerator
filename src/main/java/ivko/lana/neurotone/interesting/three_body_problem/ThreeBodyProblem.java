package ivko.lana.neurotone.interesting.three_body_problem;

/**
 * @author Lana Ivko
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class ThreeBodyProblem extends JPanel implements ActionListener
{
    protected Timer timer;
    protected double t = 0; // Время
    protected double dt = 0.02; // Шаг времени

    // Начальные условия для решения "восьмерка"
    protected double x1;
    protected double y1;
    protected double x2;
    protected double y2;
    protected double x3 = 0;
    protected double y3 = 0;

    protected double vx1;
    protected double vy1;
    protected double vx2;
    protected double vy2;
    protected double vx3;
    protected double vy3;

    protected double G; // Гравитационная постоянная
    protected double m1 = 1;
    protected double m2 = 1;
    protected double m3 = 1; // Массы тел

    protected ArrayList<Point2D.Double> trail1 = new ArrayList<>();
    protected ArrayList<Point2D.Double> trail2 = new ArrayList<>();
    protected ArrayList<Point2D.Double> trail3 = new ArrayList<>();

    public ThreeBodyProblem()
    {
        init();
        timer = new Timer(20, this);
        timer.start();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    protected abstract void init();

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        int scale = 200; // Масштаб для удобства отображения

        // Вычисляем координаты точек на экране
        int screenX1 = (int) (400 + scale * x1);
        int screenY1 = (int) (300 - scale * y1);
        int screenX2 = (int) (400 + scale * x2);
        int screenY2 = (int) (300 - scale * y2);
        int screenX3 = (int) (400 + scale * x3);
        int screenY3 = (int) (300 - scale * y3);

        // Добавляем текущие позиции в следы
        trail1.add(new Point2D.Double(screenX1, screenY1));
        trail2.add(new Point2D.Double(screenX2, screenY2));
        trail3.add(new Point2D.Double(screenX3, screenY3));

        // Ограничиваем длину следа
        if (trail1.size() > getLength()) trail1.remove(0);
        if (trail2.size() > getLength()) trail2.remove(0);
        if (trail3.size() > getLength()) trail3.remove(0);


//         Рисуем следы с правильной прозрачностью и толщиной
//        drawSmoothTrail(g2d, trail1, new Color(100, 205, 140)); // Оранжевый след
//        drawSmoothTrail(g2d, trail2, new Color(70, 130, 180)); // Голубой след
//        drawSmoothTrail(g2d, trail3, new Color(255, 150, 205)); // Светло-желтый след

        // Рисуем тела с плавным градиентным свечением
        drawSmoothGlowingCircle(g2d, screenX1, screenY1, new Color(120, 205, 150)); // Оранжевое свечение
        drawSmoothGlowingCircle(g2d, screenX2, screenY2, new Color(135, 206, 250)); // Голубое свечение
        drawSmoothGlowingCircle(g2d, screenX3, screenY3, new Color(255, 150, 200)); // Светло-желтое свечение

        // Обновляем координаты и скорости
        updatePositions();
    }

    protected abstract int getLength();


    private void drawSmoothTrail(Graphics2D g2d, ArrayList<Point2D.Double> trail, Color color)
    {
        // Рисуем сияние вокруг хвоста
        int glowLayers = 20; // Количество слоев для более плавного перехода
        for (int j = 0; j < glowLayers; j++)
        {
            for (int i = 0; i < trail.size() - 1; i++)
            {
                Point2D.Double p1 = trail.get(i);
                Point2D.Double p2 = trail.get(i + 1);

                float glowAlpha = (1 - (float) j / glowLayers) * (float) i / (trail.size() - 1) * 0.02f; // Прозрачность сияния уменьшается к краям и к концу хвоста
                int glowRadius = (int) (20 * glowAlpha * j * 10) + 1; // Радиус сияния увеличивается с каждым слоем, чтобы расширять его

                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (glowAlpha * 230)));
                g2d.setStroke(new BasicStroke(glowRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
            }
        }

        // Рисуем хвосты с правильной толщиной и прозрачностью
        for (int i = 0; i < trail.size() - 1; i++)
        {
            Point2D.Double p1 = trail.get(i);
            Point2D.Double p2 = trail.get(i + 1);

            float alpha = (float) (((float) i / (trail.size() - 1)) * 0.2); // Прозрачность уменьшается к концу
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));

            float thickness = 6 * alpha + 1; // Толщина уменьшается к концу
            g2d.setStroke(new BasicStroke(thickness));

            g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
        }
    }


    private void drawSmoothGlowingCircle(Graphics2D g2d, int x, int y, Color color)
    {
        // Увеличим количество слоев для более насыщенного свечения
        int glowLayers = 50; // Количество слоев для сияния

        for (int i = 0; i < glowLayers; i++)
        {
            int radius = 5 + i * 2; // Радиус увеличивается на каждом слое
            float alpha = (1.0f - (float) i / glowLayers) * 0.5f; // Прозрачность плавно уменьшается
            Color glowColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));

            g2d.setColor(glowColor);
            g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }

        // Рисуем центральную точку с более насыщенным цветом
        g2d.setColor(color);
        g2d.fillOval(x - 8, y - 8, 16, 16); // Центральная точка увеличена для большей выразительности
    }

    protected abstract void updatePositions();

    @Override
    public void actionPerformed(ActionEvent e)
    {
        repaint();
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Three Body Problem");
        ThreeBodyProblem panel = new UpDownThreeBodyProblem();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}


