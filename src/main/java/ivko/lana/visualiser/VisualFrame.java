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
        Dimension minimumSize = new Dimension(500, 200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setMinimumSize(minimumSize);
        setLocation(screenSize.width / 2 - minimumSize.width / 2, screenSize.height / 2 - minimumSize.height / 2);
        visualPanel_ = new VisualPanel(this);
        add(visualPanel_);
    }
}
