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
        List<Integer> userID = new ArrayList<>();
        int count = 0, activeCount = 0;

        ps = connection.prepareStatement("select id from users");
        result = ps.executeQuery();
        while (result.next()) {
            userID.add(result.getInt("id"));
            count++;
        }

        if(count >= 1) {
            for (int i : userID) {
                long timestamp = System.currentTimeMillis() / 1000;

                ps = connection.prepareStatement("select latest_activity from users where id = ?");
                ps.setInt(1, i);
                result = ps.executeQuery();
                if(result.next()) {
                    if(timestamp - 600L < result.getLong("latest_activity")) {
                        activeCount++;
                    }
                }
            }
        }
        jda.getPresence().setActivity(Activity.playing("ActivePlayers: " + activeCount));
    }
}
