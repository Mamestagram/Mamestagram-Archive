package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
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
        final long CHANNELID = 1081737936401350717L;

        int rMode = 0, userID, rMods = 0;
        String md5 = "";
        PreparedStatement ps;
        ResultSet result;

        EmbedBuilder eb = new EmbedBuilder();

        BPLAYID = NPLAYID;

        NPLAYID = getIDData().get(0);
        userID = getIDData().get(1);

        if(BPLAYID != NPLAYID && !isFirstLogin) {
            ps = connection.prepareStatement("select map_md5, mode, mods from scores where id = ?");
            ps.setInt(1, NPLAYID);
            result = ps.executeQuery();

            while (result.next()) {
                md5 = result.getString("map_md5");
                rMode = result.getInt("mode");
                rMods = result.getInt("mods");
            }

            eb.setAuthor(getBeatmapDataString(md5).get(0) + " by " + getBeatmapDataString(md5).get(1) + " +" + getModsName(rMods), getWebsiteLink(rMode, getBeatmapInt(md5).get(0), getBeatmapInt(md5).get(1)), "https://b.ppy.sh/thumb/" + getBeatmapInt(md5).get(0) + "l.jpg?");
            eb.addField("**Play Record of " + getUserNameFromID(userID) + "**", "Grade: ***" + getGradeString(rMode, userID) + "*** **[" + getUserDataDouble(rMode, userID).get(0) + "pp]**\n" +
                    "Achieved Rank: **#" + String.format("%,d", getBeatmapRank(getMapUserData(rMode, md5), userID)) + "**\n" +
                    "Score: **" + String.format("%,d",getUserDataInt(rMode, userID).get(0)) + " ▸ " + getUserDataDouble(rMode, userID).get(1) + "%**\n" +
                    "Combo: **" + String.format("%,d", getUserDataInt(rMode, userID).get(2)) + "x** / " + String.format("%,d",getBeatmapInt(md5).get(2)) + "x [" + String.format("%,d",getUserDataInt(rMode, userID).get(3)) + "/" + String.format("%,d", getUserDataInt(rMode, userID).get(4)) + "/" + String.format("%,d", getUserDataInt(rMode, userID).get(5)) + "/" + String.format("%,d", getUserDataInt(rMode, userID).get(6)) + "]\n" +
                    "Difficulty: **" + getBeatmapDataString(md5).get(2) + "**", false);
            eb.setFooter("Played in " + getModeName(rMode) + " mode on mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
            eb.setColor(getMessageColor(getGradeString(rMode, userID)));

            jda.getGuildById(GUILDID).getTextChannelById(CHANNELID).sendMessageEmbeds(eb.build()).queue();
        } else {
            isFirstLogin = false;
        }
    }

    //0 = id, 1 = userid

    private static ArrayList<Integer> getIDData() throws SQLException {

        ArrayList<Integer> userData = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;
        String query = ("select id, userid from scores where not grade = 'F' order by id desc limit 1");

        ps = connection.prepareStatement(query);
        result = ps.executeQuery();

        if(result.next()) {
            userData.add(result.getInt("id"));
            userData.add(result.getInt("userid"));
        }

        return userData;
    }

    //0 = beatmapsetID, 1 = beatmapID, 2 = max_combo

    private static ArrayList<Integer> getBeatmapInt(String md5) throws IOException {

        ArrayList<Integer> idArray = new ArrayList<>();
        JsonNode root;

        root = getMapData(md5);

        idArray.add(root.get(0).get("beatmapset_id").asInt());
        idArray.add(root.get(0).get("beatmap_id").asInt());
        idArray.add(root.get(0).get("max_combo").asInt());

        return idArray;
    }

    //0 = title, 1 = artist, 2 = difficulty

    private static ArrayList<String> getBeatmapDataString(String md5) throws IOException {

        ArrayList<String> textArray = new ArrayList<>();
        JsonNode root;

        root = getMapData(md5);

        textArray.add(root.get(0).get("title").asText());
        textArray.add(root.get(0).get("artist").asText());
        textArray.add(root.get(0).get("version").asText());

        return textArray;
    }

    private static ArrayList<Integer> getMapUserData(int playMode, String md5) throws SQLException {

        ArrayList<Integer> userID = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;
        String query = ("select userid, score from scores where mode = ? and map_md5 = ? and not grade = 'F' order by score desc");

        ps = connection.prepareStatement(query);
        ps.setInt(1, playMode);
        ps.setString(2, md5);
        result = ps.executeQuery();

        while(result.next()) {
            userID.add(result.getInt("userid"));
        }

        return userID;
    }

    private static String getGradeString(int playMode, int userID) throws SQLException {

        String query = ("select grade from scores where mode = ? and userid = ? and not grade = 'F' order by id desc limit 1");
        PreparedStatement ps;
        ResultSet result;

        ps = connection.prepareStatement(query);
        ps.setInt(1, playMode);
        ps.setInt(2, userID);
        result = ps.executeQuery();

        if(result.next()) {
            return result.getString("grade");
        } else {
            return null;
        }
    }

    //0 = score, 1 = mods, 2 = max_combo, 3 = n300, 4 = n100, 5 = n50, 6, miss

    private static ArrayList<Integer> getUserDataInt(int playMode, int userID) throws SQLException {

        ArrayList<Integer> userData = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;
        String query = ("select score, max_combo, mods, n300, n100, n50, nmiss from scores where mode = ? and userid = ? and not grade = 'F' order by id desc limit 1");

        ps = connection.prepareStatement(query);
        ps.setInt(1, playMode);
        ps.setInt(2, userID);

        result = ps.executeQuery();

        if(result.next()) {
            userData.add(result.getInt("score"));
            userData.add(result.getInt("mods"));
            userData.add(result.getInt("max_combo"));
            userData.add(result.getInt("n300"));
            userData.add(result.getInt("n100"));
            userData.add(result.getInt("n50"));
            userData.add(result.getInt("nmiss"));
        }

        return userData;
    }

    //0 = pp, 1 = acc

    private static ArrayList<Double> getUserDataDouble(int playMode, int userID) throws SQLException {

        ArrayList<Double> userData = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;
        String query = ("select pp, acc from scores where mode = ? and userid = ? and not grade = 'F' order by id desc limit 1");

        ps = connection.prepareStatement(query);
        ps.setInt(1, playMode);
        ps.setInt(2, userID);
        result = ps.executeQuery();

        if(result.next()) {
            userData.add(result.getDouble("pp"));
            userData.add(result.getDouble("acc"));
        }

        return userData;
    }

    private static String getUserNameFromID(int userID) throws SQLException{

        PreparedStatement ps;
        ResultSet result;
        String query = ("select name from users where id = ?");

        ps = connection.prepareStatement(query);
        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            return result.getString("name");
        } else {
            return null;
        }
    }
}
