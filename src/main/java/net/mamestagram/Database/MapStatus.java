package net.mamestagram.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.System.SystemLogger.setLogger;

//処理計量版MapStatus

public class MapStatus {

    public static void updateMapStatus() throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        int beatmapsetID;

        ps = connection.prepareStatement("SELECT customstats_map.set_id " +
                "FROM customstats_map " +
                "JOIN maps ON customstats_map.set_id = maps.set_id " +
                "WHERE customstats_map.status <> maps.status");
        result = ps.executeQuery();
        while(result.next()) {
            beatmapsetID = result.getInt("customstats_map.set_id");
            ps = connection.prepareStatement("UPDATE maps SET status = ? WHERE set_id = ?");
            ps.setInt(1, beatmapsetID);
            ps.executeUpdate();
            setLogger("Fixed Custom beatmap status! ID: " + beatmapsetID, 0);
        }
    }
}
