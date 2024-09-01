package ivko.lana.visualiser;

import javax.swing.*;
import java.awt.*;

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
        Dimension minimumSize = new Dimension(500, 200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(minimumSize);
        setLocation(screenSize.width / 2 - minimumSize.width / 2, screenSize.height / 2 - minimumSize.height / 2);
        visualPanel_ = new VisualPanel(this);
        add(visualPanel_);
    }
}
