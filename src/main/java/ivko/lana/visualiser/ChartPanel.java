package ivko.lana.visualiser;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Lana Ivko
 */
class ChartPanel extends JPanel
{
    private List<Integer> data_;

    public ChartPanel() {
        int height = 300; // Высота панели для графика
        setPreferredSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), height));
        setBackground(Color.WHITE);
    }

    public void setData(List<Integer> data)
    {
        data_ = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data_ == null || data_.isEmpty()) {
            return;
        }

        int minSpacing = 5; // Минимальное расстояние между точками в пикселях
        int unitHeight = 5; // Высота одной единицы данных в пикселях
        int maxWidth = getWidth();
        int maxHeight = getHeight();

        int totalDataPoints = data_.size();
        int requiredWidth = totalDataPoints * minSpacing;
        int rows = (int) Math.ceil((double) requiredWidth / maxWidth);
        int rowHeight = maxHeight / rows;

        // Найти максимальное и минимальное значение данных
        int maxDataValue = data_.stream().max(Integer::compare).orElse(1);
        int minDataValue = data_.stream().min(Integer::compare).orElse(0);

        // Определить масштаб для высоты
        int maxDataRange = maxDataValue - minDataValue;
        int requiredHeight = maxDataRange * unitHeight;
        if (requiredHeight < rowHeight) {
            unitHeight = rowHeight / maxDataRange;
        }

        g.setColor(Color.BLUE);

        for (int row = 0; row < rows; row++) {
            int startIndex = row * (maxWidth / minSpacing);
            int endIndex = Math.min(startIndex + (maxWidth / minSpacing), totalDataPoints);

            int prevX = 0;
            int prevY = rowHeight / 2; // Центр строки

            for (int i = startIndex; i < endIndex; i++) {
                int x = (i - startIndex) * minSpacing;
                int y = rowHeight - (int) (((data_.get(i) - minDataValue) * unitHeight)) + (row * rowHeight);

                if (i != startIndex) { // Избегаем рисования линии с начальной точки
                    g.drawLine(prevX, prevY, x, y);
                }
                prevX = x;
                prevY = y;
            }
        }
    }





}
