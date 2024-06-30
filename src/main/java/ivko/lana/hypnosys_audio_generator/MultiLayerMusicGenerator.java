package ivko.lana.hypnosys_audio_generator;

/**
 * @author Lana Ivko
 */

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

import java.io.File;
import java.io.IOException;

public class MultiLayerMusicGenerator {

    public static void main(String[] args) {
        generateAndSaveMusic(60, "output.midi"); // Генерируем 60 секунд музыки
        convertMidiToWav("output.midi", "output.wav");
    }

    public static void generateAndSaveMusic(int durationSeconds, String outputFilePath) {
        Pattern pattern = generateMultiLayerMusicPattern(durationSeconds);
        saveMusicToFile(pattern, outputFilePath);
    }

    public static Pattern generateMultiLayerMusicPattern(int durationSeconds) {
        String[] notes = {"C", "D", "E", "F", "G", "A", "B"};

        // Генерация басовой партии
        StringBuilder bassString = new StringBuilder("I41 "); // 41 - Electric Bass (finger)
        for (int i = 0; i < durationSeconds * 2; i++) {
            String note = notes[(int) (Math.random() * notes.length)];
            int octave = 2 + (int) (Math.random() * 2); // Октавы от 2 до 3
            int duration = 1 + (int) (Math.random() * 4); // Длительность ноты от 1 до 4
            bassString.append(note).append(octave).append("/").append(duration).append(" ");
        }
        Pattern bassPattern = new Pattern(bassString.toString());

        // Генерация средней партии (например, фортепиано)
        StringBuilder midString = new StringBuilder("I0 "); // 0 - Acoustic Grand Piano
        for (int i = 0; i < durationSeconds * 2; i++) {
            String note = notes[(int) (Math.random() * notes.length)];
            int octave = 4 + (int) (Math.random() * 2); // Октавы от 4 до 5
            int duration = 1 + (int) (Math.random() * 4); // Длительность ноты от 1 до 4
            midString.append(note).append(octave).append("/").append(duration).append(" ");
        }
        Pattern midPattern = new Pattern(midString.toString());

        // Генерация высокочастотной партии (например, ударные)
        StringBuilder highString = new StringBuilder("I115 "); // 115 - Percussive Organ
        for (int i = 0; i < durationSeconds * 4; i++) {
            String note = notes[(int) (Math.random() * notes.length)];
            int octave = 6 + (int) (Math.random() * 2); // Октавы от 6 до 7
            int duration = 1; // Длительность ноты 1
            highString.append(note).append(octave).append("/").append(duration).append(" ");
        }
        Pattern highPattern = new Pattern(highString.toString());

        // Объединение всех партий
        return new Pattern(bassPattern).add(midPattern).add(highPattern);
    }

    public static void saveMusicToFile(Pattern pattern, String outputFilePath) {
        try {
            File midiFile = new File(outputFilePath);
            MidiFileManager.savePatternToMidi(pattern, midiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertMidiToWav(String midiFilePath, String wavFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "fluidsynth",
                    "-f", "D:\\install\\midi_sounds_library\\fluidsynth.cfg",
                    "-ni",
                    "D:\\install\\midi_sounds_library\\FluidR3_GM.sf2",
                    midiFilePath,
                    "-F",
                    wavFilePath
            );
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}





