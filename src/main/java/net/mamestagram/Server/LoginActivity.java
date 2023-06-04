package net.mamestagram.Server;

import net.dv8tion.jda.api.entities.Activity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;

public class LoginActivity {

    public static void updateLoginStatus() throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        int count = 0;

        if(isRestarting) {
            return;
        }

        ps = connection.prepareStatement("select latest_activity from users");
        result = ps.executeQuery();
        while (result.next()) {
            if((System.currentTimeMillis() / 1000) - 600L < result.getLong("latest_activity")) {
                count++;
            }
        }

        jda.getPresence().setActivity(Activity.playing("ActivePlayers: " + count));
    }
}
