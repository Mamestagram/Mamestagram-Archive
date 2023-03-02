package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.data.EmbedMessageData.*;

public class RecentPlay {
    public static EmbedBuilder recentData(String dName, int mode) throws SQLException, IOException {

        EmbedBuilder eb = new EmbedBuilder();

        /*User Data*/
        double userACC, userPP;
        int userID, userScore, userCombo, n300, n100, n50, miss;
        String userGrade;

        /*beatmap Data*/

        double mapCircle, mapApproach, mapRating, mapPassRate, mapOverall;
        int mapID, mapRanked, mapLength, mapBPM, mapCombo;
        String mapName, mapDiffName, mapCreator, mapMD5;

        /*SQL Module Set*/

        PreparedStatement ps = null;
        ResultSet result = null;

        /*JSON Module Set*/

        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPIKey + "&h=";
        URL obj = new URL(url);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        /*is data exist?*/

        ps = connection.prepareStatement("select * from users where name = ?");
        ps.setString(1, dName);

        result = ps.executeQuery();

        if(!result.next()) {
            return notUserFoundMessage(dName);
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
        mapRating = Math.round(((root.get(0).get("difficultyrating").asDouble()) * 100) / 100);
        mapPassRate = Math.round(((root.get(0).get("passcount").asDouble() / root.get(0).get("playcount").asDouble()) * 100) / 100);
        mapOverall = root.get(0).get("diff_overall").asDouble();
        mapID = root.get(0).get("beatmapset_id").asInt();
        mapRanked = root.get(0).get("approved").asInt();
        mapLength = root.get(0).get("total_length").asInt(); //need to convert default time
        mapBPM = root.get(0).get("bpm").asInt();
        mapCombo = root.get(0).get("max_combo").asInt();
        mapName = root.get(0).get("artist").asText() + " - " +root.get(0).get("title").asText() + " [";
        mapDiffName = root.get(0).get("version").asText() + "]"; //**mapName + mapDiffName** = 「mahiro - song_name [Hard]」
        mapCreator = root.get(0).get("creator").asText();

        //TODO (to write getting data process)

        return eb;
    }
}
