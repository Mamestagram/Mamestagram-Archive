package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;
import static net.mamestagram.message.EmbedMessageData.*;

public class RecentPlay {
    private static int[] calcMinSecond(int number) {
        int[] time = new int[2];
        if(number > 59) {
            time[0] = number / 60;
            time[1] = number % 60;
            return time;
        } else {
            time[1] = number;
            return time;
        }
    }

    public static String getMods(int n) {

        ArrayList<String> mod = new ArrayList<String>();
        final String[] mods = {"NF", "EZ", "", "HD", "HR", "SD", "DT", "", "HT", "NC", "FL", "", "SO", "PF"};
        String rMods = "";

        if (n >= 536870912) {
            mod.add("v2");
            n -= 536870912;
        }

        for (int i = 14; i >= 0; i--) {
            if (i != 2 && i != 11 && n >= Math.pow(2, i)) {
                switch (i) {
                    case 14:
                        n -= Math.pow(2, 5);
                        break;
                    case 9:
                        n -= Math.pow(2, 6);
                        break;
                    case 13:
                    case 7:
                        n -= Math.pow(2, i);
                        continue;
                }
                mod.add(mods[i]);
                n -= Math.pow(2, i);
            }
        }

        for (int i = 0; i < mod.size(); i++) {
            rMods += mod.get(i);
        }

        if(rMods != "") {
            return rMods;
        } else {
            return "NM";
        }

    }

    public static EmbedBuilder recentData(Member dName, int mode) throws SQLException, IOException {

        EmbedBuilder eb = new EmbedBuilder();

        double userACC = 0.0, userPP = 0.0;
        int userID, userScore = 0, userMods = 0, userCombo = 0, n300 = 0, n100 = 0, n50 = 0, miss = 0;
        String userGrade = "";

        double mapCircle, mapApproach, mapRating, mapPassRate, mapOverall;
        int mapID, mapRanked, mapLength, mapBPM, mapCombo;
        String mapName, mapDiffName, mapCreator, mapMD5 = "";

        PreparedStatement ps;
        ResultSet result;

        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPIKey + "&h=";
        URL obj;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        ps = connection.prepareStatement("select id from users where name = ?");
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

        ps = connection.prepareStatement("select map_md5 from scores where userid = ? and mode = " + mode);
        ps.setInt(1, userID);

        result = ps.executeQuery();

        while(result.next()) {
            mapMD5 = result.getString("map_md5");
        }

        url += mapMD5;
        obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        root = mapper.readTree(response.toString());

        mapCircle = root.get(0).get("diff_size").asDouble();
        mapApproach = root.get(0).get("diff_approach").asDouble();
        mapRating = (double)Math.round((root.get(0).get("difficultyrating").asDouble() * 100)) / 100;
        mapPassRate = Math.round((((root.get(0).get("passcount").asDouble() / root.get(0).get("playcount").asDouble()) * 100) * 100) / 100);
        mapOverall = root.get(0).get("diff_overall").asDouble();
        mapID = root.get(0).get("beatmapset_id").asInt();
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

        eb.setAuthor(mapName + " +" + getMods(userMods), "https://osu.ppy.sh/beatmapsets/" + mapID, "https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.addField("**Performance**", "Rank: ***" + userGrade + "*** **[" + userPP + "pp]**\n" +
                "Score: **" + String.format("%,d", userScore) + "** ▸ **" + userACC + "%**\n" +
                "Combo: **" + String.format("%,d", userCombo) + "x** / " + String.format("%,d", mapCombo) + "x [" + String.format("%,d",n300) + "/" + String.format("%,d",n100) + "/" + String.format("%,d",n50) + "/" + String.format("%,d",miss) + "]", false);
        eb.addField("**Map Detail**", "Name: **" + mapName + "**\n" +
                "Difficulty: **" + mapDiffName + "**\n" +
                "Rating: **★" + mapRating + "** for NM\n" +
                "Passed Rate: **" + mapPassRate + "%**\n" +
                "AR: **" + mapApproach + "** / CS: **" + mapCircle + "** / OD: **" + mapOverall + "** / BPM: **" + mapBPM + "**\n" +
                "Length: **" + calcMinSecond(mapLength)[0] + ":" + calcMinSecond(mapLength)[1] +"**\n" +
                "Creator: **" + mapCreator + "**", false);

        switch (userGrade) {
            case "XH":
            case "SH":
                eb.setColor(Color.WHITE);
                break;
            case "X":
            case "S":
                eb.setColor(Color.YELLOW);
                break;
            case "A":
                eb.setColor(Color.GREEN);
                break;
            case "B":
                eb.setColor(Color.CYAN);
                break;
            case "C":
                eb.setColor(Color.PINK);
                break;
            default:
                eb.setColor(Color.RED);
                break;
        }

        eb.setImage("https://b.ppy.sh/thumb/" + mapID + "l.jpg?");
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");

        return eb;
    }
}
