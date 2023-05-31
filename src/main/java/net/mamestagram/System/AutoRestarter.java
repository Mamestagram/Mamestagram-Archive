package net.mamestagram.System;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.mamestagram.Main.*;
import static net.mamestagram.System.SystemLogger.*;
import static net.mamestagram.System.JDABuilder.*;
import static net.mamestagram.Database.SQLConnector.*;

public class AutoRestarter {

    static int scheduleHour = 0;
    static boolean isFirstBoot = true;

    public static void executeRestart() throws InterruptedException, SQLException {

        var date = DateTimeFormatter.ofPattern("HH");
        if(isFirstBoot) {
            scheduleHour = Integer.parseInt(date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo")))) + 1;
            if(scheduleHour >= 24) scheduleHour -= 24;
            isFirstBoot = false;
            return;
        }

        int nowHour = Integer.parseInt(date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
        if(nowHour == scheduleHour) {
            setLogger("System will be restart", 0);
            jda.shutdownNow();
            connection.close();
            System.gc();
            Thread.sleep(5000);
            jda = createJDA(TOKEN);
            connection = connectToServer();
            scheduleHour = Integer.parseInt(date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo")))) + 1;
            if(scheduleHour >= 24) scheduleHour -= 24;
            setLogger("System has restarted", 0);
        }
    }
}

