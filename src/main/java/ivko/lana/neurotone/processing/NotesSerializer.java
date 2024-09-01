package ivko.lana.neurotone.processing;

import ivko.lana.neurotone.util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Lana Ivko
 */
public class NotesSerializer
{
    private static final String FILE_NAME = "notes.txt";
    private static final int BLOCK_SIZE = 100; // Количество блоков для предварительной загрузки
    private static final int BATCH_SIZE = 20;  // Размер партии для возврата по запросу
    private BufferedReader reader_;
    private List<double[][]> currentBlocks_;
    private List<double[][]> nextBlocks_;
    private int currentIndex_;
    private ExecutorService executorService_;
    private Future<?> prefetchFuture_;

    public static void initialize()
    {
        Util.deleteFileIfExists(FILE_NAME);
    }

    private NotesSerializer()
    {
    }

    public static NotesSerializer getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    private void prefetchBlocks(List<double[][]> blocks) throws IOException
    {
        blocks.clear();
        String line;
        List<double[]> currentGroup = null;
        int count = 0;

        while ((line = reader_.readLine()) != null && count < BLOCK_SIZE)
        {
            line = line.trim();
            if (line.equals("{"))
            {
                currentGroup = new ArrayList<>();
            }
            else if (line.equals("}"))
            {
                if (currentGroup != null)
                {
                    blocks.add(currentGroup.toArray(new double[0][]));
                    count++;
                }
                currentGroup = null;
            }
            else if (line.startsWith("{") && line.endsWith("},"))
            {
                String[] values = line.substring(1, line.length() - 2).split(",\\s*");
                double[] row = new double[values.length];
                for (int i = 0; i < values.length; i++)
                {
                    row[i] = Double.parseDouble(values[i]);
                }
                if (currentGroup != null)
                {
                    currentGroup.add(row);
                }
            }
        }
    }

    private void prefetchNextBlocks()
    {
        prefetchFuture_ = executorService_.submit(() ->
        {
            try
            {
                prefetchBlocks(nextBlocks_);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    private void prepare()
    {
        try
        {
            reader_ = new BufferedReader(new FileReader(FILE_NAME));
            currentBlocks_ = new ArrayList<>();
            nextBlocks_ = new ArrayList<>();
            currentIndex_ = 0;
            executorService_ = Executors.newSingleThreadExecutor();
            prefetchBlocks(currentBlocks_); // Загружаем первые блоки
            prefetchNextBlocks(); // Начинаем загружать следующие блоки
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<double[][]> getNext()
    {
        if (reader_ == null)
        {
            prepare();
        }
        if (currentIndex_ >= currentBlocks_.size())
        {
            if (nextBlocks_.isEmpty())
            {
                return null; // Возвращаем null, если больше нет блоков
            }
            try
            {
                prefetchFuture_.get(); // Ждем завершения предзагрузки
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            currentBlocks_ = nextBlocks_; // Переключаемся на предзагруженные блоки
            nextBlocks_ = new ArrayList<>();
            currentIndex_ = 0;
            prefetchNextBlocks(); // Начинаем загружать следующие блоки
        }

        List<double[][]> batch = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE && currentIndex_ < currentBlocks_.size(); i++, currentIndex_++)
        {
            batch.add(currentBlocks_.get(currentIndex_));
        }
        return batch;
    }

    public void close()
    {
        executorService_.shutdown();
        try
        {
            reader_.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void serializeToCSV(double[][] notes) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true)))
        {
            // Начало группы
            writer.write("{");
            writer.newLine();

            for (double[] row : notes)
            {
                StringBuilder line = new StringBuilder();
                line.append("    {");
                for (int i = 0; i < row.length; i++)
                {
                    line.append(row[i]);
                    if (i < row.length - 1)
                    {
                        line.append(", ");
                    }
                }
                line.append("},");
                writer.write(line.toString());
                writer.newLine();
            }

            // Конец группы
            writer.write("}");
            writer.newLine();
        }
    }

    private static class InstanceHolder
    {
        private static final NotesSerializer INSTANCE = new NotesSerializer();
    }

}
