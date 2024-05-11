package ivko.lana.yaml;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

/**
 * @author Lana Ivko
 */
public class YamlToJava
{
    public static <T> T extract(String path, Class<T> type) {
        Yaml yaml = new Yaml(new Constructor(type));
        try (InputStream inputStream = YamlToJava.class.getClassLoader().getResourceAsStream(path)) {
            return yaml.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
