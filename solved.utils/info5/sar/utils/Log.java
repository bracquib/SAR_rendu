package info5.sar.utils;

public class Log {
    static boolean VERBOSE = true;

    /*
     * Set the verbosity of the logger.
     */
    public static void log(String s) {
        if (VERBOSE)
            System.out.println(s);
    }

    public static void log(Throwable th) {
        if (VERBOSE)
            th.printStackTrace();
    }

}
