package net.mamestagram.database;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;

public class StatusAdjustment extends ListenerAdapter {

    private static PreparedStatement ps;
    private static ResultSet result;

    public static void checkMapStatus() throws SQLException {

        ArrayList<Integer> beatmapID = new ArrayList<>();
        ArrayList<Integer> beatmapStatus = new ArrayList<>();
        int arrayCount = 0;

        ps = connection.prepareStatement("select set_id, status from customstats_map");
        result = ps.executeQuery();

        while(result.next()) {
            beatmapID.add(result.getInt("set_id"));
            beatmapStatus.add(result.getInt("status"));
        }

        for(int i : beatmapID) {

            ps = connection.prepareStatement("select status from maps where set_id = ?");
            ps.setInt(1, i);
            result = ps.executeQuery();

            if(result.next()) {
                if(result.getInt("status") != beatmapStatus.get(arrayCount)) {
                    ps = connection.prepareStatement("UPDATE maps SET status = ? WHERE set_id = ?");
                    ps.setInt(1, beatmapStatus.get(arrayCount));
                    ps.setInt(2, i);
                    ps.executeUpdate();
                }
            }

            arrayCount++;
        }
    }
}
