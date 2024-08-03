package ivko.lana.visualiser;

import javax.sound.midi.*;
import javax.sound.sampled.*;

public class MidiSynthToMp3
{
    private Synthesizer synthesizer;
    private AudioInputStream audioStream;

    public MidiSynthToMp3() throws MidiUnavailableException, LineUnavailableException
    {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        Receiver receiver = synthesizer.getReceiver();

        // Получение аудиоформата
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);

        audioStream = new AudioInputStream(line);
    }

    public void playMidi(MidiMessage message, long timeStamp) throws MidiUnavailableException
    {
        synthesizer.getReceiver().send(message, timeStamp);
    }

    public AudioInputStream getAudioStream()
    {
        return audioStream;
    }
}
