package ivko.lana.neurotone.wave_generator.sounds.violin;

import ivko.lana.util.Pair;

/**
 * @author Lana Ivko
 */
public enum Chord
{
    Dm {@Override public Pair<Integer, Integer>[] getChangeFactors() {return (Pair<Integer, Integer>[]) new Pair[] {new Pair<>(9,8), new Pair<>(4,3), new Pair<>(5,3)};}},
    G7 {@Override public Pair<Integer, Integer>[] getChangeFactors() {return (Pair<Integer, Integer>[]) new Pair[] {new Pair<>(3,2), new Pair<>(15,8), new Pair<>(9,8), new Pair<>(4,3)};}},
    C {@Override public Pair<Integer, Integer>[] getChangeFactors() {return (Pair<Integer, Integer>[]) new Pair[] {new Pair<>(1,1), new Pair<>(5,4), new Pair<>(3,2)};}},
    Am {@Override public Pair<Integer, Integer>[] getChangeFactors() {return (Pair<Integer, Integer>[]) new Pair[] {new Pair<>(5,3), new Pair<>(1,1), new Pair<>(5,4)};}};

    public abstract Pair<Integer, Integer>[] getChangeFactors();
}
