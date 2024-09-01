package ivko.lana.neurotone.wave_generator;

import java.util.*;

/**
 * @author Lana Ivko
 */
public class TriadSequenceGenerator
{
    private Map<Triad, List<TriadSequence>> sequencesByStartAccord_;
    private Map<Triad, List<TriadSequence>> lastSequencesByStartAccord_;
    private Map<Triad, List<Triad>> endStartNoteSequenceConnections_;
    private Random random_;

    public TriadSequenceGenerator()
    {
        random_ = new Random();
        init();
    }

    public TriadSequence generateNext(TriadSequence previous)
    {
        return generateNextImpl(sequencesByStartAccord_, previous);
    }

    public TriadSequence generateLast(TriadSequence previous)
    {
        return generateNextImpl(lastSequencesByStartAccord_, previous);
    }

    public TriadSequence generateNextImpl(Map<Triad, List<TriadSequence>> triadSequence, TriadSequence previous)
    {
        Triad startTriad;
        List<Triad> possibleStartTriads;
        int randomIndex;
        if (previous == null)
        {
            randomIndex = random_.nextInt(endStartNoteSequenceConnections_.size());
            possibleStartTriads = new ArrayList<>(endStartNoteSequenceConnections_.keySet());
            startTriad = possibleStartTriads.get(randomIndex);
        }
        else
        {
            Triad lastTriad = previous.sequences_[previous.sequences_.length - 1];
            possibleStartTriads = endStartNoteSequenceConnections_.get(lastTriad);
            randomIndex = random_.nextInt(possibleStartTriads.size());
            startTriad = possibleStartTriads.get(randomIndex);
        }
        List<TriadSequence> triadSequences = triadSequence.get(startTriad);
        randomIndex = random_.nextInt(triadSequences.size());
        return triadSequences.get(randomIndex);
    }

    private void init()
    {
        initTriadSequence();
        initLastTriadSequence();
        initEndStartNoteSequenceConnection();
    }

    private void initEndStartNoteSequenceConnection()
    {
        endStartNoteSequenceConnections_ = new HashMap<>();
        endStartNoteSequenceConnections_.put(Triad.TONIC, new ArrayList<>(sequencesByStartAccord_.keySet()));
        endStartNoteSequenceConnections_.put(Triad.SUB_DOMINANT, new ArrayList<>(Arrays.asList(Triad.DOMINANT, Triad.SUB_DOMINANT)));
    }

    private void initLastTriadSequence()
    {
        lastSequencesByStartAccord_ = new HashMap<>();
        addSequence(lastSequencesByStartAccord_, new TriadSequence(Triad.DOMINANT, Triad.TONIC));
        addSequence(lastSequencesByStartAccord_, new TriadSequence(Triad.SUB_DOMINANT, Triad.DOMINANT, Triad.TONIC));
        addSequence(lastSequencesByStartAccord_, new TriadSequence(Triad.SUPER_TONIC, Triad.DOMINANT, Triad.TONIC));
        addSequence(lastSequencesByStartAccord_, new TriadSequence(Triad.SUB_MEDIANT, Triad.DOMINANT, Triad.TONIC));
        addSequence(lastSequencesByStartAccord_, new TriadSequence(Triad.DIMINISHED_CHORD, Triad.DOMINANT, Triad.TONIC));
    }

    private void initTriadSequence()
    {
        sequencesByStartAccord_ = new HashMap<>();
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.TONIC, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.TONIC, Triad.SUB_DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.TONIC, Triad.SUB_DOMINANT, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUB_DOMINANT, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUB_DOMINANT, Triad.SUB_DOMINANT));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.TONIC, Triad.SUPER_TONIC, Triad.SUB_DOMINANT));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUPER_TONIC, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUB_MEDIANT, Triad.SUB_DOMINANT, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.DIMINISHED_CHORD, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUB_MEDIANT, Triad.SUPER_TONIC, Triad.DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUPER_TONIC, Triad.SUB_DOMINANT, Triad.TONIC));
        addSequence(sequencesByStartAccord_, new TriadSequence(Triad.SUB_MEDIANT, Triad.DOMINANT, Triad.TONIC));
    }

    private void addSequence(Map<Triad, List<TriadSequence>> dest, TriadSequence sequence)
    {
        List<TriadSequence> triadSequences = dest.computeIfAbsent(sequence.sequences_[0], k -> new ArrayList<>());
        triadSequences.add(sequence);
    }

    public static class TriadSequence
    {
        private final Triad[] sequences_;

        TriadSequence(Triad... sequences)
        {
            sequences_ = sequences;
        }

        public int getSize()
        {
            return sequences_.length;
        }

        public Triad[] getSequences()
        {
            return sequences_;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof TriadSequence)) return false;
            TriadSequence that = (TriadSequence) o;
            return Arrays.equals(sequences_, that.sequences_);
        }

        @Override
        public String toString()
        {
            return "TriadSequence{" +
                    "sequences_=" + Arrays.toString(sequences_) +
                    '}';
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(sequences_);
        }
    }
}
