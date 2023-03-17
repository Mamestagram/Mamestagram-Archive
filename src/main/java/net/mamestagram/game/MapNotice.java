package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;

public class MapNotice {

    private static int BPLAYID = 0;
    private static int NPLAYID = 0;
    private static boolean isFirstLogin = true;

    public static void getMapNotice() throws SQLException, IOException {

        final long GUILDID = 944248031136587796L;
        final long CHANNELID = 1081737936401350717L; //beta

        ArrayList<Integer> rID = new ArrayList<>();
        int mapsetID, rMode = 0, userID = 0, rScore = 0, rMods = 0, rCombo = 0, rMaxCombo = 0, rCount300 = 0, rCount100 = 0, rCount50 = 0, rCountMiss = 0;
        double rPP = 0.0, rACC = 0.0;
        String md5 = "", rPlayerName = "" ,rRank = "", mapDifficulty = "", mapName = "", mapArtist = "";

        PreparedStatement ps;
        ResultSet result;
        JsonNode root;

        EmbedBuilder eb = new EmbedBuilder();

        BPLAYID = NPLAYID;

        ps = connection.prepareStatement("select id, userid, max_combo from scores where not grade = 'F' order by id desc limit 1");
        result = ps.executeQuery();

        while(result.next()) {
            NPLAYID = result.getInt("id");
            userID = result.getInt("userid");
            rCombo = result.getInt("max_combo");
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

            mapsetID = root.get(0).get("beatmapset_id").asInt();
            mapDifficulty = root.get(0).get("version").asText();
            mapName = root.get(0).get("title").asText();
            mapArtist = root.get(0).get("artist").asText();
            rMaxCombo = root.get(0).get("max_combo").asInt();

            ps = connection.prepareStatement("select userid from scores where mode = ? and map_md5 = ? and not grade = 'F' order by score desc");
            ps.setInt(1, rMode);
            ps.setString(2, md5);
            result = ps.executeQuery();

            while(result.next()) {
                rID.add(result.getInt("userid"));
            }

            ps = connection.prepareStatement("select score, pp, acc, max_combo, grade, mods, n300, n100, n50, nmiss from scores where mode = ? and userid = ? and not grade = 'F' order by id desc limit 1");
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

            eb.setAuthor(mapName + " by " + mapArtist + " +" + getModsName(rMods), "https://osu.ppy.sh/beatmapsets/" + mapsetID, "https://b.ppy.sh/thumb/" + mapsetID + "l.jpg?");
            eb.addField("**" + rPlayerName+ "'s Play Record**", "Rank: ***" + rRank + "*** **[" + rPP + "pp]**\n" +
                    "Achieved Rank: ***#" + String.format("%,d", getBeatmapRank(rID, userID)) + "***\n" +
                    "Score: **" + String.format("%,d",rScore) + " ▸ " + rACC + "%**\n" +
                    "Combo: **" + String.format("%,d",rCombo) + "x** / " + String.format("%,d",rMaxCombo) + "x [" + String.format("%,d",rCount300) + "/" + String.format("%,d",rCount100) + "/" + String.format("%,d",rCount50) + "/" + String.format("%,d",rCountMiss) + "]\n" +
                    "Difficulty: **" + mapDifficulty + "**", false);
            eb.setFooter("Played by " + rPlayerName + " on mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
            eb.setColor(getMessageColor(rRank));

            jda.getGuildById(GUILDID).getTextChannelById(CHANNELID).sendMessageEmbeds(eb.build()).queue();
        } else {
            isFirstLogin = false;
        }

    }
}
