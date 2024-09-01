package ivko.lana.neurotone.processing;

import ivko.lana.neurotone.StereoPlayer;
import ivko.lana.neurotone.StereoSaver;
import ivko.lana.neurotone.audio_generator.AudioPlayer;
import ivko.lana.neurotone.audio_generator.AudioSaver;
import ivko.lana.neurotone.util.Util;
import ivko.lana.neurotone.video_generator.VideoPlayer;
import ivko.lana.neurotone.video_generator.VideoSaver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lana Ivko
 */
public enum GeneratorType
{
    VIDEO
    {
        @Override public List<StereoPlayer> getSavers() {{return List.of(new VideoSaver());}}
        @Override public List<StereoPlayer> getPlayers() {return List.of(new VideoPlayer());}
    },
    AUDIO
    {
        @Override public List<StereoPlayer> getSavers() {{return List.of(new AudioSaver());}}
        @Override public List<StereoPlayer> getPlayers() {return List.of(new AudioPlayer());}
    };

    public abstract List<StereoPlayer> getSavers();
    public abstract List<StereoPlayer> getPlayers();
}
