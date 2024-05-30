package ivko.lana.util;

/**
 * @author Lana Ivko
 */
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiSaver {

    public static void saveMidiFile(Sequence sequence, String filePath) throws IOException {
        File midiFile = new File(filePath);
        MidiSystem.write(sequence, 1, midiFile);
    }

    public static void main(String[] args) {
        try {
            // Создайте новое MIDI последовательность
            Sequence sequence = new Sequence(Sequence.PPQ, 24);

            // Добавьте трек к последовательности
            Track track = sequence.createTrack();

            // Добавьте MIDI события к треку (например, ноты)
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93); // нота C4
            track.add(new MidiEvent(msg, 0));
            msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
            track.add(new MidiEvent(msg, 24));

            // Сохраните последовательность в MIDI файл
            String midiFilePath = "output.mid";
            saveMidiFile(sequence, midiFilePath);

            // Попробуйте воспроизвести сохраненный MIDI файл
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.setSequence(MidiSystem.getSequence(new File(midiFilePath)));
            sequencer.open();
            sequencer.start();

            // Дождитесь завершения воспроизведения
            while (sequencer.isRunning()) {
                Thread.sleep(1000);
            }
            sequencer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

