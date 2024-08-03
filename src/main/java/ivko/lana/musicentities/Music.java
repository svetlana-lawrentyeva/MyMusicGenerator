package ivko.lana.musicentities;

import ivko.lana.converter.MidiToAudioConverter;
import ivko.lana.entities.IScale;
import ivko.lana.generators.Initializer;
import ivko.lana.util.MusicUtil;
import ivko.lana.visualiser.VisualPanel;
import ivko.lana.yaml.RhythmDetails;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lana Ivko
 */
public class Music implements IPlayable, IMusic
{
    private List<Channel> channels_;
    private Metronom metronom_;
    private Initializer initializer_;

    public Music(List<Channel> channels, Initializer initializer)
    {
        channels_ = channels;
        initializer_ = initializer;
    }

    public List<Channel> getChannels()
    {
        return channels_;
    }

    public void addChannel(Channel channel)
    {
        channels_.add(channel);
    }

    @Override
    public int getChannelNumber()
    {
        return -1;
    }

    public void save(String fileName)
    {
        try
        {
            Sequence sequence = new Sequence(Sequence.PPQ, VisualPanel.TICK_RESOLUTION);
            Track track = sequence.createTrack();

            track.add(new MidiEvent(createTempoMessage(initializer_), 0));

            writeToTrack(this, track);
            File midiFile = new File(fileName + ".mid");
            MidiSystem.write(sequence, 1, midiFile);

            MidiToAudioConverter.convertWithTimidity(fileName + ".mid", fileName + ".wav");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
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

        long startTick = (long) (startBeat * VisualPanel.TICK_RESOLUTION); // Преобразование битов в тики
        long endTick = (long) ((startBeat + duration) * VisualPanel.TICK_RESOLUTION);
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

    private MetaMessage createTempoMessage(Initializer initializer) throws InvalidMidiDataException
    {
        RhythmDetails rhythmDetails = initializer.getMelodyPrimaryRhythmDetails();
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

    public void play() throws InterruptedException
    {
        try
        {
//            LOGGER.info(String.format("%s '%s' is playing", this.getClass().getSimpleName(), this.hashCode()));
            List<Thread> threads = new ArrayList<>();
            metronom_ = new Metronom(channels_.size());
            for (Channel channel : channels_)
            {
                Thread thread = new Thread(() ->
                {
                    try
                    {
                        channel.play(metronom_);
                    }
                    catch (InterruptedException | MidiUnavailableException e)
                    {
                        throw new RuntimeException(e);
                    }
                });
                thread.start(); // Запускаем поток
                threads.add(thread); // Добавляем поток в список
            }
            // Ожидание завершения всех потоков
            for (Thread thread : threads)
            {
                thread.join(); // Ожидаем завершения потока
            }
            Synthesizer synthesizer = MusicUtil.getInstance().getSynthesizer();
            synthesizer.close();
            MusicUtil.getInstance().resetFreeCanalNumbers();
        }
        catch (Throwable t)
        {
            LOGGER.severe(t.getLocalizedMessage());
            throw t;
        }
    }

    public void stop() throws InterruptedException
    {
        metronom_.stop();
        metronom_.await();
        for (Channel channel : channels_)
        {
            channel.stop();
        }
    }

    @Override
    public void play(MidiChannel channel, Metronom metronom) throws InterruptedException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IPlayable> getPlayables()
    {
        return channels_.stream()
                .map(channel -> (IPlayable) channel)
                .collect(Collectors.toList());
    }

    public List<Integer> getAllNotes()
    {
        Channel melodyChannel = channels_.stream()
                .filter(channel -> channel.isMelody())
                .findFirst()
                .orElse(null);
        List<Integer> result;
        if (melodyChannel != null)
        {
            result = melodyChannel.getAllNotes();
        }
        else
        {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    public List<ISound> getAllSounds()
    {
        List<ISound> sounds = new ArrayList<>();
        for (Channel channel : channels_)
        {
            sounds.addAll(channel.getAllSounds());
        }
        return sounds;
    }
}
