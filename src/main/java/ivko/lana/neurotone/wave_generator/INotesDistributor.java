package ivko.lana.neurotone.wave_generator;

/**
 * @author Lana Ivko
 */
public interface INotesDistributor
{
    double[][] getLastNotes();

    double[][] getNotes();
    double getFrequency(int degree);
}
