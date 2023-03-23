package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;
import static net.mamestagram.message.EmbedMessageData.*;

public class RecentPlay {

    public static EmbedBuilder getRecentData(String sName, Member dName, int mode) throws SQLException, IOException {

        double userACC = 0.0, userPP = 0.0, mapCircle, mapApproach, mapRating, mapPassRate, mapOverall;
        int userID, userScore = 0, userMods = 0, userCombo = 0, n300 = 0, n100 = 0, n50 = 0, miss = 0, mapsetID, mapID, mapRanked, mapLength, mapBPM, mapCombo;
        String userGrade = "", mapName, mapDiffName, mapCreator, mapMD5 = "";

        PreparedStatement ps;
        ResultSet result;
        JsonNode root;

        EmbedBuilder eb = new EmbedBuilder();

        ps = connection.prepareStatement("select id from users where name = ?");

        if(sName == null) {
            ps.setString(1, dName.getNickname());
            result = ps.executeQuery();

            if(!result.next()) {
                ps = connection.prepareStatement("select id from users where name = ?");
                ps.setString(1, dName.getUser().getName());
                result = ps.executeQuery();
                if(!result.next()) {
                    return notUserFoundMessage(dName.getUser().getName());
                } else {
                    userID = result.getInt("id");
                }
            } else {
                userID = result.getInt("id");
            }
        } else {
            ps.setString(1, sName);
            result = ps.executeQuery();

            if(!result.next()) {
                return notUserFoundMessage(sName);
            } else {
               userID = result.getInt("id");
            }
        }

        ps = connection.prepareStatement("select map_md5 from scores where userid = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();

        while(result.next()) {
            mapMD5 = result.getString("map_md5");
        }

        root = getMapData(mapMD5);

        mapCircle = root.get(0).get("diff_size").asDouble();
        mapApproach = root.get(0).get("diff_approach").asDouble();
        mapRating = (double)Math.round((root.get(0).get("difficultyrating").asDouble() * 100)) / 100;
        mapPassRate = Math.round((((root.get(0).get("passcount").asDouble() / root.get(0).get("playcount").asDouble()) * 100) * 100) / 100);
        mapOverall = root.get(0).get("diff_overall").asDouble();
        mapsetID = root.get(0).get("beatmapset_id").asInt();
        mapID = root.get(0).get("beatmap_id").asInt();
        mapRanked = root.get(0).get("approved").asInt();
        mapLength = root.get(0).get("total_length").asInt(); //need to convert default time
        mapBPM = root.get(0).get("bpm").asInt();
        mapCombo = root.get(0).get("max_combo").asInt();
        mapName = root.get(0).get("title").asText() + " by " + root.get(0).get("artist").asText();
        mapDiffName = root.get(0).get("version").asText(); //**mapName + mapDiffName** = 「mahiro - song_name [Hard]」
        mapCreator = root.get(0).get("creator").asText();

        ps = connection.prepareStatement("select acc, mods, pp, score, max_combo, n300, n100, n50, nmiss,grade from scores where userid = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();

        while(result.next()) {
            userACC = result.getDouble("acc");
            userMods = result.getInt("mods");
            userPP = result.getDouble("pp");
            userScore = result.getInt("score");
            userCombo = result.getInt("max_combo");
            n300 = result.getInt("n300");
            n100 = result.getInt("n100");
            n50 = result.getInt("n50");
            miss = result.getInt("nmiss");
            userGrade = result.getString("grade");
        }

        eb.setAuthor(mapName + " +" + getModsName(userMods), getWebsiteLink(mode, mapsetID, mapID), "https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.addField("**Performance**", "Rank: ***" + userGrade + "*** **[" + userPP + "pp]**\n" +
                "Score: **" + String.format("%,d", userScore) + "** ▸ **" + userACC + "%**\n" +
                "Combo: **" + String.format("%,d", userCombo) + "x** / " + String.format("%,d", mapCombo) + "x [" + String.format("%,d",n300) + "/" + String.format("%,d",n100) + "/" + String.format("%,d",n50) + "/" + String.format("%,d",miss) + "]\n" +
                "MapRanked: **" + isRanked(mapRanked) + "**", false);
        eb.addField("**Map Detail**", "Name: **" + mapName + "**\n" +
                "Difficulty: **" + mapDiffName + "**\n" +
                "Rating: **★" + mapRating + "** for NM\n" +
                "Passed Rate: **" + mapPassRate + "%**\n" +
                "AR: **" + mapApproach + "** / CS: **" + mapCircle + "** / OD: **" + mapOverall + "** / BPM: **" + mapBPM + "**\n" +
                "Length: **" + getMinSecond(mapLength)[0] + ":" + getMinSecond(mapLength)[1] +"**\n" +
                "Creator: **" + mapCreator + "**", false);
        eb.setColor(getMessageColor(userGrade));
        eb.setImage("https://b.ppy.sh/thumb/" + mapID + "l.jpg?");
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");

        return eb;
    }
}
