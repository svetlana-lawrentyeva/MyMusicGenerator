package ivko.lana.visualiser;

import ivko.lana.generators.Initializer;
import ivko.lana.generators.MusicGenerator;
import ivko.lana.musicentities.Channel;
import ivko.lana.musicentities.IPlayable;
import ivko.lana.musicentities.ISound;
import ivko.lana.musicentities.Music;
import ivko.lana.util.MusicUtil;

import javax.sound.midi.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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

    private Music music_;
    private Initializer initializer_;

    public VisualPanel()
    {
        super(new GridBagLayout());
        synthesizer_ = MusicUtil.getInstance().getSynthesizer();
        initializer_ = new Initializer();
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
//                audioRecorder_.stopRecording();
                String fileName = generateNewName();
                save(fileName);
//                audioRecorder_.saveToFile(fileName);
                JOptionPane.showMessageDialog(null, String.format("Audio saved to %s", fileName));
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }));
        playButton_.addActionListener(e ->
        {
            if (playButton_.getText().equals("Play"))
            {
                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>()
                {
                    @Override
                    protected Void doInBackground() throws Exception
                    {
                        try
                        {
//                            audioRecorder_ = new AudioRecorder();
//                            audioRecorder_.startRecording();
                            playMusic(initializer_); // Ваш метод для воспроизведения музыки
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void done()
                    {
                        playButton_.setText("Play");
                    }
                };
                playButton_.setText("Stop");
//                if (audioRecorder_ != null)
//                {
//                    audioRecorder_.stopRecording();
//                }
                swingWorker.execute();
            }
            else
            {
//                audioRecorder_.startRecording();
                try
                {
                    stopMusic();
                } catch (InterruptedException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });


        add(playButton_, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(saveButton_, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

    private void save(String fileName)
    {
        try
        {
            Sequence sequence = new Sequence(Sequence.PPQ, 24);
            Track track = sequence.createTrack();
            writeToTrack(music_, track);
            MidiSystem.write(sequence, 1, new File(fileName + ".mid"));
            convertMidiToWav( fileName + ".mid", fileName + ".wav");
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void writeToTrack(Music music, Track track) throws InvalidMidiDataException
    {
        List<Channel> channels = music.getChannels();
        for (Channel channel : channels)
        {
            float currentBeat = 0;
            List<IPlayable> playables = channel.getPlayables();
            for (IPlayable playable : playables)
            {
                if (playable instanceof ISound)
                {
                    ISound sound = (ISound) playable;
                    addNoteToTrack(track, currentBeat++, sound.getTone(), sound.getAccent(), sound.getDuration(), channel.getChannelNumber());
                }
            }
        }
    }

    private static void addNoteToTrack(Track track, float startBeat, int note, int velocity, float duration, int channel) throws InvalidMidiDataException {
        long startTick = (long)(startBeat * 24); // Преобразование битов в тики
        long endTick = (long)((startBeat + duration) * 24);

        ShortMessage onMessage = new ShortMessage();
        onMessage.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
        MidiEvent noteOn = new MidiEvent(onMessage, startTick);
        track.add(noteOn);

        ShortMessage offMessage = new ShortMessage();
        offMessage.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
        MidiEvent noteOff = new MidiEvent(offMessage, endTick);
        track.add(noteOff);
    }

    public static void convertMidiToWav(String midiFilePath, String wavFilePath) throws Exception {
        Sequence sequence = MidiSystem.getSequence(new File(midiFilePath));

        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();

        Receiver receiver = synthesizer.getReceiver();
        Transmitter transmitter = MidiSystem.getTransmitter();
        transmitter.setReceiver(receiver);

        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        AudioInputStream ais = new AudioInputStream(line);

        Thread midiThread = new Thread(() -> {
            try {
                Sequencer sequencer = MidiSystem.getSequencer(false);
                sequencer.setSequence(sequence);
                sequencer.open();
                sequencer.start();
                while (sequencer.isRunning()) {
                    Thread.sleep(100);
                }
                sequencer.stop();
                sequencer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        midiThread.start();

        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(wavFilePath));
        midiThread.join();

        line.stop();
        line.close();
        synthesizer.close();
    }

    private String generateNewName()
    {
        Date now = new Date();

        // Определение формата
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss-ddMMyyyy");

        // Форматирование даты
        return SAVE_DIRECTORY + dateFormat.format(now);
    }

    private void playMusic(Initializer initializer) throws InterruptedException
    {
        MusicGenerator musicGenerator = new MusicGenerator(initializer);
        music_ = musicGenerator.generate();
        music_.play();
    }

    private void stopMusic() throws InterruptedException
    {
        music_.stop();
    }
}
