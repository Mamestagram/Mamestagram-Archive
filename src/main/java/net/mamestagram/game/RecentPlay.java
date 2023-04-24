package net.mamestagram.game;

//TODO エラー直す array out

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
                "Difficulty: **" + getBeatmapDataString(getMD5String(mode, userID)).get(1) + "**\n"  +
                "Rating: **★" + getBeatmapDataDouble(getMD5String(mode, userID)).get(3) + "** for NM\n" +
                "AR: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(0) + "** / CS: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(1) + "** / OD: **" + getBeatmapDataDouble(getMD5String(mode, userID)).get(2)  + "** / BPM: **" + getBeatmapDataInt(getMD5String(mode, userID)).get(2) + "**\n" +
                "Status: **" + isRanked(getBeatmapDataInt(getMD5String(mode, userID)).get(5)) + "** ",false);
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

    private static ArrayList<Double> getBeatmapDataDouble(String md5) throws SQLException{

        ArrayList<Double> arrayData = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;

        ps = connection.prepareStatement("select ar, cs, od, diff, passes, plays from maps where md5 = ?");
        ps.setString(1, md5);
        result = ps.executeQuery();

        if(result.next()) {
            arrayData.add(result.getDouble("ar"));
            arrayData.add(result.getDouble("cs"));
            arrayData.add(result.getDouble("od"));
            arrayData.add((double) Math.round((result.getDouble("diff") * 100)) / 100);
        } else {
            System.out.println("error on double");
        }

        return arrayData;
    }

    //beatmapsetid = 0, beatmapid = 1, bpm = 2, max_combo = 3, length = 4, approved = 5

    private static ArrayList<Integer> getBeatmapDataInt(String md5) throws SQLException {

        ArrayList<Integer> arrayData = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;

        ps = connection.prepareStatement("select set_id, id, bpm, max_combo, total_length, status from maps where md5 = ?");
        ps.setString(1, md5);
        result = ps.executeQuery();

        if(result.next()) {
            arrayData.add(result.getInt("set_id"));
            arrayData.add(result.getInt("id"));
            arrayData.add((result.getInt("bpm")));
            arrayData.add(result.getInt("max_combo"));
            arrayData.add(result.getInt("total_length"));
            arrayData.add(result.getInt("status"));
        } else {
        System.out.println("error on int_beatmap");
    }
        return arrayData;
    }

    //title = 0, verison = 1, creator = 2

    private static ArrayList<String> getBeatmapDataString(String md5) throws SQLException {

        ArrayList<String> arrayData = new ArrayList<>();
        PreparedStatement ps;
        ResultSet result;

        ps = connection.prepareStatement("select artist, title, version, creator from maps where md5 = ?");
        ps.setString(1, md5);
        result = ps.executeQuery();

        if(result.next()) {
            arrayData.add(result.getString("title") + " - " + result.getString("artist"));
            arrayData.add(result.getString("version"));
            arrayData.add(result.getString("creator"));
        } else {
            System.out.println("error on string data");
        }

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
        } else {
            System.out.println("error on userdata_int");
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
        } else {
            System.out.println("error on double_userdata");
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
            System.out.println("error on grade_data");
            return null;
        }
    }
}
