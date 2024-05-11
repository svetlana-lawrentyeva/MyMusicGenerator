package ivko.lana.generators;

import ivko.lana.musicentities.Music;

/**
 * @author Lana Ivko
 */
public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        Initializer initializer = new Initializer();
        MusicGenerator musicGenerator = new MusicGenerator(initializer);
        Music music = musicGenerator.generate();
        music.play();


//        DynamicMelodyGeneratorWithTheme.start(initializer);
    }
}
