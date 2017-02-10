package tools;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    /**
     * @param m    Message to send
     * @param args Format options
     */
    public static void print(String m, Object... args) {
        String x = String.format(m, args);
        System.out.print(x);
    }

    public static void println(Object m, Object... args) {
        println(Error.INFO, m.toString(), args);
    }

    public static void println(Error e, String m, Object... args) {
        m = String.format(m, args);

        if (e == Error.FATAL)
            LogErrorToFile(m);

        System.out.println(m);
    }

    private static void LogErrorToFile(String m) {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");

        String path = "logs/" + dateFormat.format(today) + ".txt";

        try {
            FileWriter fw = new FileWriter(path, true);
            fw.write(timeFormat.format(today) + " : " + m + "\n");
            fw.close();
        } catch (Exception e) {
            Logger.println("Error logging to file: " + e);
        }
    }
}