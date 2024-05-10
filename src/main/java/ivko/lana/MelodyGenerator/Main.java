package ivko.lana.MelodyGenerator;

/**
 * @author Lana Ivko
 */
public class Main
{
    public static void main(String[] args)
    {
        Initializer initializer = new Initializer();
        DynamicMelodyGeneratorWithTheme.start(initializer);
    }
}
