package net.mamestagram.debug;

public class DebugModule {

    static long startTime;
    static long endTime;

    public static void startDebuggingProcess(String processName) {

        startTime = System.currentTimeMillis();
        System.out.println("[" + processName + "] Process has been started!");
    }

    public static void endDebuggingProcess(String processName) {

        endTime = System.currentTimeMillis();
        System.out.println("[" + processName + "] " + (endTime - startTime) + "ms");
    }

}
