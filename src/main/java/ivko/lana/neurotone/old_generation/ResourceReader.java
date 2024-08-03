package ivko.lana.neurotone.old_generation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lana Ivko
 */
public class ResourceReader
{
    private Map<ResourceType, String> resourceByType_ = new HashMap<>();

    private ResourceReader()
    {
    }

    public String get(ResourceType resourceType)
    {
        String resource = resourceByType_.get(resourceType);
        if (resource == null)
        {
            resource = read(resourceType);
            resourceByType_.put(resourceType, resource);
        }
        return resource;
    }

    private String read(ResourceType resourceType)
    {
        try (InputStream inputStream = ResourceReader.class.getResourceAsStream(resourceType.getPath()))
        {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    content.append(line).append(System.lineSeparator());
                }
                return content.toString();

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static ResourceReader getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder
    {
        private static final ResourceReader INSTANCE = new ResourceReader();
    }

    public enum ResourceType
    {
        COMMON_DESCRIPTION("/commonDescription.txt"),
        TAGS("/tags.txt");

        private ResourceType(String path)
        {
            path_ = path;
        }

        private String path_;

        public String getPath()
        {
            return path_;
        }
    }
}
