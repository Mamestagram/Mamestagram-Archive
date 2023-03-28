package net.mamestagram.game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;

public class BanConvertScore {

    private static int nScoreID = 0;
    private static int bScoreID = 0;

    public static void deleteConvertScore() throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        int userMod = 0;

        bScoreID = nScoreID;

        ps = connection.prepareStatement("select id, mods from scores where not grade = 'F' order by id desc limit 1");
        result = ps.executeQuery();

        if(result.next()) {
            nScoreID = result.getInt("id");
            userMod = result.getInt("mods");
        }

        if(bScoreID != nScoreID && (getModsName(userMod).indexOf("4K") != -1 || getModsName(userMod).indexOf("5K") != -1 || getModsName(userMod).indexOf("6K") != -1)) {
            ps = connection.prepareStatement("delete from `private` . `scores` where (`id` = '" + nScoreID + "')");
            ps.executeUpdate();
        }
    }
}
