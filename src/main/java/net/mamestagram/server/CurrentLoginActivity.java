package net.mamestagram.server;

import net.dv8tion.jda.api.entities.Activity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.mamestagram.Main.*;

public class CurrentLoginActivity {

    public static void updateLoginStatus() throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        int count = 0;

        ps = connection.prepareStatement("select latest_activity from users");
        result = ps.executeQuery();
        while (result.next()) {
            if((System.currentTimeMillis() / 1000) - 120L < result.getLong("latest_activity")) {
                count++;
            }
        }

        jda.getPresence().setActivity(Activity.playing("ActivePlayers: " + count));
    }
}
