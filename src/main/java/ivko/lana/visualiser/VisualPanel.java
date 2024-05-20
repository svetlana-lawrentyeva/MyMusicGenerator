package ivko.lana.visualiser;

import ivko.lana.generators.Initializer;
import ivko.lana.generators.MusicGenerator;
import ivko.lana.musicentities.Music;
import ivko.lana.util.MusicUtil;

import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class VisualPanel extends JPanel
{
    private static final String SAVE_DIRECTORY = "D:\\music\\generated\\";
    private JButton saveButton_;
    private JButton playButton_;
    private Synthesizer synthesizer_;
    private AudioRecorder audioRecorder_;

    public VisualPanel()
    {
        super(new GridBagLayout());
        synthesizer_ = MusicUtil.getInstance().getSynthesizer();
        prepare();
    }

    private void prepare()
    {
        saveButton_ = new JButton("Save");
        playButton_ = new JButton("Play");
        saveButton_.addActionListener(e -> SwingUtilities.invokeLater(() ->
        {
            try
            {
                audioRecorder_.stopRecording();
                String fileName = generateNewName();
                audioRecorder_.saveToMp3(fileName);
                JOptionPane.showMessageDialog(null, String.format("Audio saved to %s", fileName));
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }));
        playButton_.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>()
                {
                    @Override
                    protected Void doInBackground() throws Exception
                    {
                        try
                        {
                            audioRecorder_ = new AudioRecorder();
                            audioRecorder_.startRecording();
                            playMusic(); // Ваш метод для воспроизведения музыки
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void done()
                    {
                        playButton_.setEnabled(true);
                    }
                };
                playButton_.setEnabled(false);
                swingWorker.execute();
            }
        });


        add(playButton_, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(saveButton_, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

    private String generateNewName()
    {
        Date now = new Date();

        // Определение формата
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss-ddMMyyyy");

        // Форматирование даты
        return SAVE_DIRECTORY + dateFormat.format(now) + ".mp3";
    }

    private void playMusic() throws InterruptedException
    {

        Initializer initializer = new Initializer();
        MusicGenerator musicGenerator = new MusicGenerator(initializer);
        Music music = musicGenerator.generate();
        music.play();
    }

    public void setData(List<Integer> data)
    {

    }
}
