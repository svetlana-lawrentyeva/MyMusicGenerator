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

public class EightThreeBodyProblem extends ThreeBodyProblem
{
    protected static final int LENGTH = 120;
    public EightThreeBodyProblem()
    {
        super();
    }

    protected void init()
    {
        dt = 0.02;

        x1 = 0.97000436;
        y1 = -0.24308753;
        x2 = -x1;
        y2 = -y1;
        x3 = 0;
        y3 = 0;

        vx1 = 0.4662036850;
        vy1 = 0.4323657300;
        vx2 = vx1;
        vy2 = vy1;
        vx3 = -2 * vx1;
        vy3 = -2 * vy1;

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

    protected void updatePositions()
    {
        // Вычисляем расстояния между телами
        double r12 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double r13 = Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
        double r23 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));

        // Вычисляем силы и ускорения
        double ax1 = G * m2 * (x2 - x1) / Math.pow(r12, 3) + G * m3 * (x3 - x1) / Math.pow(r13, 3);
        double ay1 = G * m2 * (y2 - y1) / Math.pow(r12, 3) + G * m3 * (y3 - y1) / Math.pow(r13, 3);

        double ax2 = G * m1 * (x1 - x2) / Math.pow(r12, 3) + G * m3 * (x3 - x2) / Math.pow(r23, 3);
        double ay2 = G * m1 * (y1 - y2) / Math.pow(r12, 3) + G * m3 * (y3 - y2) / Math.pow(r23, 3);

        double ax3 = G * m1 * (x1 - x3) / Math.pow(r13, 3) + G * m2 * (x2 - x3) / Math.pow(r23, 3);
        double ay3 = G * m1 * (y1 - y3) / Math.pow(r13, 3) + G * m2 * (y2 - y3) / Math.pow(r23, 3);

        // Обновляем скорости
        vx1 += ax1 * dt;
        vy1 += ay1 * dt;
        vx2 += ax2 * dt;
        vy2 += ay2 * dt;
        vx3 += ax3 * dt;
        vy3 += ay3 * dt;

        // Обновляем координаты
        x1 += vx1 * dt;
        y1 += vy1 * dt;
        x2 += vx2 * dt;
        y2 += vy2 * dt;
        x3 += vx3 * dt;
        y3 += vy3 * dt;
    }
}


