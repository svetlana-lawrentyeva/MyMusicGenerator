package ivko.lana.util;

import ivko.lana.generators.HertzChannelGenerator;
import ivko.lana.generators.Initializer;
import ivko.lana.musicentities.Channel;

import javax.sound.midi.*;

public class PitchBendCalculator
{
    private static final int MAX_PITCH_BEND_RANGE = 8192;
    private static final double REFERENCE_FREQUENCY = 440.0; // A4
    private static final int REFERENCE_NOTE = 69; // MIDI number for A4

    public static void main(String[] args) throws Exception
    {
        // Примеры использования
//        double[] frequencies = {285, 396, 417, 528, 639, 741, 852, 963};
        double[] frequencies = {285};

        // Пример настройки диапазона pitch bend и воспроизведения ноты
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        Channel channel = new HertzChannelGenerator(new Initializer()).generate();
        MidiChannel midiChannel = channel.getChannel();
        setPitchBendRange(channel, 1, 0);
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        int instrumentCode = 51;
        synthesizer.loadInstrument(instruments[instrumentCode]);
        midiChannel.programChange(instrumentCode);

        for (double frequency : frequencies)
        {
            int midiNote = findClosestMidiNote(frequency);
            int pitchBendValue = calculatePitchBendValue(frequency);
            double actualFrequency = calculateActualFrequency(frequency, pitchBendValue);
            System.out.println(String.format("Target Frequency: %s Hz, Pitch Bend Value: %s, Actual Frequency: %s",
                    frequency, pitchBendValue, actualFrequency));
            midiChannel.setPitchBend(pitchBendValue + 8192);
            midiChannel.noteOn(midiNote, 600); // Используйте правильный MIDI номер
            int duration = 6000;
            Thread.sleep(duration);
            midiChannel.noteOff(midiNote);
        }

        // Установить диапазон pitch bend в полтона

        synthesizer.close();
    }

    public static int calculatePitchBendValue1(double targetFrequency)
    {
        // Найти ближайшую ноту к целевой частоте
        int midiNote = findClosestMidiNote(targetFrequency);

        // Рассчитать частоту этой ноты
        double midiNoteFrequency = REFERENCE_FREQUENCY * Math.pow(2, (midiNote - REFERENCE_NOTE) / 12.0);

        // Рассчитать соотношение частот
        double bendRatio = targetFrequency / midiNoteFrequency;

        // Рассчитать значение pitch bend
        int pitchBendValue = (int) ((bendRatio - 1) * MAX_PITCH_BEND_RANGE);

        // Ограничить значение диапазоном [-MAX_PITCH_BEND_RANGE, MAX_PITCH_BEND_RANGE]
        pitchBendValue = Math.max(-MAX_PITCH_BEND_RANGE, Math.min(MAX_PITCH_BEND_RANGE, pitchBendValue));

        return pitchBendValue;
    }

    public static int calculatePitchBendValue(double targetFrequency)
    {
        int midiNote = findClosestMidiNote(targetFrequency);
        double midiNoteFrequency = REFERENCE_FREQUENCY * Math.pow(2, (midiNote - REFERENCE_NOTE) / 12.0);
        double bendRatio = targetFrequency / midiNoteFrequency;
        double pitchBendValueFloat = (bendRatio - 1) * MAX_PITCH_BEND_RANGE;
        int pitchBendValue = (int) Math.round(pitchBendValueFloat);
        pitchBendValue = Math.max(-MAX_PITCH_BEND_RANGE, Math.min(MAX_PITCH_BEND_RANGE, pitchBendValue));
        return pitchBendValue;
    }

    public static int findClosestMidiNote(double frequency)
    {
        // Вычислить MIDI-номер, который наиболее близко соответствует заданной частоте
        return (int) Math.round(REFERENCE_NOTE + 12 * Math.log(frequency / REFERENCE_FREQUENCY) / Math.log(2));
    }

    public static void setPitchBendRange(Channel channel, int semitones, int cents) throws InvalidMidiDataException
    {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel.getChannelNumber(), 101, 0); // RPN MSB
        channel.getChannel().controlChange(101, 0);
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel.getChannelNumber(), 100, 0); // RPN LSB
        channel.getChannel().controlChange(100, 0);
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel.getChannelNumber(), 6, semitones); // Data Entry MSB
        channel.getChannel().controlChange(6, semitones);
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel.getChannelNumber(), 38, cents); // Data Entry LSB
        channel.getChannel().controlChange(38, cents);
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel.getChannelNumber(), 101, 127); // RPN NULL
        channel.getChannel().controlChange(101, 127);
        message.setMessage(ShortMessage.CONTROL_CHANGE, channel.getChannelNumber(), 100, 127); // RPN NULL
        channel.getChannel().controlChange(100, 127);
    }

    public static double calculateActualFrequency(double targetFrequency, int pitchBendValue)
    {
        int midiNote = findClosestMidiNote(targetFrequency);
        double midiNoteFrequency = REFERENCE_FREQUENCY * Math.pow(2, (midiNote - REFERENCE_NOTE) / 12.0);
        double pitchBendRatio = 1.0 + (double) pitchBendValue / MAX_PITCH_BEND_RANGE;
        return midiNoteFrequency * pitchBendRatio;
    }

}
