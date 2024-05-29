package ivko.lana.musicentities;

/**
 * @author Lana Ivko
 */
public enum RhythmType
{
    START (0.0, new int[] {0, 5, 7}, new int[] {3, 4}),
    BUILD (0.3, new int[] {1, 2, 3, 4, 8, 9}, new int[] {5, 7}),
    PEAK (0.1, new int[] {6, 11}, new int[] {12, 9}),
    END (0.8, new int[] {}, new int[] {0, 5, 7});

    private RhythmType(double pauseProbability, int[] stepsForQuestion, int[] stepsForAnswer)
    {
        pauseProbability_ = pauseProbability;
        stepsForQuestion_ = stepsForQuestion;
        stepsForAnswer_ = stepsForAnswer;
    }

    public double getPauseProbability()
    {
        return pauseProbability_;
    }

    public int[] getSteps(PhraseType phraseType)
    {
        return phraseType == PhraseType.QUESTION ? stepsForQuestion_ : stepsForAnswer_;
    }

    private double pauseProbability_;
    private int[] stepsForQuestion_;
    private int[] stepsForAnswer_;
}
