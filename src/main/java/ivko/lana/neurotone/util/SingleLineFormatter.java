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
        return String.format("%1$s%n %2$s%n %3$tF %3$tT %4$-7s %5$-20s %6$s%n",
                record.getSourceClassName(),
                record.getSourceMethodName(),
                record.getMillis(),  // %1$ - время
                record.getLevel().getLocalizedName(),  // %2$ - уровень
                "[" + Thread.currentThread().getName() + "]",  // %3$ - имя потока в квадратных скобках
                record.getMessage());  // %4$ - сообщение
    }
}

