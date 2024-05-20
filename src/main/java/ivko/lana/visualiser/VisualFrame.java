package ivko.lana.visualiser;

import ivko.lana.util.MusicUtil;

import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class VisualFrame extends JFrame
{
    private VisualPanel visualPanel_;

    public VisualFrame()
    {
        // Настройка основного фрейма
        setTitle("Full Screen Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Получаем устройство графики для управления экраном
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Получаем размер экрана
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setMinimumSize(screenSize); // Устанавливаем минимальный размер окна равным размеру экрана



        visualPanel_ = new VisualPanel();
        // Добавляем панель, где можно будет рисовать графики
//        JPanel panel = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                // Тут можно добавлять свои графики
//                // Например, рисуем простой прямоугольник
//                g.drawRect(50, 50, 100, 100);
//            }
//        };

        // Добавляем панель на фрейм
        add(visualPanel_);
    }

    public void addData(List<Integer> data)
    {
        visualPanel_.setData(data);
    }
}
