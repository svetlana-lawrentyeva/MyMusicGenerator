package ivko.lana.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class YamlToJava
{
    public static <T> T extract(String path, Class<T> type)
    {
        // Создаем LoaderOptions для настройки загрузки
        LoaderOptions loaderOptions = new LoaderOptions();

        // Создаем экземпляр Constructor с классом type и LoaderOptions
        Constructor constructor = new Constructor(type, loaderOptions);

        // Создаем экземпляр Yaml с помощью конструктора
        Yaml yaml = new Yaml(constructor);

        try (InputStream inputStream = YamlToJava.class.getClassLoader().getResourceAsStream(path))
        {
            if (inputStream == null)
            {
                System.err.println("Файл не найден: " + path);
                return null;
            }
            return yaml.load(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

