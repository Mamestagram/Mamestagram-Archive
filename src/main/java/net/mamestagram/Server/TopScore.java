package net.mamestagram.Server;

public class TopScore {

    static boolean isFirstBoot = true;

    public static void updateTopScore() {

        if(isFirstBoot) {
            isFirstBoot = false;
            return;
        } else {
            //TODO
        }
    }
}