package ivko.lana.frequencycombination;

import ivko.lana.yaml.FrequencyCombinationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lana Ivko
 */
public class FrequencyCombinationGenerator
{
    public static void main(String[] args)
    {

        List<String> frequencyCombinations = getFrequencyCombinations();

        Collections.shuffle(frequencyCombinations);

        Path filePath = Paths.get("output.txt");

        try
        {
            Files.write(filePath, frequencyCombinations);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getFrequencyCombinations()
    {
        FrequencyManager frequencyManager = FrequencyCombinationLoader.load(FrequencyCombinationLoader.FrequencyType.FREQUENCY);
        FrequencyManager binauralManager = FrequencyCombinationLoader.load(FrequencyCombinationLoader.FrequencyType.BINAURAL);

        List<Frequency> frequencies = frequencyManager.getFrequencies();
        List<Frequency> binaurals = binauralManager.getFrequencies();

        String pattern = "%s герц мелодия и %s герц бинауральный ритм: %s и %s";
        List<String> result = new ArrayList<>();

        for (Frequency frequency : frequencies)
        {
            for (Frequency binaural : binaurals)
            {
                result.add(String.format(pattern, frequency.getValue(), binaural.getValue(), frequency.getDescription(), binaural.getDescription()));
            }
        } return result;
    }
}
