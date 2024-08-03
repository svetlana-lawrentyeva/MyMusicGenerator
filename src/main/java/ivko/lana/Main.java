package ivko.lana;

import ivko.lana.visualiser.VisualFrame;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lana Ivko
 */
public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        VisualFrame frame = new VisualFrame();
        frame.setVisible(true);

//        String directoryPath = "D:\\VIDEO\\зая умный"; // Replace with your directory path
//        String directoryPath = "D:\\VIDEO\\TEST"; // Rep lace with your directory path
//        renameFiles(directoryPath);


//        DynamicMelodyGeneratorWithTheme.start(initializer);
    }

    public static void renameFiles(String directoryPath)
    {
        File dir = new File(directoryPath);
        if (!dir.isDirectory())
        {
            System.out.println("Provided path is not a directory");
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0)
        {
            System.out.println("No files found in the directory");

            // FILE   20231203-133058-000824

            return;
        }

        for (File file : files)
        {
            if (file.isFile())
            {
                String oldName = file.getName();
                String newName = convertFileName(oldName);
                if (newName != null)
                {
                    File newFile = new File(dir, newName);
                    if (file.renameTo(newFile))
                    {
                        System.out.println("Renamed: " + oldName + " to " + newName);
                    }
                    else
                    {
                        System.out.println("Failed to rename: " + oldName);
                    }
                }
            }
        }
    }

    private static String convertFileName(String oldName)
    {
        // Example filename: FILE20231203-133058-000824.MP4
        // Extract date and time parts
        if (!oldName.matches("PARK\\d{8}-\\d{6}-\\d{6}\\.\\w+"))
        {
            System.out.println("Filename does not match expected format: " + oldName);
            return null;
        }

        try
        {
            String datePart = oldName.substring(4, 12); // 20231203
            String timePart = oldName.substring(13, 19); // 133058
            String extension = oldName.substring(oldName.lastIndexOf(".")); // .MP4

            // Parse date and time parts
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = dateFormat.parse(datePart);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
            Date time = timeFormat.parse(timePart);

            // Format new date and time strings
            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String newDatePart = newDateFormat.format(date);

            SimpleDateFormat newTimeFormat = new SimpleDateFormat("HH-mm");
            String newTimePart = newTimeFormat.format(time);

            // Construct new filename
            return "ЗАЯ (parking) " + newDatePart + " " + newTimePart + extension;
        }
        catch (ParseException e)
        {
            System.out.println("Error parsing date/time in filename: " + oldName);
            return null;
        }
    }

}
