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

import static net.mamestagram.Main.*;
import static net.mamestagram.data.EmbedMessageData.*;

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

    public static EmbedBuilder recentData(Member dName, int mode) throws SQLException, IOException {

        EmbedBuilder eb = new EmbedBuilder();

        /*User Data*/
        double userACC = 0.0, userPP = 0.0;
        int userID = 0, userScore = 0, userCombo = 0, n300 = 0, n100 = 0, n50 = 0, miss = 0;
        int[] time = new int[2];
        String userGrade = "";

        /*beatmap Data*/

        double mapCircle = 0, mapApproach= 0, mapRating= 0, mapPassRate= 0, mapOverall= 0;
        int mapID, mapRanked, mapLength, mapBPM, mapCombo;
        String mapName = "", mapDiffName = "", mapCreator = "", mapMD5 = "";

        /*SQL Module Set*/

        PreparedStatement ps = null;
        ResultSet result = null;

        /*JSON Module Set*/

        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPIKey + "&h=";
        URL obj;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        /*is data exist?*/

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

        /*Get map_md5*/

        ps = connection.prepareStatement("select map_md5 from scores where userid = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            mapMD5 = result.getString("map_md5");
        }

        /*Load Map Data*/
        
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

        /*Input Got Data*/

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
        mapName = root.get(0).get("title_unicode").asText() + " by " + root.get(0).get("artist_unicode").asText();
        mapDiffName = root.get(0).get("version").asText(); //**mapName + mapDiffName** = 「mahiro - song_name [Hard]」
        mapCreator = root.get(0).get("creator").asText();

        /*user acc*/

        ps = connection.prepareStatement("select acc from scores where userid = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userACC = result.getDouble("acc");
        }

        /*user pp*/

        ps = connection.prepareStatement("select pp from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userPP = result.getDouble("pp");
        }

        /*user score*/

        ps = connection.prepareStatement("select score from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userScore = result.getInt("score");
        }

        /*max_combo*/

        ps = connection.prepareStatement("select max_combo from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userCombo = result.getInt("max_combo");
        }

        /*n300*/

        ps = connection.prepareStatement("select n300 from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            n300 = result.getInt("n300");
        }

        /*n100*/

        ps = connection.prepareStatement("select n100 from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            n100 = result.getInt("n100");
        }

        /*n50*/

        ps = connection.prepareStatement("select n50 from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            n50 = result.getInt("n50");
        }

        /*miss*/

        ps = connection.prepareStatement("select nmiss from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            miss = result.getInt("nmiss");
        }

        /*score grade*/

        ps = connection.prepareStatement("select grade from scores where userid = ? and mode =" + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userGrade = result.getString("grade");
        }

        eb.setAuthor(mapName + " [" + mapDiffName + "] [★" + mapRating + "]", "https://osu.ppy.sh/beatmapsets/" + mapID, "https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.addField("**Performance**", "Rank: ***" + userGrade + "*** **[" + userPP + "pp]**\n" +
                "Score: **" + String.format("%,d", userScore) + "** ▸ **" + userACC + "%**\n" +
                "Combo: **" + String.format("%,d", userCombo) + "x** / " + String.format("%,d", mapCombo) + "x [" + String.format("%,d",n300) + "/" + String.format("%,d",n100) + "/" + String.format("%,d",n50) + "/" + String.format("%,d",miss) + "]", false);
        eb.addField("**Map Detail**", "Name: **" + mapName + "**\n" +
                "Rating: **★" + mapRating + "**\n" +
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
