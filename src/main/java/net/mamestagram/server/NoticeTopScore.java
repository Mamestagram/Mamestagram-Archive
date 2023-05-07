package net.mamestagram.server;

import java.sql.SQLException;

public class NoticeTopScore {

    static boolean isFirstBoot = true;

    public static void updateTopScore() {

        if(isFirstBoot) {
            isFirstBoot = false;
            return;
        } else {
            
        }
    }
}