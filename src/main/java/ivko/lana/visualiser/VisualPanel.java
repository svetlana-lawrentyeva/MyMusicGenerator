package ivko.lana.visualiser;

import ivko.lana.converter.MidiToAudioConverter;
import ivko.lana.entities.IScale;
import ivko.lana.generators.ChordChannelGenerator;
import ivko.lana.generators.DrumsChannelGenerator;
import ivko.lana.generators.Initializer;
import ivko.lana.generators.MusicGenerator;
import ivko.lana.musicentities.*;
import ivko.lana.util.MusicUtil;
import ivko.lana.yaml.RhythmDetails;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class VisualPanel extends JPanel
{
    public static final int TICK_RESOLUTION = 12;
    private JButton saveButton_;
    private JButton playButton_;
    private JSpinner minutesSpinner_;
    private JComboBox<IScale> scaleComboBox_;
    private JComboBox<InstrumentCode> soloInstrumentComboBox_;
    private List<JComboBox<InstrumentCode>> chordComboBoxes_ = new ArrayList<>();
    private JPanel chordInstrumentsPanel_;
    private JComboBox<RhythmDetails> rhythmSizeComboBox_;
    private Synthesizer synthesizer_;

    private Music music_;
    private Initializer initializer_;

    private JFrame parentFrame_;

    private boolean isTest_ = Initializer.isTest();

    public VisualPanel(JFrame parentFrame)
    {
        super(new GridBagLayout());
        parentFrame_ = parentFrame;
        synthesizer_ = MusicUtil.getInstance().getSynthesizer();
        initializer_ = new Initializer();
        prepare();
        if (isTest_)
        {
            playTestMusic();
//            playPlamenev();
//            playGamma();
        }
    }

    private void playSound()
    {
        try
        {
            int channelNumber = 0;
            List<Rhythm> rhythms = new ArrayList<>();
            List<ISound> sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);


            List<Phrase> phrases = new ArrayList<>();
            phrases.add(new Phrase(rhythms, channelNumber));
            List<Part> parts = new ArrayList<>();
            parts.add(new Part(phrases, channelNumber));
            List<Channel> channels = new ArrayList<>();
            Channel melodyChannel = new Channel(parts, 0, channelNumber);
            channels.add(melodyChannel);

            melodyChannel.setIsMelody(true);

            ChordChannelGenerator chordChannelGenerator = new ChordChannelGenerator(initializer_, 1);
            chordChannelGenerator.setMelodyChannel(melodyChannel);
            channels.add(chordChannelGenerator.generate());
//
            DrumsChannelGenerator drumsChannelGenerator = new DrumsChannelGenerator(initializer_);
            drumsChannelGenerator.setMelodyChannel(melodyChannel);
            channels.add(drumsChannelGenerator.generate());

            music_ = new Music(channels);
            music_.play();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void playGamma()
    {

        try
        {
            int channelNumber = 0;
            List<Rhythm> rhythms = new ArrayList<>();
            List<ISound> sounds = new ArrayList<>();
            sounds.add(new Note(0, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 8, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(4, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(7, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(11, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            List<Phrase> phrases = new ArrayList<>();
            phrases.add(new Phrase(rhythms, channelNumber));
            List<Part> parts = new ArrayList<>();
            parts.add(new Part(phrases, channelNumber));
            List<Channel> channels = new ArrayList<>();
            Channel melodyChannel = new Channel(parts, 0, channelNumber);
//            channels.add(melodyChannel);

            melodyChannel.setIsMelody(true);

            ChordChannelGenerator chordChannelGenerator = new ChordChannelGenerator(initializer_, 1);
            chordChannelGenerator.setMelodyChannel(melodyChannel);
            channels.add(chordChannelGenerator.generate());
//
            DrumsChannelGenerator drumsChannelGenerator = new DrumsChannelGenerator(initializer_);
            drumsChannelGenerator.setMelodyChannel(melodyChannel);
            channels.add(drumsChannelGenerator.generate());

            music_ = new Music(channels);
            music_.play();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void playPlamenev()
    {
        try
        {
            int channelNumber = 0;
            List<Rhythm> rhythms = new ArrayList<>();
// Первый такт
            List<ISound> sounds = new ArrayList<>();
            sounds.add(new Note(0, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Второй такт
            sounds = new ArrayList<>();
            sounds.add(new Note(7, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Третий такт
            sounds = new ArrayList<>();
            sounds.add(new Note(0, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Четвертый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(9, 12, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 2, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 2, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 16, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);
// Пятый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(0, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Шестой такт
            sounds = new ArrayList<>();
            sounds.add(new Note(7, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Седьмой такт
            sounds = new ArrayList<>();
            sounds.add(new Note(0, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Восьмой такт
            sounds = new ArrayList<>();
            sounds.add(new Note(9, 12, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 2, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 2, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 16, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Девятый такт
            sounds = new ArrayList<>();
            sounds.add(new Pause(32, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Десятый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Одиннадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двенадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Тринадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 16, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(16, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Четырнадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Пятнадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Шестнадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Семнадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 16, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            sounds.add(new Pause(4, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            sounds.add(new Note(2, 2, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 2, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Восемнадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Девятнадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать первый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 16, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(16, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать второй такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать третий такт
            sounds = new ArrayList<>();
            sounds.add(new Note(5, 8, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать четвёртый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать пятый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 16, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(16, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать шестой такт
            sounds = new ArrayList<>();
            sounds.add(new Note(11, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 16, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать седьмой такт
            sounds = new ArrayList<>();
            sounds.add(new Note(12, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 16, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать восьмой такт
            sounds = new ArrayList<>();
            sounds.add(new Note(11, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(11, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Двадцать девятый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(12, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(12, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Тридцатый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(0, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 12, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Тридцать первый такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

// Тридцать второй такт
            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 100, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Pause(4, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            sounds.add(new Pause(8, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            sounds.add(new Pause(16, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier(), channelNumber));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);


            List<Phrase> phrases = new ArrayList<>();
            phrases.add(new Phrase(rhythms, channelNumber));
            List<Part> parts = new ArrayList<>();
            parts.add(new Part(phrases, channelNumber));
            List<Channel> channels = new ArrayList<>();
            Channel melodyChannel = new Channel(parts, 0, channelNumber);
            channels.add(melodyChannel);

            melodyChannel.setIsMelody(true);

            ChordChannelGenerator chordChannelGenerator = new ChordChannelGenerator(initializer_, 1);
            chordChannelGenerator.setMelodyChannel(melodyChannel);
            channels.add(chordChannelGenerator.generate());

            DrumsChannelGenerator drumsChannelGenerator = new DrumsChannelGenerator(initializer_);
            drumsChannelGenerator.setMelodyChannel(melodyChannel);
            channels.add(drumsChannelGenerator.generate());

            music_ = new Music(channels);
            music_.play();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void playTestMusic()
    {
        try
        {
            int channelNumber = 0;
            List<Rhythm> rhythms = new ArrayList<>();
            List<ISound> sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(7, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(7, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(0, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(9, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(7, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(4, 4, 75, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(5, 4, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(2, 4, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            sounds = new ArrayList<>();
            sounds.add(new Note(0, 8, 70, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            sounds.add(new Note(0, 8, 85, channelNumber, initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier()));
            rhythms.add(new Rhythm(sounds, channelNumber));
            rhythms.add(RhythmSeparator.SEPARATOR);

            List<Phrase> phrases = new ArrayList<>();
            phrases.add(new Phrase(rhythms, channelNumber));
            List<Part> parts = new ArrayList<>();
            parts.add(new Part(phrases, channelNumber));
            List<Channel> channels = new ArrayList<>();
            int partMultiplier = 1;
            List<Part> multiplesParts = new ArrayList<>();
            for (int i = 0; i < partMultiplier; ++i)
            {
                multiplesParts.addAll(parts);
            }
            Channel melodyChannel = new Channel(multiplesParts, 0, channelNumber);
            channels.add(melodyChannel);

            melodyChannel.setIsMelody(true);

            ChordChannelGenerator chordChannelGenerator = new ChordChannelGenerator(initializer_, 1);
            chordChannelGenerator.setMelodyChannel(melodyChannel);
//            channels.add(chordChannelGenerator.generate());
//
            DrumsChannelGenerator drumsChannelGenerator = new DrumsChannelGenerator(initializer_);
            drumsChannelGenerator.setMelodyChannel(melodyChannel);
//            channels.add(drumsChannelGenerator.generate());

            music_ = new Music(channels);
            music_.play();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void prepare()
    {
        minutesSpinner_ = createMinutesSpinner();
        rhythmSizeComboBox_ = createRhythmSizeComboBox();
        scaleComboBox_ = createScaleComboBox();
        soloInstrumentComboBox_ = createInstrumentComboBox(0);
        saveButton_ = new JButton("Save");
        playButton_ = new JButton("Play");
        if (isTest_)
        {
            playButton_.setEnabled(false);
        }
        saveButton_.addActionListener(e -> SwingUtilities.invokeLater(this::saveCommand));
        playButton_.addActionListener(e -> playCommand());

        add(minutesSpinner_, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(scaleComboBox_, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(rhythmSizeComboBox_, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        add(createInstrumentPanel(soloInstrumentComboBox_), new GridBagConstraints(0, 1, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        chordInstrumentsPanel_ = new JPanel();
        BoxLayout boxLayout = new BoxLayout(chordInstrumentsPanel_, BoxLayout.Y_AXIS);
        chordInstrumentsPanel_.setLayout(boxLayout);
        add(chordInstrumentsPanel_, new GridBagConstraints(0, 2, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton_);
        buttonPanel.add(saveButton_);
        add(buttonPanel, new GridBagConstraints(0, 3, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    }

    private JPanel createInstrumentPanel(JComboBox<InstrumentCode> instrumentComboBox)
    {
        JPanel instrumentPanel = new JPanel(new GridBagLayout());

        JButton addButton = new JButton("+");
        addButton.addActionListener(e ->
        {
            JComboBox<InstrumentCode> newInstrumentComboBox = createInstrumentComboBox(chordComboBoxes_.size() + 1);
            chordComboBoxes_.add(newInstrumentComboBox);
            chordInstrumentsPanel_.add(createInstrumentPanel(newInstrumentComboBox));
            ChordChannelGenerator chordChannelGenerator = new ChordChannelGenerator(initializer_, chordComboBoxes_.size());
            //                Channel channel = chordChannelGenerator.generate();
//                music_.getChannels()
            revalidate();
            repaint();
            parentFrame_.pack();
            parentFrame_.validate();
        });

        JButton removeButton = new JButton("-");
        removeButton.addActionListener(e ->
        {
            chordComboBoxes_.remove(instrumentComboBox);
            chordInstrumentsPanel_.remove(instrumentPanel);
            revalidate();
            repaint();
            parentFrame_.pack();
            parentFrame_.validate();
        });

        instrumentPanel.add(instrumentComboBox, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        instrumentPanel.add(addButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        instrumentPanel.add(removeButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        return instrumentPanel;
    }

    private JSpinner createMinutesSpinner()
    {
        SpinnerModel spinnerModel = new SpinnerNumberModel(60, 1, 120, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setValue(initializer_.getMinutes());
        spinner.addChangeListener(e -> initializer_.setMinutes((Integer) spinner.getValue()));
        return spinner;
    }

    private void saveCommand()
    {
        try
        {
            String fileName = MusicUtil.generateNewName();
            save(fileName);
            JOptionPane.showMessageDialog(null, String.format("Audio saved to %s", fileName));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void playCommand()
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
//                        playTestMusic();
                        playMusic(initializer_); // Ваш метод для воспроизведения музыки
                    }
                    catch (Exception ex)
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
            swingWorker.execute();
        }
        else
        {
            try
            {
                stopMusic();
            }
            catch (InterruptedException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private JComboBox<RhythmDetails> createRhythmSizeComboBox()
    {
        JComboBox<RhythmDetails> comboBox = new JComboBox<>(initializer_.getMelodyRhythmDetails().toArray(new RhythmDetails[0]));
        comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
        {
            JLabel label = new JLabel();
            if (value != null)
            {
                label.setText(value.getName());
            }
            if (isSelected)
            {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            else
            {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            label.setOpaque(true);
            return label;
        });
        comboBox.addItemListener(e -> onRhythmSizeChanged());
        comboBox.setSelectedItem(initializer_.getScale());
        return comboBox;
    }

    private JComboBox<InstrumentCode> createInstrumentComboBox(int channel)
    {
        JComboBox<InstrumentCode> comboBox = new JComboBox<>(initializer_.getInstrumentCodesConfig().getInstrumentCodes().toArray(new InstrumentCode[0]));
        comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
        {
            JLabel label = new JLabel();
            if (value != null)
            {
                label.setText(value.getName());
                if (isSelected)
                {
                    label.setBackground(list.getSelectionBackground());
                    if (value.isMeditative())
                    {
                        label.setForeground(Color.BLUE);
                    }
                    else
                    {
                        label.setForeground(list.getSelectionForeground());
                    }
                }
                else
                {
                    label.setBackground(list.getBackground());
                    if (value.isMeditative())
                    {
                        label.setForeground(Color.BLUE);
                    }
                    else
                    {
                        label.setForeground(list.getForeground());
                    }
                }
            }
            label.setOpaque(true);
            return label;
        });
        comboBox.addItemListener(e -> onInstrumentCodeChanged(channel));
        comboBox.setSelectedItem(initializer_.getMelodyInstrumentCode());
        return comboBox;
    }

    private void onInstrumentCodeChanged(int channel)
    {
        JComboBox<InstrumentCode> comboBox = channel == 0
        ? soloInstrumentComboBox_
                : chordComboBoxes_.get(channel - 1);
        InstrumentCode selectedItem = (InstrumentCode) comboBox.getSelectedItem();
        if (selectedItem != null)
        {
            if (channel == 0)
            {
                initializer_.setSoloInstrument(selectedItem.getCode());
            }
            else
            {
                initializer_.setChordInstrument(channel, selectedItem.getCode());
            }
        }

    }

    private void onRhythmSizeChanged()
    {
        initializer_.setMelodyPrimaryRhythmDetails((RhythmDetails) rhythmSizeComboBox_.getSelectedItem());
        initializer_.setChordPrimaryRhythmDetails((RhythmDetails) rhythmSizeComboBox_.getSelectedItem());
    }

    private JComboBox<IScale> createScaleComboBox()
    {
        JComboBox<IScale> comboBox = new JComboBox<>(initializer_.getScales().toArray(new IScale[0]));
        comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
        {
            JLabel label = new JLabel();
            if (value != null)
            {
                label.setText(value.getName());
            }
            if (isSelected)
            {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            else
            {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            label.setOpaque(true);
            return label;
        });
        comboBox.addItemListener(e -> onScaleChanged(comboBox));
        comboBox.setSelectedItem(initializer_.getScale());
        return comboBox;
    }

    private void onScaleChanged(JComboBox<IScale> comboBox)
    {
        initializer_.setScale((IScale) comboBox.getSelectedItem());
        RhythmDetails selectedItem = (RhythmDetails) rhythmSizeComboBox_.getSelectedItem();
        List<RhythmDetails> melodyRhythmDetails = initializer_.getMelodyRhythmDetails();
        rhythmSizeComboBox_.setModel(new DefaultComboBoxModel<>(melodyRhythmDetails.toArray(new RhythmDetails[0])));
        if (melodyRhythmDetails.contains(selectedItem))
        {
            rhythmSizeComboBox_.setSelectedItem(selectedItem);
        }
        else
        {
            rhythmSizeComboBox_.setSelectedIndex(0);
        }
        onRhythmSizeChanged();
    }

    private void save(String fileName)
    {
        try
        {
            Sequence sequence = new Sequence(Sequence.PPQ, TICK_RESOLUTION);
            Track track = sequence.createTrack();

            track.add(new MidiEvent(createTempoMessage(), 0));

            writeToTrack(music_, track);
            File midiFile = new File(fileName + ".mid");
            MidiSystem.write(sequence, 1, midiFile);

            MidiToAudioConverter.convertWithTimidity(fileName + ".mid", fileName + ".wav");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private MetaMessage createTempoMessage() throws InvalidMidiDataException
    {
        RhythmDetails rhythmDetails = initializer_.getMelodyPrimaryRhythmDetails();
        int tempoInMPQ = 60000000 / (rhythmDetails.getBaseDurationMultiplier() * rhythmDetails.getBaseDuration());
        MetaMessage tempoMessage = new MetaMessage();
        byte[] tempoBytes = {
                (byte) (tempoInMPQ >> 16),
                (byte) (tempoInMPQ >> 8),
                (byte) tempoInMPQ
        };
        tempoMessage.setMessage(0x51, tempoBytes, tempoBytes.length);
        return tempoMessage;
    }

    private void writeToTrack(Music music, Track track) throws InvalidMidiDataException
    {
//        ShortMessage msg = new ShortMessage();
//        msg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93); // нота C4
//        track.add(new MidiEvent(msg, 0));
//        msg = new ShortMessage();
//        msg.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
//        track.add(new MidiEvent(msg, 24));


        List<Channel> channels = music.getChannels();
        for (Channel channel : channels)
        {
            if (channel.getChannelNumber() != MusicUtil.DRUMS_CHANNEL_NUMBER)
            {
                setInstrument(track, channel.getInstrumentCode(), channel.getChannelNumber()); // Скрипка на канале 1
            }
            float currentBeat = 0;
            List<ISound> sounds = channel.getAllSounds();
            int a = 0;
            for (ISound sound : sounds)
            {
                addNoteToTrack(track, currentBeat, sound.getTone() + IScale.BASE_NOTE, sound.getAccent(), sound.getDuration(), channel.getChannelNumber());
                currentBeat += sound.getDuration();
            }
        }
    }

    private static void setInstrument(Track track, int instrument, int channel) throws InvalidMidiDataException
    {
        ShortMessage programChange = new ShortMessage();
        programChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, instrument, 0);
        MidiEvent changeInstrument = new MidiEvent(programChange, 0);
        track.add(changeInstrument);
    }


    private void addNoteToTrack(Track track, float startBeat, int tone, int accent, float duration, int channel) throws InvalidMidiDataException
    {

        long startTick = (long) (startBeat * TICK_RESOLUTION); // Преобразование битов в тики
        long endTick = (long) ((startBeat + duration) * TICK_RESOLUTION);
//        duration *= initializer_.getMelodyPrimaryRhythmDetails().getBaseDurationMultiplier();

        accent = Math.min(accent, 127);
        tone = Math.min(tone, 127);

        System.out.println("Note ON: Tone=" + tone + " Accent=" + accent + " StartTick=" + startTick);
        ShortMessage onMessage = new ShortMessage();
        onMessage.setMessage(ShortMessage.NOTE_ON, channel, tone, accent);
        MidiEvent noteOn = new MidiEvent(onMessage, startTick);
        track.add(noteOn);

        System.out.println("Note OFF: Tone=" + tone + " EndTick=" + endTick);
        ShortMessage offMessage = new ShortMessage();
        offMessage.setMessage(ShortMessage.NOTE_OFF, channel, tone, 0);
        MidiEvent noteOff = new MidiEvent(offMessage, endTick);
        track.add(noteOff);
    }

    public static void convertMidiToWav(String midiFilePath, String wavFilePath) throws Exception
    {
        Sequence sequence = MidiSystem.getSequence(new File(midiFilePath));

        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();

        // Убедитесь, что у вас есть первый доступный Sequencer
        Sequencer sequencer = MidiSystem.getSequencer(false);
        sequencer.open();
        sequencer.setSequence(sequence);

        // Устанавливаем синтезатор как приемник для sequencer
        Transmitter transmitter = sequencer.getTransmitter();
        Receiver receiver = synthesizer.getReceiver();
        transmitter.setReceiver(receiver);

        // Настройка AudioFormat и DataLine.Info
        AudioFormat format = new AudioFormat(44100, 16, 3, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        // Используем PipedOutputStream и PipedInputStream для захвата аудио данных
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        AudioInputStream ais = new AudioInputStream(pis, format, AudioSystem.NOT_SPECIFIED);

        Thread midiThread = new Thread(() ->
        {
            try
            {
                sequencer.start();
                while (sequencer.isRunning())
                {
                    Thread.sleep(100);
                }
                sequencer.stop();
                sequencer.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        midiThread.start();

        // Пишем данные в PipedOutputStream
        Thread writerThread = new Thread(() ->
        {
            try
            {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = line.read(buffer, 0, buffer.length)) != -1)
                {
                    pos.write(buffer, 0, bytesRead);
                }
                pos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        writerThread.start();

        // Запись в WAV файл
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(wavFilePath));

        midiThread.join();
        writerThread.join();

        line.stop();
        line.close();
        synthesizer.close();
    }

    private void playMusic(Initializer initializer) throws InterruptedException
    {
        MusicGenerator musicGenerator = new MusicGenerator(initializer);
        music_ = musicGenerator.generate();
        music_.play();
    }

    private void stopMusic() throws InterruptedException
    {
        if (music_ != null)
        {
            music_.stop();
        }
    }
}
