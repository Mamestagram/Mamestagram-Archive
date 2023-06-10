package net.mamestagram.System;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.mamestagram.Main.*;
import static net.mamestagram.System.SystemLogger.*;

public class CacheRemover {

    static int scheduleHour = 0;
    static boolean isFirstBoot = true;

    public static void executeRestart() throws InterruptedException, SQLException {

        final long guildID = 944248031136587796L;
        final long channelID = 1081737936401350717L;

        var date = DateTimeFormatter.ofPattern("HH");
        if(isFirstBoot) {
            scheduleHour = Integer.parseInt(date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo")))) + 1;
            if(scheduleHour >= 24) scheduleHour -= 24;
            isFirstBoot = false;
            return;
        }

        int nowHour = Integer.parseInt(date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
        if(nowHour == scheduleHour) {
            setLogger("System's cache will be removed", 0);
            isRestarting = true;
            System.gc();
            scheduleHour = Integer.parseInt(date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo")))) + 1;
            if(scheduleHour >= 24) scheduleHour -= 24;
            isRestarting = false;
            setLogger("System's cache has been removed", 0);
        }
    }
}

