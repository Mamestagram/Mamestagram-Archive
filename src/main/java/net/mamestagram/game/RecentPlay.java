package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;
import static net.mamestagram.message.EmbedMessageData.*;

public class RecentPlay {

    public static EmbedBuilder getRecentData(String sName, Member dName, int mode) throws SQLException, IOException {

        int userID;

        PreparedStatement ps;
        ResultSet result;

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

        eb.setAuthor(getBeatmapDataString(getMD5String(mode, userID)).get(0) + " +" + getModsName(getUserDataInt(mode, userID).get(1)), getWebsiteLink(mode, getBeatmapDataInt(getMD5String(mode, userID)).get(0), getBeatmapDataInt(getMD5String(mode, userID)).get(1)), "https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.addField("**:chart_with_upwards_trend: Performance**", "Grade: ***" + getUserDataString(mode, userID) + "*** **[" + getUserDataDouble(mode, userID).get(1) + "pp]**\n" +
                "Score: **" + String.format("%,d", getUserDataInt(mode, userID).get(0)) + "** ▸ **" + getUserDataDouble(mode, userID).get(0) + "%**\n" +
                "Combo: **" + String.format("%,d", getUserDataInt(mode, userID).get(2)) + "x** / " + String.format("%,d", getBeatmapDataInt(getMD5String(mode, userID)).get(3)) + "x [" + String.format("%,d",getUserDataInt(mode, userID).get(7)) + "/" +  String.format("%,d",getUserDataInt(mode, userID).get(3)) + "/" + String.format("%,d",getUserDataInt(mode, userID).get(8)) + "/" + String.format("%,d",getUserDataInt(mode, userID).get(4)) + "/" + String.format("%,d",getUserDataInt(mode, userID).get(5)) + "/" + String.format("%,d",getUserDataInt(mode, userID).get(6)) + "]", false);

        eb.addField("**:notepad_spiral: Map Detail**", "Name: **" + getBeatmapDataString(getMD5String(mode, userID)).get(0) + "**\n" +
                "Difficulty: **" + getBeatmapDataString(getMD5String(mode, userID)).get(1) + "**\n" +
                "Rating: **★" + getBeatmapDataDouble(getMD5String(mode, userID)).get(3) + "** for NM\n" +
                "Passed Rate: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(4) + "%**\n" +
                "AR: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(0) + "** / CS: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(1) + "** / OD: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(2)  + "** / BPM: **" + getBeatmapDataInt(getMD5String(mode, userID)).get(2) + "**\n" +
                "MapRanked: **" + isRanked(getBeatmapDataInt(getMD5String(mode, userID)).get(5)) + "**",false);
        eb.setColor(getMessageColor(getUserDataString(mode, userID)));
        eb.setImage("https://assets.ppy.sh/beatmaps/" + getBeatmapDataInt(getMD5String(mode, userID)).get(0) + "/covers/cover.jpg?");
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");

        return eb;
    }

    private static String getMD5String(int playMode, int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        String query = ("select map_md5 from scores where userid = ? and mode = " + playMode + " order by id desc limit 1");

        ps = connection.prepareStatement(query);
        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            return result.getString("map_md5");
        } else {
            return null;
        }
    }

    //0 = AR, 1 = CS, 2 = OD, 3 = MapRating, 4 = PassRate

    private static ArrayList<Double> getBeatmapDataDouble(String md5) throws IOException{

        ArrayList<Double> arrayData = new ArrayList<>();
        JsonNode root;

        root = getMapData(md5);

        arrayData.add(root.get(0).get("diff_approach").asDouble());
        arrayData.add(root.get(0).get("diff_size").asDouble());
        arrayData.add(root.get(0).get("diff_overall").asDouble());
        arrayData.add((double)Math.round((root.get(0).get("difficultyrating").asDouble() * 100)) / 100);
        arrayData.add((double) Math.round((((root.get(0).get("passcount").asDouble() / root.get(0).get("playcount").asDouble()) * 100) * 100) / 100));

        return arrayData;
    }

    //beatmapsetid = 0, beatmapid = 1, bpm = 2, max_combo = 3, length = 4, approved = 5

    private static ArrayList<Integer> getBeatmapDataInt(String md5) throws IOException {

        ArrayList<Integer> arrayData = new ArrayList<>();
        JsonNode root;

        root = getMapData(md5);

        arrayData.add(root.get(0).get("beatmapset_id").asInt());
        arrayData.add(root.get(0).get("beatmap_id").asInt());
        arrayData.add(root.get(0).get("bpm").asInt());
        arrayData.add(root.get(0).get("max_combo").asInt());
        arrayData.add(root.get(0).get("total_length").asInt());
        arrayData.add(root.get(0).get("approved").asInt());

        return arrayData;
    }

    //title = 0, verison = 1, creator = 2

    private static ArrayList<String> getBeatmapDataString(String md5) throws IOException {

        ArrayList<String> arrayData = new ArrayList<>();
        JsonNode root;

        root = getMapData(md5);

        arrayData.add(root.get(0).get("title").asText() + " by " + root.get(0).get("artist").asText());
        arrayData.add(root.get(0).get("version").asText());
        arrayData.add(root.get(0).get("creator").asText());

        return arrayData;
    }

    //Score = 0, mods = 1, max_combo = 2, n300 = 3, n100 = 4, n50 = 5, nmiss = 6

    private static ArrayList<Integer> getUserDataInt(int playMode, int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        ArrayList<Integer> arrayData = new ArrayList<>();
        String query = ("select mods, score, max_combo, n300, n100, n50, nmiss, ngeki, nkatu from scores where userid = ? and mode = " + playMode + " order by id desc limit 1");

        ps = connection.prepareStatement(query);

        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            arrayData.add(result.getInt("score"));
            arrayData.add(result.getInt("mods"));
            arrayData.add(result.getInt("max_combo"));
            arrayData.add(result.getInt("n300"));
            arrayData.add(result.getInt("n100"));
            arrayData.add(result.getInt("n50"));
            arrayData.add(result.getInt("nmiss"));
            arrayData.add(result.getInt("ngeki"));
            arrayData.add(result.getInt("nkatu"));
        }

        return arrayData;
    }

    //acc = 0, pp = 1

    private static ArrayList<Double> getUserDataDouble(int playMode, int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        ArrayList<Double> arrayData = new ArrayList<>();
        String query = ("select acc, pp from scores where userid = ? and mode = " + playMode + " order by id desc limit 1");

        ps = connection.prepareStatement(query);
        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            arrayData.add(result.getDouble("acc"));
            arrayData.add(result.getDouble("pp"));
        }

        return arrayData;
    }

    private static String getUserDataString(int playMode, int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        String query = ("select grade from scores where userid = ? and mode = " + playMode + " order by id desc limit 1");

        ps = connection.prepareStatement(query);

        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            return result.getString("grade");
        } else {
            return null;
        }
    }
}
