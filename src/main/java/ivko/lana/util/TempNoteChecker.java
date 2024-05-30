package ivko.lana.util;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Lana Ivko
 */
public class TempNoteChecker
{
    private static final int TICK_RESOLUTION = 24; // 24 ticks per quarter note

    public void save(String fileName, int tempoInBPM) {
        try {
            // Создайте новое MIDI последовательность
            Sequence sequence = new Sequence(Sequence.PPQ, TICK_RESOLUTION);

            // Добавьте трек к последовательности
            Track track = sequence.createTrack();

            // Установите темп
            int tempoInMPQ = 60000000 / tempoInBPM;
            MetaMessage tempoMessage = new MetaMessage();
            byte[] tempoBytes = {
                    (byte) (tempoInMPQ >> 16),
                    (byte) (tempoInMPQ >> 8),
                    (byte) tempoInMPQ
            };
            tempoMessage.setMessage(0x51, tempoBytes, tempoBytes.length);
            track.add(new MidiEvent(tempoMessage, 0));

            // Пример добавления MIDI событий к треку (например, ноты)
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93); // нота C4
            track.add(new MidiEvent(msg, 0));
            msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
            track.add(new MidiEvent(msg, 24));

            // Сохраните последовательность в MIDI файл
            File midiFile = new File(fileName + ".mid");
            MidiSystem.write(sequence, 1, midiFile);

            System.out.println("MIDI file saved successfully: " + midiFile.getAbsolutePath());

        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TempNoteChecker checker = new TempNoteChecker();
        int tempoInBPM = 75; // Установите ваш темп здесь
        checker.save(MusicUtil.generateNewName() + "output", tempoInBPM);
    }
}
