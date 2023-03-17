package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;

public class MapNotice {

    private static int BPLAYID;
    private static int NPLAYID;
    private static boolean isFirstLogin = true;

    public static void getMapNotice() throws SQLException, IOException {

        ArrayList<Integer> rID = new ArrayList<>();
        int mapRanked, mapsetID, rMode = 0, userID = 0, rScore = 0, rMods = 0, rCombo = 0, rMaxCombo = 0, rCount300 = 0, rCount100 = 0, rCount50 = 0, rCountMiss = 0;
        double rPP = 0.0, rACC = 0.0;
        String md5 = "", rPlayerName = "" ,rRank = "", mapDifficulty = "", mapName = "", mapArtist = "";

        PreparedStatement ps;
        ResultSet result;
        JsonNode root;

        EmbedBuilder eb = new EmbedBuilder();

        BPLAYID = NPLAYID;

        ps = connection.prepareStatement("select id, userid from scores where not grade = 'F' order by score desc limit 1");
        result = ps.executeQuery();

        while(result.next()) {
            NPLAYID = result.getInt("id");
            userID = result.getInt("userid");
        }

        if(BPLAYID != NPLAYID && !isFirstLogin) {
            ps = connection.prepareStatement("select map_md5, mode from scores where id = ?");
            ps.setInt(1, NPLAYID);
            result = ps.executeQuery();

            while (result.next()) {
                md5 = result.getString("map_md5");
                rMode = result.getInt("mode");
            }

            root = getMapData(md5);
            mapRanked = root.get(0).get("approved").asInt();
            mapsetID = root.get(0).get("beatmapset_id").asInt();
            mapDifficulty = root.get(0).get("version").asText();
            mapName = root.get(0).get("title").asText();
            mapArtist = root.get(0).get("artist").asText();

            ps = connection.prepareStatement("select userid from scores where mode = ? and map_md5 = ? and not grade = 'F' order by score desc");
            ps.setInt(1, rMode);
            ps.setString(2, md5);
            result = ps.executeQuery();

            while(result.next()) {
                rID.add(result.getInt("userid"));
            }

            ps = connection.prepareStatement("select score, pp, acc, max_combo, grade, mods, n300, n100, n50, nmiss from scores where mode = ? and userid = ? and not grade = 'F' order by id limit 1");
            ps.setInt(1, rMode);
            ps.setInt(2, userID);
            result = ps.executeQuery();

            while(result.next()) {
                rRank = result.getString("grade");
                rScore = result.getInt("score");
                rPP = result.getDouble("pp");
                rACC = result.getDouble("acc");
                rCombo = result.getInt("max_combo");
                rMods = result.getInt("mods");
                rCount300 = result.getInt("n300");
                rCount100 = result.getInt("n100");
                rCount50 = result.getInt("n50");
                rCountMiss = result.getInt("nmiss");
            }

            ps = connection.prepareStatement("select name from users where id = ?");
            ps.setInt(1, userID);
            result = ps.executeQuery();

            while(result.next()) {
                rPlayerName = result.getString("name");
            }

            //fieldかく TODO

            eb.setAuthor(mapName + " by " + mapArtist + " +" + getModsName(rMods), "https://osu.ppy.sh/beatmapsets/" + mapsetID, "https://b.ppy.sh/thumb/" + mapsetID + "l.jpg?");
            eb.addField("**Performance**", "Achieved Rank: ***#" + getBeatmapRank(rID, userID) + "***\n" +
                    "Rank: ***" + rRank + "*** **[" + rPP + "pp]**\n" +
                    "Score: **" + rScore + "** ▸ " + rACC + "%**\n" +
                    "Combo: **" + rCombo, false);
        } else {
            isFirstLogin = false;
        }

    }
}
