package ivko.lana.neurotone.util;

/**
 * @author Lana Ivko
 */

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter
{
    @Override
    public String format(LogRecord record)
    {
        // Форматируем строку как: Время - Уровень - Имя потока - Сообщение
        return String.format("%1$tF %1$tT %2$-7s %3$-20s %4$s%n",
                record.getMillis(),  // %1$ - время
                record.getLevel().getLocalizedName(),  // %2$ - уровень
                "[" + Thread.currentThread().getName() + "]",  // %3$ - имя потока в квадратных скобках
                record.getMessage());  // %4$ - сообщение
    }
}

