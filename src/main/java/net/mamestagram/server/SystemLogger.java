package net.mamestagram.server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SystemLogger {

    public static void setLogger(String msg, int level) {

        var date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        switch (level) {
            case 0 -> System.out.println("[Normal: " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))) + "] " + msg);
            case 1 -> System.out.println("[Warning: " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))) + "] " + msg);
            default -> System.out.println("[Error: " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))) + "] " + msg);
        }
    }
}
