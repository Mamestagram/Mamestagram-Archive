package net.mamestagram.Module;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.connection;
import static net.mamestagram.Module.OSU.*;

public class DataBase {

    public static int getGlobalRank(int userID, int playMode) throws SQLException {
        PreparedStatement ps;
        ResultSet result;
        String query = ("SELECT COUNT(*) + 1 AS 'ranking' " +
                "FROM stats " +
                "WHERE pp > (" +
                "SELECT pp " +
                "FROM stats " +
                "WHERE id = ? " +
                "AND mode = ? ) " +
                "AND mode = ?");

        ps = connection.prepareStatement(query);

        ps.setInt(1, userID);
        ps.setInt(2, playMode);
        ps.setInt(3, playMode);

        result = ps.executeQuery();

        if(result.next()) {
            return result.getInt("ranking");
        } else {
            return 0;
        }
    }

    public static int getCountryRank(int userID, int playMode) throws SQLException {
        PreparedStatement ps;
        ResultSet result;
        String query = ("SELECT COUNT(*) + 1 AS 'cranking' " +
                "FROM stats " +
                "JOIN users " +
                "ON stats.id = users.id " +
                "WHERE pp > ( " +
                "    SELECT pp " +
                "    FROM stats " +
                "    WHERE id = ? " +
                "    AND mode = ? " +
                "    AND country = ( " +
                "        SELECT country " +
                "        FROM users " +
                "        WHERE id = ? " +
                "        AND mode = ? " +
                "    ) " +
                ") " +
                "AND mode = ?");

        ps = connection.prepareStatement(query);

        ps.setInt(1, userID);
        ps.setInt(2, playMode);
        ps.setInt(3, userID);
        ps.setInt(4, playMode);
        ps.setInt(5, playMode);

        result = ps.executeQuery();

        if(result.next()) {
            return result.getInt("cranking");
        } else {
            return 0;
        }
    }

    public static int getNumberOfMapsWonByUser(int userid, int mode) throws SQLException {
        PreparedStatement ps;
        ResultSet result;
        ArrayList<Integer> mapID = new ArrayList<>();
        ArrayList<String> mapMD5 = new ArrayList<>();
        int mapCount = 0, count = 0;
        int rankCount = 0;

        ps = connection.prepareStatement("select id, map_md5 from scores where userid = " + userid + " and not grade = 'F' and mode = " + mode);
        result = ps.executeQuery();

        while(result.next()) {
            mapID.add(result.getInt("id"));
            mapMD5.add(result.getString("map_md5"));
        }

        ps = connection.prepareStatement("select COUNT(*) from scores");
        result = ps.executeQuery();

        if (result.next()) {
            mapCount = result.getInt("COUNT(*)");
        }

        for(int i = 1; i <= mapCount; i++) {

            ArrayList<Integer> id = new ArrayList<>();

            try {
                if (i == mapID.get(count)) {
                    ps = connection.prepareStatement("select userid from scores where map_md5 = ? and not grade = 'F' and mode = ? order by score desc");
                    ps.setString(1, mapMD5.get(count));
                    ps.setInt(2, mode);
                    result = ps.executeQuery();

                    while (result.next()) {
                        id.add(result.getInt("userid"));
                    }

                    if (getBeatmapRank(id, userid) == 1) {
                        rankCount++;
                    }
                    count++;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        return rankCount;
    }

}
