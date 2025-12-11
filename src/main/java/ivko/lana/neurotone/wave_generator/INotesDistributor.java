package ivko.lana.neurotone.wave_generator;

/**
 * @author Lana Ivko
 */
public interface INotesDistributor
{
    double[][] getLastNotes();

    double[][] getNotes();
    double[][] getNotesForLeftChannel();
    double[][] getNotesForRightChannel();
    double getFrequency(int degree);
    String generateFileName();
}
